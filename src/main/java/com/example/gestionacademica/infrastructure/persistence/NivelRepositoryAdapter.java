package com.example.gestionacademica.infrastructure.persistence;

import com.example.gestionacademica.domain.models.Nivel;
import com.example.gestionacademica.domain.repository.NivelRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class NivelRepositoryAdapter implements NivelRepository {

    private final NivelJpaRepository jpaRepository;

    public NivelRepositoryAdapter(NivelJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Nivel save(Nivel nivel) {
        return jpaRepository.save(nivel);
    }

    @Override
    public Optional<Nivel> findById(String id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Nivel> findAll() {
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
    public long countByCarreraIdCarrera(String idCarrera) {
        return jpaRepository.countByCarreraIdCarrera(idCarrera);
    }
}
