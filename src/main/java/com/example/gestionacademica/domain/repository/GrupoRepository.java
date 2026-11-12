package com.example.gestionacademica.domain.repository;

import com.example.gestionacademica.domain.models.Grupo;

import java.util.List;
import java.util.Optional;

public interface GrupoRepository {

    Grupo save(Grupo grupo);

    Optional<Grupo> findById(String id);

    List<Grupo> findAll();

    boolean existsById(String id);

    void deleteById(String id);

    long countByNivelIdNivel(String idNivel);
}
