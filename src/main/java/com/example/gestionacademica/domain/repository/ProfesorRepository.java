package com.example.gestionacademica.domain.repository;

import com.example.gestionacademica.domain.models.Profesor;

import java.util.List;
import java.util.Optional;

public interface ProfesorRepository {

    Profesor save(Profesor profesor);

    Optional<Profesor> findById(String id);

    List<Profesor> findAll();

    boolean existsById(String id);

    void deleteById(String id);

    boolean existsByCorreo(String correo);
}
