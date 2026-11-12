package com.example.gestionacademica.domain.repository;

import com.example.gestionacademica.domain.models.Rol;
import com.example.gestionacademica.domain.models.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {

    Usuario save(Usuario usuario);

    Optional<Usuario> findById(Long id);

    List<Usuario> findAll();

    boolean existsById(Long id);

    void deleteById(Long id);

    boolean existsByRol(Rol rol);

    long countByRolAndActivoTrue(Rol rol);

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEstudianteIdEstudiante(String idEstudiante);

    boolean existsByProfesorIdProfesor(String idProfesor);
}
