package com.example.gestionacademica.infrastructure.persistence;

import com.example.gestionacademica.domain.models.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstudianteJpaRepository extends JpaRepository<Estudiante, String> {

    boolean existsByCorreo(String correo);
}
