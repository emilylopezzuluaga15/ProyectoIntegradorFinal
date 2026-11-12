package com.example.gestionacademica.infrastructure.persistence;

import com.example.gestionacademica.domain.models.Nivel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NivelJpaRepository extends JpaRepository<Nivel, String> {

    long countByCarreraIdCarrera(String idCarrera);
}
