package com.example.gestionacademica.domain.repository;

import com.example.gestionacademica.domain.models.Nivel;

import java.util.List;
import java.util.Optional;

public interface NivelRepository {

    Nivel save(Nivel nivel);

    Optional<Nivel> findById(String id);

    List<Nivel> findAll();

    boolean existsById(String id);

    void deleteById(String id);

    long countByCarreraIdCarrera(String idCarrera);
}
