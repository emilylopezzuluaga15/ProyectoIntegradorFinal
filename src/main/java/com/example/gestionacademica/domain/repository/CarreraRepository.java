package com.example.gestionacademica.domain.repository;

import com.example.gestionacademica.domain.models.Carrera;

import java.util.List;
import java.util.Optional;

public interface CarreraRepository {

    Carrera save(Carrera carrera);

    Optional<Carrera> findById(String id);

    List<Carrera> findAll();

    boolean existsById(String id);

    void deleteById(String id);
}
