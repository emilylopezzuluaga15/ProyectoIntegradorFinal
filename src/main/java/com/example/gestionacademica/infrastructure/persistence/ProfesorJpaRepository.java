package com.example.gestionacademica.infrastructure.persistence;

import com.example.gestionacademica.domain.models.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfesorJpaRepository extends JpaRepository<Profesor, String> {

    boolean existsByCorreo(String correo);
}
