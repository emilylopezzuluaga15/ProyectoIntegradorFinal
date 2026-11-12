package com.example.gestionacademica.application.Services.impl;

import com.example.gestionacademica.application.Exceptions.BusinessException;
import com.example.gestionacademica.application.Exceptions.UsuarioNotFoundException;
import com.example.gestionacademica.application.Services.UsuarioServices;
import com.example.gestionacademica.domain.models.Rol;
import com.example.gestionacademica.domain.models.Usuario;
import com.example.gestionacademica.domain.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UsuarioServicesImpl implements UsuarioServices {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServicesImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario crear(Usuario usuario, String password) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new BusinessException("USERNAME_DUPLICADO", "Ya existe un usuario con el username: " + usuario.getUsername());
        }
        validarVinculo(usuario, null);
        usuario.setPasswordHash(passwordEncoder.encode(password));
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con id: " + id));
    }

    @Override
    public Usuario obtenerPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado: " + username));
    }

    @Override
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario actualizar(Long id, Usuario datos, String password, Long idUsuarioActual) {
        Usuario existente = obtenerPorId(id);

        boolean cambioUsername = !existente.getUsername().equals(datos.getUsername());
        if (cambioUsername && usuarioRepository.existsByUsername(datos.getUsername())) {
            throw new BusinessException("USERNAME_DUPLICADO", "Ya existe un usuario con el username: " + datos.getUsername());
        }
        validarVinculo(datos, existente);

        boolean dejaDeSerAdminActivo = esAdminActivo(existente)
                && (datos.getRol() != Rol.ADMINISTRATIVO || !Boolean.TRUE.equals(datos.getActivo()));
        if (dejaDeSerAdminActivo && id.equals(idUsuarioActual)) {
            throw new BusinessException("CUENTA_PROPIA",
                    "No puedes desactivar tu propia cuenta ni quitarte el rol ADMINISTRATIVO");
        }
        if (dejaDeSerAdminActivo) {
            exigirOtroAdminActivo();
        }

        existente.setUsername(datos.getUsername());
        existente.setRol(datos.getRol());
        existente.setActivo(datos.getActivo());
        existente.setEstudiante(datos.getEstudiante());
        existente.setProfesor(datos.getProfesor());
        if (password != null && !password.isBlank()) {
            existente.setPasswordHash(passwordEncoder.encode(password));
        }
        return usuarioRepository.save(existente);
    }

    @Override
    public void eliminar(Long id, Long idUsuarioActual) {
        Usuario usuario = obtenerPorId(id);
        if (id.equals(idUsuarioActual)) {
            throw new BusinessException("CUENTA_PROPIA", "No puedes eliminar tu propia cuenta");
        }
        if (esAdminActivo(usuario)) {
            exigirOtroAdminActivo();
        }
        usuarioRepository.deleteById(id);
    }

    private boolean esAdminActivo(Usuario usuario) {
        return usuario.getRol() == Rol.ADMINISTRATIVO && Boolean.TRUE.equals(usuario.getActivo());
    }

    // Sin un ADMINISTRATIVO activo nadie podría volver a administrar las cuentas
    private void exigirOtroAdminActivo() {
        if (usuarioRepository.countByRolAndActivoTrue(Rol.ADMINISTRATIVO) <= 1) {
            throw new BusinessException("ULTIMO_ADMINISTRATIVO",
                    "No se puede eliminar ni desactivar al último ADMINISTRATIVO activo");
        }
    }

    // El rol define a qué registro académico se vincula la cuenta; existente = null al crear.
    // Que el estudiante o profesor exista ya lo valida Referencias (404) al convertir el DTO
    private void validarVinculo(Usuario usuario, Usuario existente) {
        Rol rol = usuario.getRol();
        boolean tieneEstudiante = usuario.getEstudiante() != null;
        boolean tieneProfesor = usuario.getProfesor() != null;

        if (rol == Rol.ESTUDIANTE && (!tieneEstudiante || tieneProfesor)) {
            throw new BusinessException("VINCULO_INVALIDO", "Un usuario ESTUDIANTE debe estar vinculado a un estudiante (idEstudiante) y a ningún profesor");
        }
        if (rol == Rol.PROFESOR && (!tieneProfesor || tieneEstudiante)) {
            throw new BusinessException("VINCULO_INVALIDO", "Un usuario PROFESOR debe estar vinculado a un profesor (idProfesor) y a ningún estudiante");
        }
        if ((rol == Rol.COORDINADOR || rol == Rol.ADMINISTRATIVO) && (tieneEstudiante || tieneProfesor)) {
            throw new BusinessException("VINCULO_INVALIDO", "Un usuario " + rol + " no se vincula a estudiantes ni profesores");
        }

        // Una sola cuenta por estudiante o profesor
        if (tieneEstudiante) {
            String idEstudiante = usuario.getEstudiante().getIdEstudiante();
            boolean mismoVinculo = existente != null && existente.getEstudiante() != null
                    && existente.getEstudiante().getIdEstudiante().equals(idEstudiante);
            if (!mismoVinculo && usuarioRepository.existsByEstudianteIdEstudiante(idEstudiante)) {
                throw new BusinessException("VINCULO_DUPLICADO", "El estudiante " + idEstudiante + " ya tiene un usuario");
            }
        }
        if (tieneProfesor) {
            String idProfesor = usuario.getProfesor().getIdProfesor();
            boolean mismoVinculo = existente != null && existente.getProfesor() != null
                    && existente.getProfesor().getIdProfesor().equals(idProfesor);
            if (!mismoVinculo && usuarioRepository.existsByProfesorIdProfesor(idProfesor)) {
                throw new BusinessException("VINCULO_DUPLICADO", "El profesor " + idProfesor + " ya tiene un usuario");
            }
        }
    }
}
