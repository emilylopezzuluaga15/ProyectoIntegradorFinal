package com.example.gestionacademica.application.Services.impl;

import com.example.gestionacademica.application.Exceptions.AsistenciaNotFoundException;
import com.example.gestionacademica.application.Exceptions.BusinessException;
import com.example.gestionacademica.application.Exceptions.EstudianteNotFoundException;
import com.example.gestionacademica.application.Services.AsistenciaServices;
import com.example.gestionacademica.domain.models.Asistencia;
import com.example.gestionacademica.domain.repository.AsistenciaRepository;
import com.example.gestionacademica.domain.repository.EstudianteRepository;
import com.example.gestionacademica.domain.repository.InscripcionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AsistenciaServicesImpl implements AsistenciaServices {

    private final AsistenciaRepository asistenciaRepository;
    private final InscripcionRepository inscripcionRepository;
    private final EstudianteRepository estudianteRepository;

    public AsistenciaServicesImpl(AsistenciaRepository asistenciaRepository,
                                  InscripcionRepository inscripcionRepository,
                                  EstudianteRepository estudianteRepository) {
        this.asistenciaRepository = asistenciaRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.estudianteRepository = estudianteRepository;
    }

    @Override
    public Asistencia crear(Asistencia asistencia) {
        validar(asistencia);
        validarJustificacion(asistencia);
        return asistenciaRepository.save(asistencia);
    }

    @Override
    public Asistencia obtenerPorId(Long id) {
        return asistenciaRepository.findById(id)
                .orElseThrow(() -> new AsistenciaNotFoundException("Asistencia no encontrada con id: " + id));
    }

    @Override
    public List<Asistencia> obtenerTodos() {
        return asistenciaRepository.findAll();
    }

    @Override
    public List<Asistencia> obtenerPorEstudiante(String idEstudiante) {
        validarEstudianteExiste(idEstudiante);
        return asistenciaRepository.findByEstudianteIdEstudiante(idEstudiante);
    }

    @Override
    public Map<String, Object> resumenPorEstudiante(String idEstudiante) {
        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("idEstudiante", idEstudiante);
        resumen.putAll(calcularResumen(obtenerPorEstudiante(idEstudiante)));
        return resumen;
    }

    @Override
    public Map<String, Object> resumenPorEstudianteYGrupo(String idEstudiante, String idGrupo) {
        validarEstudianteExiste(idEstudiante);
        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("idEstudiante", idEstudiante);
        resumen.put("idGrupo", idGrupo);
        resumen.putAll(calcularResumen(asistenciaRepository.findByEstudianteIdEstudianteAndGrupoIdGrupo(idEstudiante, idGrupo)));
        return resumen;
    }

    @Override
    public Asistencia actualizar(Long id, Asistencia datos) {
        Asistencia existente = obtenerPorId(id);

        boolean cambioEstudiante = !existente.getEstudiante().getIdEstudiante().equals(datos.getEstudiante().getIdEstudiante());
        boolean cambioGrupo = !existente.getGrupo().getIdGrupo().equals(datos.getGrupo().getIdGrupo());
        boolean cambioFecha = !existente.getFechaAsistencia().equals(datos.getFechaAsistencia());
        if (cambioEstudiante || cambioGrupo || cambioFecha) {
            validar(datos);
        }
        validarJustificacion(datos);

        existente.setFechaAsistencia(datos.getFechaAsistencia());
        existente.setAsistio(datos.getAsistio());
        existente.setJustificada(datos.getJustificada());
        existente.setObservacion(datos.getObservacion());
        existente.setEstudiante(datos.getEstudiante());
        existente.setGrupo(datos.getGrupo());
        return asistenciaRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        if (!asistenciaRepository.existsById(id)) {
            throw new AsistenciaNotFoundException("Asistencia no encontrada con id: " + id);
        }
        asistenciaRepository.deleteById(id);
    }

    private void validar(Asistencia asistencia) {
        String idEstudiante = asistencia.getEstudiante().getIdEstudiante();
        String idGrupo = asistencia.getGrupo().getIdGrupo();
        LocalDate fecha = asistencia.getFechaAsistencia();

        // 1. No se registra asistencia a futuro
        if (fecha.isAfter(LocalDate.now())) {
            throw new BusinessException("FECHA_FUTURA", "No se puede registrar asistencia en una fecha futura: " + fecha);
        }
        // 2. El estudiante debe estar inscrito en el grupo
        if (!inscripcionRepository.existsByEstudianteIdEstudianteAndGrupoIdGrupo(idEstudiante, idGrupo)) {
            throw new BusinessException("ESTUDIANTE_NO_INSCRITO", "El estudiante " + idEstudiante + " no está inscrito en el grupo " + idGrupo);
        }
        // 3. Un solo registro por estudiante, grupo y fecha
        if (asistenciaRepository.existsByEstudianteIdEstudianteAndGrupoIdGrupoAndFechaAsistencia(idEstudiante, idGrupo, fecha)) {
            throw new BusinessException("ASISTENCIA_DUPLICADA", "Ya existe un registro de asistencia del estudiante " + idEstudiante
                    + " en el grupo " + idGrupo + " para la fecha " + fecha);
        }
    }

    // Solo una inasistencia puede quedar justificada
    private void validarJustificacion(Asistencia asistencia) {
        if (asistencia.getAsistio() && asistencia.getJustificada() != null && asistencia.getJustificada()) {
            throw new BusinessException("JUSTIFICACION_INVALIDA", "Solo se puede justificar una inasistencia (asistio debe ser false)");
        }
    }

    private Map<String, Object> calcularResumen(List<Asistencia> registros) {
        int total = registros.size();
        int asistidas = 0;
        int justificadas = 0;
        for (Asistencia asistencia : registros) {
            if (asistencia.getAsistio()) {
                asistidas++;
            } else if (asistencia.getJustificada() != null && asistencia.getJustificada()) {
                justificadas++;
            }
        }

        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("totalClases", total);
        resumen.put("asistidas", asistidas);
        resumen.put("inasistencias", total - asistidas);
        resumen.put("inasistenciasJustificadas", justificadas);
        if (total == 0) {
            resumen.put("porcentajeAsistencia", null); // sin registros
        } else {
            resumen.put("porcentajeAsistencia", Math.round(asistidas * 10000.0 / total) / 100.0);
        }
        return resumen;
    }

    private void validarEstudianteExiste(String idEstudiante) {
        if (!estudianteRepository.existsById(idEstudiante)) {
            throw new EstudianteNotFoundException("Estudiante no encontrado con id: " + idEstudiante);
        }
    }
}
