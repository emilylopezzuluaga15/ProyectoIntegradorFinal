package com.example.gestionacademica.infrastructure.persistence;

import com.example.gestionacademica.domain.models.Inscripcion;
import com.example.gestionacademica.domain.repository.InscripcionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class InscripcionRepositoryAdapter implements InscripcionRepository {

    private final InscripcionJpaRepository jpaRepository;

    public InscripcionRepositoryAdapter(InscripcionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Inscripcion save(Inscripcion inscripcion) {
        return jpaRepository.save(inscripcion);
    }

    @Override
    public Optional<Inscripcion> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Inscripcion> findAll() {
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
    public boolean existsByEstudianteIdEstudianteAndGrupoIdGrupoAndAnioInscripcion(String idEstudiante, String idGrupo, Integer anioInscripcion) {
        return jpaRepository.existsByEstudianteIdEstudianteAndGrupoIdGrupoAndAnioInscripcion(idEstudiante, idGrupo, anioInscripcion);
    }

    @Override
    public long countByGrupoIdGrupoAndAnioInscripcion(String idGrupo, Integer anioInscripcion) {
        return jpaRepository.countByGrupoIdGrupoAndAnioInscripcion(idGrupo, anioInscripcion);
    }

    @Override
    public boolean existsByEstudianteIdEstudianteAndGrupoIdGrupo(String idEstudiante, String idGrupo) {
        return jpaRepository.existsByEstudianteIdEstudianteAndGrupoIdGrupo(idEstudiante, idGrupo);
    }

    @Override
    public List<Inscripcion> findByEstudianteIdEstudiante(String idEstudiante) {
        return jpaRepository.findByEstudianteIdEstudiante(idEstudiante);
    }

    @Override
    public List<Inscripcion> findByGrupoIdGrupo(String idGrupo) {
        return jpaRepository.findByGrupoIdGrupo(idGrupo);
    }

    @Override
    public List<Inscripcion> findByGrupoIdGrupoAndAnioInscripcion(String idGrupo, Integer anioInscripcion) {
        return jpaRepository.findByGrupoIdGrupoAndAnioInscripcion(idGrupo, anioInscripcion);
    }

    @Override
    public long countByGrupoIdGrupo(String idGrupo) {
        return jpaRepository.countByGrupoIdGrupo(idGrupo);
    }

    @Override
    public long countByEstudianteIdEstudiante(String idEstudiante) {
        return jpaRepository.countByEstudianteIdEstudiante(idEstudiante);
    }
}
