package com.example.gestionacademica.application.Services.impl;

import com.example.gestionacademica.application.Exceptions.BusinessException;
import com.example.gestionacademica.application.Services.AsistenciaServices;
import com.example.gestionacademica.application.Services.EstudianteServices;
import com.example.gestionacademica.application.Services.GrupoServices;
import com.example.gestionacademica.application.Services.MateriaServices;
import com.example.gestionacademica.application.Services.NotaServices;
import com.example.gestionacademica.application.Services.ReporteServices;
import com.example.gestionacademica.domain.models.AsignacionMateriaProfesor;
import com.example.gestionacademica.domain.models.Estudiante;
import com.example.gestionacademica.domain.models.Grupo;
import com.example.gestionacademica.domain.models.Inscripcion;
import com.example.gestionacademica.domain.models.Materia;
import com.example.gestionacademica.domain.models.Nota;
import com.example.gestionacademica.domain.repository.AsignacionMateriaProfesorRepository;
import com.example.gestionacademica.domain.repository.InscripcionRepository;
import com.example.gestionacademica.domain.repository.MovimientoNotaRepository;
import com.example.gestionacademica.domain.repository.NivelMateriaRepository;
import com.example.gestionacademica.domain.repository.NotaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Consultas de solo lectura que combinan información de varias tablas
@Service
@Transactional(readOnly = true)
public class ReporteServicesImpl implements ReporteServices {

    private static final double PROMEDIO_MINIMO = 3.0;
    private static final double ASISTENCIA_MINIMA = 80.0;

    private final EstudianteServices estudianteServices;
    private final GrupoServices grupoServices;
    private final MateriaServices materiaServices;
    private final NotaServices notaServices;
    private final AsistenciaServices asistenciaServices;
    private final InscripcionRepository inscripcionRepository;
    private final NotaRepository notaRepository;
    private final MovimientoNotaRepository movimientoNotaRepository;
    private final AsignacionMateriaProfesorRepository asignacionMateriaProfesorRepository;
    private final NivelMateriaRepository nivelMateriaRepository;

    public ReporteServicesImpl(EstudianteServices estudianteServices,
                               GrupoServices grupoServices,
                               MateriaServices materiaServices,
                               NotaServices notaServices,
                               AsistenciaServices asistenciaServices,
                               InscripcionRepository inscripcionRepository,
                               NotaRepository notaRepository,
                               MovimientoNotaRepository movimientoNotaRepository,
                               AsignacionMateriaProfesorRepository asignacionMateriaProfesorRepository,
                               NivelMateriaRepository nivelMateriaRepository) {
        this.estudianteServices = estudianteServices;
        this.grupoServices = grupoServices;
        this.materiaServices = materiaServices;
        this.notaServices = notaServices;
        this.asistenciaServices = asistenciaServices;
        this.inscripcionRepository = inscripcionRepository;
        this.notaRepository = notaRepository;
        this.movimientoNotaRepository = movimientoNotaRepository;
        this.asignacionMateriaProfesorRepository = asignacionMateriaProfesorRepository;
        this.nivelMateriaRepository = nivelMateriaRepository;
    }

    // Todo lo del estudiante en una sola respuesta: datos, grupos, notas, promedio y asistencia
    @Override
    public Map<String, Object> boletin(String idEstudiante) {
        Estudiante estudiante = estudianteServices.obtenerPorId(idEstudiante);

        List<Map<String, Object>> grupos = new ArrayList<>();
        for (Inscripcion inscripcion : inscripcionRepository.findByEstudianteIdEstudiante(idEstudiante)) {
            Map<String, Object> grupo = new LinkedHashMap<>();
            grupo.put("idGrupo", inscripcion.getGrupo().getIdGrupo());
            grupo.put("nombreGrupo", inscripcion.getGrupo().getNombreGrupo());
            grupo.put("anio", inscripcion.getAnioInscripcion());
            grupos.add(grupo);
        }

        List<Map<String, Object>> materias = new ArrayList<>();
        for (Nota nota : notaRepository.findByEstudianteIdEstudiante(idEstudiante)) {
            Map<String, Object> materia = new LinkedHashMap<>();
            materia.put("idNota", nota.getIdNota());
            materia.put("idMateria", nota.getMateria().getIdMateria());
            materia.put("nombreMateria", nota.getMateria().getNombreMateria());
            materia.put("definitiva", nota.getDefinitiva());
            materia.put("estado", estadoNota(nota));
            materia.put("calificaciones", movimientoNotaRepository.findByNotaIdNota(nota.getIdNota()).size());
            materias.add(materia);
        }

        Map<String, Object> boletin = new LinkedHashMap<>();
        boletin.put("idEstudiante", estudiante.getIdEstudiante());
        boletin.put("nombreEstudiante", estudiante.getNombreEstudiante());
        boletin.put("estado", estudiante.getEstado());
        boletin.put("grupos", grupos);
        boletin.put("materias", materias);
        boletin.put("resumenNotas", notaServices.calcularPromedio(idEstudiante));
        boletin.put("asistencia", asistenciaServices.resumenPorEstudiante(idEstudiante));
        return boletin;
    }

    // Inscritos del grupo con promedio menor a 3.0 o asistencia (en ese grupo) menor al 80 %
    @Override
    public Map<String, Object> estudiantesEnRiesgo(String idGrupo, Integer anio) {
        grupoServices.obtenerPorId(idGrupo); // lanza 404 si el grupo no existe
        List<Inscripcion> inscripciones = inscripcionRepository.findByGrupoIdGrupoAndAnioInscripcion(idGrupo, anio);

        List<Map<String, Object>> enRiesgo = new ArrayList<>();
        for (Inscripcion inscripcion : inscripciones) {
            Estudiante estudiante = inscripcion.getEstudiante();
            String idEstudiante = estudiante.getIdEstudiante();
            Double promedio = (Double) notaServices.calcularPromedio(idEstudiante).get("promedio");
            Double asistencia = (Double) asistenciaServices
                    .resumenPorEstudianteYGrupo(idEstudiante, idGrupo).get("porcentajeAsistencia");

            List<String> motivos = new ArrayList<>();
            if (promedio != null && promedio < PROMEDIO_MINIMO) {
                motivos.add("Promedio bajo (" + promedio + ")");
            }
            if (asistencia != null && asistencia < ASISTENCIA_MINIMA) {
                motivos.add("Asistencia baja (" + asistencia + " %)");
            }

            if (!motivos.isEmpty()) {
                Map<String, Object> fila = new LinkedHashMap<>();
                fila.put("idEstudiante", idEstudiante);
                fila.put("nombreEstudiante", estudiante.getNombreEstudiante());
                fila.put("promedio", promedio);
                fila.put("porcentajeAsistencia", asistencia);
                fila.put("motivos", motivos);
                enRiesgo.add(fila);
            }
        }

        Map<String, Object> reporte = new LinkedHashMap<>();
        reporte.put("idGrupo", idGrupo);
        reporte.put("anio", anio);
        reporte.put("totalInscritos", inscripciones.size());
        reporte.put("totalEnRiesgo", enRiesgo.size());
        reporte.put("estudiantes", enRiesgo);
        return reporte;
    }

    // Lo que vería el profesor: todos los inscritos del grupo con su nota en la materia
    @Override
    public Map<String, Object> planillaNotas(String idGrupo, String idMateria, Integer anio) {
        Grupo grupo = grupoServices.obtenerPorId(idGrupo);
        Materia materia = materiaServices.obtenerPorId(idMateria);
        String idNivel = grupo.getNivel().getIdNivel();
        if (!nivelMateriaRepository.existsByNivelIdNivelAndMateriaIdMateria(idNivel, idMateria)) {
            throw new BusinessException("MATERIA_FUERA_DEL_NIVEL", "La materia " + idMateria + " no pertenece al nivel " + idNivel
                    + " del grupo " + idGrupo);
        }

        String profesor = "Sin asignar";
        List<AsignacionMateriaProfesor> asignaciones =
                asignacionMateriaProfesorRepository.findByGrupoIdGrupoAndMateriaIdMateria(idGrupo, idMateria);
        if (!asignaciones.isEmpty()) {
            profesor = asignaciones.get(0).getProfesor().getNombreProfesor();
        }

        List<Map<String, Object>> estudiantes = new ArrayList<>();
        int aprobados = 0;
        int reprobados = 0;
        int calificados = 0;
        double suma = 0;
        for (Inscripcion inscripcion : inscripcionRepository.findByGrupoIdGrupoAndAnioInscripcion(idGrupo, anio)) {
            Estudiante estudiante = inscripcion.getEstudiante();
            Nota nota = notaMasReciente(estudiante.getIdEstudiante(), idMateria);

            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("idEstudiante", estudiante.getIdEstudiante());
            fila.put("nombreEstudiante", estudiante.getNombreEstudiante());
            if (nota == null) {
                fila.put("idNota", null);
                fila.put("definitiva", null);
                fila.put("calificaciones", 0);
            } else {
                fila.put("idNota", nota.getIdNota());
                fila.put("definitiva", nota.getDefinitiva());
                fila.put("calificaciones", movimientoNotaRepository.findByNotaIdNota(nota.getIdNota()).size());
                if (nota.getDefinitiva() != null) {
                    calificados++;
                    suma += nota.getDefinitiva();
                    if (nota.getAprobada() != null && nota.getAprobada()) {
                        aprobados++;
                    } else {
                        reprobados++;
                    }
                }
            }
            fila.put("estado", estadoNota(nota));
            estudiantes.add(fila);
        }

        Map<String, Object> planilla = new LinkedHashMap<>();
        planilla.put("idGrupo", idGrupo);
        planilla.put("nombreGrupo", grupo.getNombreGrupo());
        planilla.put("idMateria", idMateria);
        planilla.put("nombreMateria", materia.getNombreMateria());
        planilla.put("profesor", profesor);
        planilla.put("anio", anio);
        planilla.put("aprobados", aprobados);
        planilla.put("reprobados", reprobados);
        if (calificados == 0) {
            planilla.put("promedioGrupo", null);
        } else {
            planilla.put("promedioGrupo", Math.round(suma / calificados * 100.0) / 100.0);
        }
        planilla.put("estudiantes", estudiantes);
        return planilla;
    }

    // Si el estudiante repitió la materia, se toma la última nota registrada
    private Nota notaMasReciente(String idEstudiante, String idMateria) {
        Nota masReciente = null;
        for (Nota nota : notaRepository.findByEstudianteIdEstudianteAndMateriaIdMateria(idEstudiante, idMateria)) {
            if (masReciente == null || nota.getIdNota() > masReciente.getIdNota()) {
                masReciente = nota;
            }
        }
        return masReciente;
    }

    private String estadoNota(Nota nota) {
        if (nota == null) {
            return "Sin nota";
        }
        if (nota.getAprobada() == null) {
            return "En curso";
        }
        if (nota.getAprobada()) {
            return "Aprobada";
        }
        return "Reprobada";
    }
}
