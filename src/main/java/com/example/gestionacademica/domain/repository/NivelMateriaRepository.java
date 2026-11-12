package com.example.gestionacademica.domain.repository;

import com.example.gestionacademica.domain.models.NivelMateria;

import java.util.List;
import java.util.Optional;

public interface NivelMateriaRepository {

    NivelMateria save(NivelMateria nivelMateria);

    Optional<NivelMateria> findById(Long id);

    List<NivelMateria> findAll();

    boolean existsById(Long id);

    void deleteById(Long id);

    boolean existsByNivelIdNivelAndMateriaIdMateria(String idNivel, String idMateria);

    long countByNivelIdNivel(String idNivel);

    long countByMateriaIdMateria(String idMateria);
}
