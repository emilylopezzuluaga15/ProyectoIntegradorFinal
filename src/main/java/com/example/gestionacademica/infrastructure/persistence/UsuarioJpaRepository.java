package com.example.gestionacademica.infrastructure.persistence;

import com.example.gestionacademica.domain.models.Rol;
import com.example.gestionacademica.domain.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioJpaRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByRol(Rol rol);

    long countByRolAndActivoTrue(Rol rol);

    boolean existsByEstudianteIdEstudiante(String idEstudiante);

    boolean existsByProfesorIdProfesor(String idProfesor);
}
