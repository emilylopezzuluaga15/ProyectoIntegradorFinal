package com.example.gestionacademica.infrastructure.persistence;

import com.example.gestionacademica.domain.models.NivelMateria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NivelMateriaJpaRepository extends JpaRepository<NivelMateria, Long> {

    boolean existsByNivelIdNivelAndMateriaIdMateria(String idNivel, String idMateria);

    long countByNivelIdNivel(String idNivel);

    long countByMateriaIdMateria(String idMateria);
}
