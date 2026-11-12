package com.example.gestionacademica.infrastructure.persistence;

import com.example.gestionacademica.domain.models.Nota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotaJpaRepository extends JpaRepository<Nota, Long> {

    List<Nota> findByEstudianteIdEstudiante(String idEstudiante);

    List<Nota> findByEstudianteIdEstudianteAndMateriaIdMateria(String idEstudiante, String idMateria);

    long countByEstudianteIdEstudiante(String idEstudiante);

    long countByMateriaIdMateria(String idMateria);
}
