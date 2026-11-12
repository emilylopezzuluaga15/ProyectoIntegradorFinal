package com.example.gestionacademica.domain.repository;

import com.example.gestionacademica.domain.models.Estudiante;

import java.util.List;
import java.util.Optional;

public interface EstudianteRepository {

    Estudiante save(Estudiante estudiante);

    Optional<Estudiante> findById(String id);

    List<Estudiante> findAll();

    boolean existsById(String id);

    void deleteById(String id);

    boolean existsByCorreo(String correo);
}
