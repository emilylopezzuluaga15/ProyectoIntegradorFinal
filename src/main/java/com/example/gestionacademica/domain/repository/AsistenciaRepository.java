package com.example.gestionacademica.domain.repository;

import com.example.gestionacademica.domain.models.Asistencia;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AsistenciaRepository {

    Asistencia save(Asistencia asistencia);

    Optional<Asistencia> findById(Long id);

    List<Asistencia> findAll();

    boolean existsById(Long id);

    void deleteById(Long id);

    boolean existsByEstudianteIdEstudianteAndGrupoIdGrupoAndFechaAsistencia(String idEstudiante, String idGrupo, LocalDate fechaAsistencia);

    List<Asistencia> findByEstudianteIdEstudiante(String idEstudiante);

    List<Asistencia> findByEstudianteIdEstudianteAndGrupoIdGrupo(String idEstudiante, String idGrupo);

    long countByEstudianteIdEstudiante(String idEstudiante);

    long countByGrupoIdGrupo(String idGrupo);
}
