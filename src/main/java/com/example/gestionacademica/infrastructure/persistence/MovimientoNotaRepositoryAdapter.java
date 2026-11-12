package com.example.gestionacademica.infrastructure.persistence;

import com.example.gestionacademica.domain.models.MovimientoNota;
import com.example.gestionacademica.domain.repository.MovimientoNotaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MovimientoNotaRepositoryAdapter implements MovimientoNotaRepository {

    private final MovimientoNotaJpaRepository jpaRepository;

    public MovimientoNotaRepositoryAdapter(MovimientoNotaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public MovimientoNota save(MovimientoNota movimientoNota) {
        return jpaRepository.save(movimientoNota);
    }

    @Override
    public Optional<MovimientoNota> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<MovimientoNota> findAll() {
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
    public List<MovimientoNota> findByNotaIdNota(Long idNota) {
        return jpaRepository.findByNotaIdNota(idNota);
    }
}
