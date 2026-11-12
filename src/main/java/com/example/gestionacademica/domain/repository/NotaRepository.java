package com.example.gestionacademica.domain.repository;

import com.example.gestionacademica.domain.models.Nota;

import java.util.List;
import java.util.Optional;

public interface NotaRepository {

    Nota save(Nota nota);

    Optional<Nota> findById(Long id);

    List<Nota> findAll();

    boolean existsById(Long id);

    void deleteById(Long id);

    List<Nota> findByEstudianteIdEstudiante(String idEstudiante);

    List<Nota> findByEstudianteIdEstudianteAndMateriaIdMateria(String idEstudiante, String idMateria);

    long countByEstudianteIdEstudiante(String idEstudiante);

    long countByMateriaIdMateria(String idMateria);
}
