package com.example.gestionacademica.infrastructure.persistence;

import com.example.gestionacademica.domain.models.NivelMateria;
import com.example.gestionacademica.domain.repository.NivelMateriaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class NivelMateriaRepositoryAdapter implements NivelMateriaRepository {

    private final NivelMateriaJpaRepository jpaRepository;

    public NivelMateriaRepositoryAdapter(NivelMateriaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public NivelMateria save(NivelMateria nivelMateria) {
        return jpaRepository.save(nivelMateria);
    }

    @Override
    public Optional<NivelMateria> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<NivelMateria> findAll() {
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
    public boolean existsByNivelIdNivelAndMateriaIdMateria(String idNivel, String idMateria) {
        return jpaRepository.existsByNivelIdNivelAndMateriaIdMateria(idNivel, idMateria);
    }

    @Override
    public long countByNivelIdNivel(String idNivel) {
        return jpaRepository.countByNivelIdNivel(idNivel);
    }

    @Override
    public long countByMateriaIdMateria(String idMateria) {
        return jpaRepository.countByMateriaIdMateria(idMateria);
    }
}
