package com.example.gestionacademica.infrastructure.persistence;

import com.example.gestionacademica.domain.models.Estudiante;
import com.example.gestionacademica.domain.repository.EstudianteRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EstudianteRepositoryAdapter implements EstudianteRepository {

    private final EstudianteJpaRepository jpaRepository;

    public EstudianteRepositoryAdapter(EstudianteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Estudiante save(Estudiante estudiante) {
        return jpaRepository.save(estudiante);
    }

    @Override
    public Optional<Estudiante> findById(String id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Estudiante> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public boolean existsById(String id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByCorreo(String correo) {
        return jpaRepository.existsByCorreo(correo);
    }
}
