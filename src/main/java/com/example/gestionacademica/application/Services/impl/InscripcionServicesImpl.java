package com.example.gestionacademica.application.Services.impl;

import com.example.gestionacademica.application.Exceptions.BusinessException;
import com.example.gestionacademica.application.Exceptions.EstudianteNotFoundException;
import com.example.gestionacademica.application.Exceptions.GrupoNotFoundException;
import com.example.gestionacademica.application.Exceptions.InscripcionNotFoundException;
import com.example.gestionacademica.application.Services.InscripcionServices;
import com.example.gestionacademica.domain.models.EstadoEstudiante;
import com.example.gestionacademica.domain.models.Estudiante;
import com.example.gestionacademica.domain.models.Grupo;
import com.example.gestionacademica.domain.models.Inscripcion;
import com.example.gestionacademica.domain.repository.EstudianteRepository;
import com.example.gestionacademica.domain.repository.GrupoRepository;
import com.example.gestionacademica.domain.repository.InscripcionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class InscripcionServicesImpl implements InscripcionServices {

    private final InscripcionRepository inscripcionRepository;
    private final EstudianteRepository estudianteRepository;
    private final GrupoRepository grupoRepository;

    public InscripcionServicesImpl(InscripcionRepository inscripcionRepository,
                                   EstudianteRepository estudianteRepository,
                                   GrupoRepository grupoRepository) {
        this.inscripcionRepository = inscripcionRepository;
        this.estudianteRepository = estudianteRepository;
        this.grupoRepository = grupoRepository;
    }

    @Override
    public Inscripcion crear(Inscripcion inscripcion) {
        validar(inscripcion);
        return inscripcionRepository.save(inscripcion);
    }

    @Override
    public Inscripcion obtenerPorId(Long id) {
        return inscripcionRepository.findById(id)
                .orElseThrow(() -> new InscripcionNotFoundException("Inscripción no encontrada con id: " + id));
    }

    @Override
    public List<Inscripcion> obtenerTodos() {
        return inscripcionRepository.findAll();
    }

    @Override
    public List<Inscripcion> obtenerPorEstudiante(String idEstudiante) {
        return inscripcionRepository.findByEstudianteIdEstudiante(idEstudiante);
    }

    @Override
    public List<Inscripcion> obtenerPorGrupo(String idGrupo) {
        return inscripcionRepository.findByGrupoIdGrupo(idGrupo);
    }

    @Override
    public Map<String, Object> consultarCupo(String idGrupo, Integer anio) {
        Grupo grupo = buscarGrupo(idGrupo);
        long inscritos = inscripcionRepository.countByGrupoIdGrupoAndAnioInscripcion(idGrupo, anio);

        Map<String, Object> cupo = new LinkedHashMap<>();
        cupo.put("idGrupo", idGrupo);
        cupo.put("anio", anio);
        cupo.put("cupoMaximo", grupo.getCupoMaximo());
        cupo.put("inscritos", inscritos);
        if (grupo.getCupoMaximo() == null) {
            cupo.put("disponibles", null); // grupo sin límite
        } else {
            cupo.put("disponibles", Math.max(grupo.getCupoMaximo() - inscritos, 0));
        }
        return cupo;
    }

    @Override
    public Inscripcion actualizar(Long id, Inscripcion datos) {
        Inscripcion existente = obtenerPorId(id);

        // Solo se revalida si cambia el estudiante, el grupo o el año
        boolean cambioEstudiante = !existente.getEstudiante().getIdEstudiante().equals(datos.getEstudiante().getIdEstudiante());
        boolean cambioGrupo = !existente.getGrupo().getIdGrupo().equals(datos.getGrupo().getIdGrupo());
        boolean cambioAnio = !existente.getAnioInscripcion().equals(datos.getAnioInscripcion());
        if (cambioEstudiante || cambioGrupo || cambioAnio) {
            validar(datos);
        }

        existente.setAnioInscripcion(datos.getAnioInscripcion());
        existente.setCohorte(datos.getCohorte());
        existente.setEstudiante(datos.getEstudiante());
        existente.setGrupo(datos.getGrupo());
        return inscripcionRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        if (!inscripcionRepository.existsById(id)) {
            throw new InscripcionNotFoundException("Inscripción no encontrada con id: " + id);
        }
        inscripcionRepository.deleteById(id);
    }

    private void validar(Inscripcion inscripcion) {
        String idEstudiante = inscripcion.getEstudiante().getIdEstudiante();
        String idGrupo = inscripcion.getGrupo().getIdGrupo();
        Integer anio = inscripcion.getAnioInscripcion();

        // 1. El estudiante debe existir y estar activo
        Estudiante estudiante = estudianteRepository.findById(idEstudiante)
                .orElseThrow(() -> new EstudianteNotFoundException("Estudiante no encontrado con id: " + idEstudiante));
        if (!estudiante.getActivo() || (estudiante.getEstado() != null && estudiante.getEstado() != EstadoEstudiante.Activo)) {
            throw new BusinessException("ESTUDIANTE_INACTIVO", "El estudiante " + idEstudiante + " no está activo y no puede inscribirse");
        }

        // 2. No puede estar inscrito dos veces en el mismo grupo el mismo año
        if (inscripcionRepository.existsByEstudianteIdEstudianteAndGrupoIdGrupoAndAnioInscripcion(idEstudiante, idGrupo, anio)) {
            throw new BusinessException("INSCRIPCION_DUPLICADA", "El estudiante " + idEstudiante + " ya está inscrito en el grupo "
                    + idGrupo + " para el año " + anio);
        }

        // 3. El grupo no puede superar su cupo máximo (si lo tiene)
        Grupo grupo = buscarGrupo(idGrupo);
        if (grupo.getCupoMaximo() != null) {
            long inscritos = inscripcionRepository.countByGrupoIdGrupoAndAnioInscripcion(idGrupo, anio);
            if (inscritos >= grupo.getCupoMaximo()) {
                throw new BusinessException("CUPO_LLENO", "El grupo " + idGrupo + " no tiene cupo disponible para el año " + anio
                        + " (cupo máximo: " + grupo.getCupoMaximo() + ")");
            }
        }
    }

    private Grupo buscarGrupo(String idGrupo) {
        return grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new GrupoNotFoundException("Grupo no encontrado con id: " + idGrupo));
    }
}
