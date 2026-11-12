package com.example.gestionacademica.domain.repository;

import com.example.gestionacademica.domain.models.AsignacionMateriaProfesor;

import java.util.List;
import java.util.Optional;

public interface AsignacionMateriaProfesorRepository {

    AsignacionMateriaProfesor save(AsignacionMateriaProfesor asignacionMateriaProfesor);

    Optional<AsignacionMateriaProfesor> findById(Long id);

    List<AsignacionMateriaProfesor> findAll();

    boolean existsById(Long id);

    void deleteById(Long id);

    boolean existsByGrupoIdGrupoAndMateriaIdMateria(String idGrupo, String idMateria);

    List<AsignacionMateriaProfesor> findByProfesorIdProfesor(String idProfesor);

    List<AsignacionMateriaProfesor> findByGrupoIdGrupoAndMateriaIdMateria(String idGrupo, String idMateria);

    long countByProfesorIdProfesor(String idProfesor);

    long countByGrupoIdGrupo(String idGrupo);

    long countByMateriaIdMateria(String idMateria);

    boolean existsByProfesorIdProfesorAndMateriaIdMateria(String idProfesor, String idMateria);

    boolean existsByProfesorIdProfesorAndGrupoIdGrupo(String idProfesor, String idGrupo);

    boolean existsByProfesorIdProfesorAndGrupoIdGrupoAndMateriaIdMateria(String idProfesor, String idGrupo, String idMateria);
}
