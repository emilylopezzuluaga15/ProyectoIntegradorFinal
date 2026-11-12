package com.example.gestionacademica.infrastructure.persistence;

import com.example.gestionacademica.domain.models.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrupoJpaRepository extends JpaRepository<Grupo, String> {

    long countByNivelIdNivel(String idNivel);
}
