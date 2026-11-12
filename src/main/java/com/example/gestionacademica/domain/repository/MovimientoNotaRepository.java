package com.example.gestionacademica.domain.repository;

import com.example.gestionacademica.domain.models.MovimientoNota;

import java.util.List;
import java.util.Optional;

public interface MovimientoNotaRepository {

    MovimientoNota save(MovimientoNota movimientoNota);

    Optional<MovimientoNota> findById(Long id);

    List<MovimientoNota> findAll();

    boolean existsById(Long id);

    void deleteById(Long id);

    List<MovimientoNota> findByNotaIdNota(Long idNota);
}
