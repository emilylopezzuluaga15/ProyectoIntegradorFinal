package com.example.gestionacademica.infrastructure.persistence;

import com.example.gestionacademica.domain.models.MovimientoNota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoNotaJpaRepository extends JpaRepository<MovimientoNota, Long> {

    List<MovimientoNota> findByNotaIdNota(Long idNota);
}
