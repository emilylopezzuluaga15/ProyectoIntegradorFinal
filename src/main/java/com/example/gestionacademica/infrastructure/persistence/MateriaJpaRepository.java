package com.example.gestionacademica.infrastructure.persistence;

import com.example.gestionacademica.domain.models.Materia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MateriaJpaRepository extends JpaRepository<Materia, String> {
}
