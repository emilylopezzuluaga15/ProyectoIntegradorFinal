package com.example.gestionacademica.infrastructure.persistence;

import com.example.gestionacademica.domain.models.Profesor;
import com.example.gestionacademica.domain.repository.ProfesorRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProfesorRepositoryAdapter implements ProfesorRepository {

    private final ProfesorJpaRepository jpaRepository;

    public ProfesorRepositoryAdapter(ProfesorJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Profesor save(Profesor profesor) {
        return jpaRepository.save(profesor);
    }

    @Override
    public Optional<Profesor> findById(String id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Profesor> findAll() {
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
