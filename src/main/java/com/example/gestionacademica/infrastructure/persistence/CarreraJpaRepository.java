package com.example.gestionacademica.infrastructure.persistence;

import com.example.gestionacademica.domain.models.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarreraJpaRepository extends JpaRepository<Carrera, String> {
}
