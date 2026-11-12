package com.example.gestionacademica.application.Services.impl;

import com.example.gestionacademica.application.Exceptions.BusinessException;
import com.example.gestionacademica.application.Exceptions.GrupoNotFoundException;
import com.example.gestionacademica.application.Services.GrupoServices;
import com.example.gestionacademica.domain.models.Grupo;
import com.example.gestionacademica.domain.repository.AsignacionMateriaProfesorRepository;
import com.example.gestionacademica.domain.repository.AsistenciaRepository;
import com.example.gestionacademica.domain.repository.GrupoRepository;
import com.example.gestionacademica.domain.repository.InscripcionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GrupoServicesImpl implements GrupoServices {

    private final GrupoRepository grupoRepository;
    private final InscripcionRepository inscripcionRepository;
    private final AsignacionMateriaProfesorRepository asignacionMateriaProfesorRepository;
    private final AsistenciaRepository asistenciaRepository;

    public GrupoServicesImpl(GrupoRepository grupoRepository,
                             InscripcionRepository inscripcionRepository,
                             AsignacionMateriaProfesorRepository asignacionMateriaProfesorRepository,
                             AsistenciaRepository asistenciaRepository) {
        this.grupoRepository = grupoRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.asignacionMateriaProfesorRepository = asignacionMateriaProfesorRepository;
        this.asistenciaRepository = asistenciaRepository;
    }

    @Override
    public Grupo crear(Grupo grupo) {
        if (grupoRepository.existsById(grupo.getIdGrupo())) {
            throw new BusinessException("YA_EXISTE", "Grupo ya existe con id: " + grupo.getIdGrupo());
        }
        return grupoRepository.save(grupo);
    }

    @Override
    public Grupo obtenerPorId(String id) {
        return grupoRepository.findById(id)
                .orElseThrow(() -> new GrupoNotFoundException("Grupo no encontrado con id: " + id));
    }

    @Override
    public List<Grupo> obtenerTodos() {
        return grupoRepository.findAll();
    }

    @Override
    public Grupo actualizar(String id, Grupo datos) {
        Grupo existente = obtenerPorId(id);
        existente.setNombreGrupo(datos.getNombreGrupo());
        existente.setNivel(datos.getNivel());
        existente.setCupoMaximo(datos.getCupoMaximo());
        return grupoRepository.save(existente);
    }

    @Override
    public void eliminar(String id) {
        if (!grupoRepository.existsById(id)) {
            throw new GrupoNotFoundException("Grupo no encontrado con id: " + id);
        }
        long inscripciones = inscripcionRepository.countByGrupoIdGrupo(id);
        long asignaciones = asignacionMateriaProfesorRepository.countByGrupoIdGrupo(id);
        long asistencias = asistenciaRepository.countByGrupoIdGrupo(id);
        if (inscripciones > 0 || asignaciones > 0 || asistencias > 0) {
            throw new BusinessException("TIENE_DEPENDENCIAS", "No se puede eliminar el grupo " + id + " porque tiene "
                    + inscripciones + " inscripciones, " + asignaciones + " asignaciones de profesor y "
                    + asistencias + " registros de asistencia");
        }
        grupoRepository.deleteById(id);
    }
}
