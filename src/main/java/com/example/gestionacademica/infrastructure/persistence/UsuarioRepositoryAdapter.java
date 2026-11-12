package com.example.gestionacademica.infrastructure.persistence;

import com.example.gestionacademica.domain.models.Rol;
import com.example.gestionacademica.domain.models.Usuario;
import com.example.gestionacademica.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final UsuarioJpaRepository jpaRepository;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Usuario save(Usuario usuario) {
        return jpaRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Usuario> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByRol(Rol rol) {
        return jpaRepository.existsByRol(rol);
    }

    @Override
    public long countByRolAndActivoTrue(Rol rol) {
        return jpaRepository.countByRolAndActivoTrue(rol);
    }

    @Override
    public Optional<Usuario> findByUsername(String username) {
        return jpaRepository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEstudianteIdEstudiante(String idEstudiante) {
        return jpaRepository.existsByEstudianteIdEstudiante(idEstudiante);
    }

    @Override
    public boolean existsByProfesorIdProfesor(String idProfesor) {
        return jpaRepository.existsByProfesorIdProfesor(idProfesor);
    }
}
