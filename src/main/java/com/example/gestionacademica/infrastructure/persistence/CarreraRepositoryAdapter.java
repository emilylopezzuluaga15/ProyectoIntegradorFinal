package com.example.gestionacademica.infrastructure.persistence;

import com.example.gestionacademica.domain.models.Carrera;
import com.example.gestionacademica.domain.repository.CarreraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CarreraRepositoryAdapter implements CarreraRepository {

    private final CarreraJpaRepository jpaRepository;

    public CarreraRepositoryAdapter(CarreraJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Carrera save(Carrera carrera) {
        return jpaRepository.save(carrera);
    }

    @Override
    public Optional<Carrera> findById(String id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Carrera> findAll() {
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
}
