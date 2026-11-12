package com.example.gestionacademica.infrastructure.persistence;

import com.example.gestionacademica.domain.models.Grupo;
import com.example.gestionacademica.domain.repository.GrupoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GrupoRepositoryAdapter implements GrupoRepository {

    private final GrupoJpaRepository jpaRepository;

    public GrupoRepositoryAdapter(GrupoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Grupo save(Grupo grupo) {
        return jpaRepository.save(grupo);
    }

    @Override
    public Optional<Grupo> findById(String id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Grupo> findAll() {
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
    public long countByNivelIdNivel(String idNivel) {
        return jpaRepository.countByNivelIdNivel(idNivel);
    }
}
