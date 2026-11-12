package com.example.gestionacademica.domain.repository;

import com.example.gestionacademica.domain.models.Materia;

import java.util.List;
import java.util.Optional;

public interface MateriaRepository {

    Materia save(Materia materia);

    Optional<Materia> findById(String id);

    List<Materia> findAll();

    boolean existsById(String id);

    void deleteById(String id);
}
