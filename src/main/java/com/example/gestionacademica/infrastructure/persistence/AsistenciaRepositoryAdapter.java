package com.example.gestionacademica.infrastructure.persistence;

import com.example.gestionacademica.domain.models.Asistencia;
import com.example.gestionacademica.domain.repository.AsistenciaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class AsistenciaRepositoryAdapter implements AsistenciaRepository {

    private final AsistenciaJpaRepository jpaRepository;

    public AsistenciaRepositoryAdapter(AsistenciaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Asistencia save(Asistencia asistencia) {
        return jpaRepository.save(asistencia);
    }

    @Override
    public Optional<Asistencia> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Asistencia> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByEstudianteIdEstudianteAndGrupoIdGrupoAndFechaAsistencia(String idEstudiante, String idGrupo, LocalDate fechaAsistencia) {
        return jpaRepository.existsByEstudianteIdEstudianteAndGrupoIdGrupoAndFechaAsistencia(idEstudiante, idGrupo, fechaAsistencia);
    }

    @Override
    public List<Asistencia> findByEstudianteIdEstudiante(String idEstudiante) {
        return jpaRepository.findByEstudianteIdEstudiante(idEstudiante);
    }

    @Override
    public List<Asistencia> findByEstudianteIdEstudianteAndGrupoIdGrupo(String idEstudiante, String idGrupo) {
        return jpaRepository.findByEstudianteIdEstudianteAndGrupoIdGrupo(idEstudiante, idGrupo);
    }

    @Override
    public long countByEstudianteIdEstudiante(String idEstudiante) {
        return jpaRepository.countByEstudianteIdEstudiante(idEstudiante);
    }

    @Override
    public long countByGrupoIdGrupo(String idGrupo) {
        return jpaRepository.countByGrupoIdGrupo(idGrupo);
    }
}
