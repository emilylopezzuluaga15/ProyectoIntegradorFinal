package com.example.gestionacademica.infrastructure.persistence;

import com.example.gestionacademica.domain.models.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InscripcionJpaRepository extends JpaRepository<Inscripcion, Long> {

    boolean existsByEstudianteIdEstudianteAndGrupoIdGrupoAndAnioInscripcion(String idEstudiante, String idGrupo, Integer anioInscripcion);

    long countByGrupoIdGrupoAndAnioInscripcion(String idGrupo, Integer anioInscripcion);

    boolean existsByEstudianteIdEstudianteAndGrupoIdGrupo(String idEstudiante, String idGrupo);

    List<Inscripcion> findByEstudianteIdEstudiante(String idEstudiante);

    List<Inscripcion> findByGrupoIdGrupo(String idGrupo);

    List<Inscripcion> findByGrupoIdGrupoAndAnioInscripcion(String idGrupo, Integer anioInscripcion);

    long countByGrupoIdGrupo(String idGrupo);

    long countByEstudianteIdEstudiante(String idEstudiante);
}
