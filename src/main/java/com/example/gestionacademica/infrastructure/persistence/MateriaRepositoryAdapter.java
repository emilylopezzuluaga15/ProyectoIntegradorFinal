package com.example.gestionacademica.infrastructure.persistence;

import com.example.gestionacademica.domain.models.Materia;
import com.example.gestionacademica.domain.repository.MateriaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MateriaRepositoryAdapter implements MateriaRepository {

    private final MateriaJpaRepository jpaRepository;

    public MateriaRepositoryAdapter(MateriaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Materia save(Materia materia) {
        return jpaRepository.save(materia);
    }

    @Override
    public Optional<Materia> findById(String id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Materia> findAll() {
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
