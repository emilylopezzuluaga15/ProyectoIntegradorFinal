package com.example.gestionacademica.infrastructure.persistence;

import com.example.gestionacademica.domain.models.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AsistenciaJpaRepository extends JpaRepository<Asistencia, Long> {

    boolean existsByEstudianteIdEstudianteAndGrupoIdGrupoAndFechaAsistencia(String idEstudiante, String idGrupo, LocalDate fechaAsistencia);

    List<Asistencia> findByEstudianteIdEstudiante(String idEstudiante);

    List<Asistencia> findByEstudianteIdEstudianteAndGrupoIdGrupo(String idEstudiante, String idGrupo);

    long countByEstudianteIdEstudiante(String idEstudiante);

    long countByGrupoIdGrupo(String idGrupo);
}
