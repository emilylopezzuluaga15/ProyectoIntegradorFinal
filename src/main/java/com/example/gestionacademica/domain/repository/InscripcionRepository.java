package com.example.gestionacademica.domain.repository;

import com.example.gestionacademica.domain.models.Inscripcion;

import java.util.List;
import java.util.Optional;

public interface InscripcionRepository {

    Inscripcion save(Inscripcion inscripcion);

    Optional<Inscripcion> findById(Long id);

    List<Inscripcion> findAll();

    boolean existsById(Long id);

    void deleteById(Long id);

    boolean existsByEstudianteIdEstudianteAndGrupoIdGrupoAndAnioInscripcion(String idEstudiante, String idGrupo, Integer anioInscripcion);

    long countByGrupoIdGrupoAndAnioInscripcion(String idGrupo, Integer anioInscripcion);

    boolean existsByEstudianteIdEstudianteAndGrupoIdGrupo(String idEstudiante, String idGrupo);

    List<Inscripcion> findByEstudianteIdEstudiante(String idEstudiante);

    List<Inscripcion> findByGrupoIdGrupo(String idGrupo);

    List<Inscripcion> findByGrupoIdGrupoAndAnioInscripcion(String idGrupo, Integer anioInscripcion);

    long countByGrupoIdGrupo(String idGrupo);

    long countByEstudianteIdEstudiante(String idEstudiante);
}
