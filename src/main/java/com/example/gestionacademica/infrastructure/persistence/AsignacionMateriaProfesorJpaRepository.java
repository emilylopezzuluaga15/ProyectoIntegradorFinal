package com.example.gestionacademica.infrastructure.persistence;

import com.example.gestionacademica.domain.models.AsignacionMateriaProfesor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AsignacionMateriaProfesorJpaRepository extends JpaRepository<AsignacionMateriaProfesor, Long> {

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
