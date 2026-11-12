package com.example.gestionacademica.application.Services.impl;

import com.example.gestionacademica.application.Exceptions.AsignacionMateriaProfesorNotFoundException;
import com.example.gestionacademica.application.Exceptions.BusinessException;
import com.example.gestionacademica.application.Exceptions.GrupoNotFoundException;
import com.example.gestionacademica.application.Exceptions.ProfesorNotFoundException;
import com.example.gestionacademica.application.Services.AsignacionMateriaProfesorServices;
import com.example.gestionacademica.domain.models.AsignacionMateriaProfesor;
import com.example.gestionacademica.domain.models.Grupo;
import com.example.gestionacademica.domain.models.Profesor;
import com.example.gestionacademica.domain.repository.AsignacionMateriaProfesorRepository;
import com.example.gestionacademica.domain.repository.GrupoRepository;
import com.example.gestionacademica.domain.repository.NivelMateriaRepository;
import com.example.gestionacademica.domain.repository.ProfesorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AsignacionMateriaProfesorServicesImpl implements AsignacionMateriaProfesorServices {

    private final AsignacionMateriaProfesorRepository asignacionMateriaProfesorRepository;
    private final ProfesorRepository profesorRepository;
    private final GrupoRepository grupoRepository;
    private final NivelMateriaRepository nivelMateriaRepository;

    public AsignacionMateriaProfesorServicesImpl(AsignacionMateriaProfesorRepository asignacionMateriaProfesorRepository,
                                                 ProfesorRepository profesorRepository,
                                                 GrupoRepository grupoRepository,
                                                 NivelMateriaRepository nivelMateriaRepository) {
        this.asignacionMateriaProfesorRepository = asignacionMateriaProfesorRepository;
        this.profesorRepository = profesorRepository;
        this.grupoRepository = grupoRepository;
        this.nivelMateriaRepository = nivelMateriaRepository;
    }

    @Override
    public AsignacionMateriaProfesor crear(AsignacionMateriaProfesor asignacionMateriaProfesor) {
        validar(asignacionMateriaProfesor, true);
        return asignacionMateriaProfesorRepository.save(asignacionMateriaProfesor);
    }

    @Override
    public AsignacionMateriaProfesor obtenerPorId(Long id) {
        return asignacionMateriaProfesorRepository.findById(id)
                .orElseThrow(() -> new AsignacionMateriaProfesorNotFoundException("Asignación no encontrada con id: " + id));
    }

    @Override
    public List<AsignacionMateriaProfesor> obtenerTodos() {
        return asignacionMateriaProfesorRepository.findAll();
    }

    @Override
    public List<AsignacionMateriaProfesor> obtenerPorProfesor(String idProfesor) {
        if (!profesorRepository.existsById(idProfesor)) {
            throw new ProfesorNotFoundException("Profesor no encontrado con id: " + idProfesor);
        }
        return asignacionMateriaProfesorRepository.findByProfesorIdProfesor(idProfesor);
    }

    @Override
    public AsignacionMateriaProfesor actualizar(Long id, AsignacionMateriaProfesor datos) {
        AsignacionMateriaProfesor existente = obtenerPorId(id);

        boolean cambioProfesor = !existente.getProfesor().getIdProfesor().equals(datos.getProfesor().getIdProfesor());
        boolean cambioGrupo = !existente.getGrupo().getIdGrupo().equals(datos.getGrupo().getIdGrupo());
        boolean cambioMateria = !existente.getMateria().getIdMateria().equals(datos.getMateria().getIdMateria());
        if (cambioProfesor || cambioGrupo || cambioMateria) {
            // Si solo cambia el profesor, no se revisa el duplicado (sería contra sí misma)
            validar(datos, cambioGrupo || cambioMateria);
        }
        existente.setProfesor(datos.getProfesor());
        existente.setGrupo(datos.getGrupo());
        existente.setMateria(datos.getMateria());
        return asignacionMateriaProfesorRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        if (!asignacionMateriaProfesorRepository.existsById(id)) {
            throw new AsignacionMateriaProfesorNotFoundException("Asignación no encontrada con id: " + id);
        }
        asignacionMateriaProfesorRepository.deleteById(id);
    }

    private void validar(AsignacionMateriaProfesor asignacion, boolean validarDuplicado) {
        String idProfesor = asignacion.getProfesor().getIdProfesor();
        String idGrupo = asignacion.getGrupo().getIdGrupo();
        String idMateria = asignacion.getMateria().getIdMateria();

        // 1. El profesor debe existir y estar activo
        Profesor profesor = profesorRepository.findById(idProfesor)
                .orElseThrow(() -> new ProfesorNotFoundException("Profesor no encontrado con id: " + idProfesor));
        if (!profesor.getActivo()) {
            throw new BusinessException("PROFESOR_INACTIVO", "El profesor " + idProfesor + " no está activo y no puede recibir asignaciones");
        }

        // 2. La materia debe pertenecer al plan del nivel del grupo
        Grupo grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new GrupoNotFoundException("Grupo no encontrado con id: " + idGrupo));
        String idNivel = grupo.getNivel().getIdNivel();
        if (!nivelMateriaRepository.existsByNivelIdNivelAndMateriaIdMateria(idNivel, idMateria)) {
            throw new BusinessException("MATERIA_FUERA_DEL_NIVEL", "La materia " + idMateria + " no pertenece al nivel " + idNivel
                    + " del grupo " + idGrupo);
        }

        // 3. Cada materia tiene un solo profesor por grupo
        if (validarDuplicado && asignacionMateriaProfesorRepository.existsByGrupoIdGrupoAndMateriaIdMateria(idGrupo, idMateria)) {
            throw new BusinessException("MATERIA_YA_ASIGNADA", "La materia " + idMateria + " ya tiene un profesor asignado en el grupo " + idGrupo);
        }
    }
}
