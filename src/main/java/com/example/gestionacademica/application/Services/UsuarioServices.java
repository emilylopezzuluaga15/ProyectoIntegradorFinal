package com.example.gestionacademica.application.Services;

import com.example.gestionacademica.domain.models.Usuario;

import java.util.List;

public interface UsuarioServices {

    Usuario crear(Usuario usuario, String password);

    Usuario obtenerPorId(Long id);

    Usuario obtenerPorUsername(String username);

    List<Usuario> obtenerTodos();

    // password puede ser null para conservar la actual; idUsuarioActual es quien hace el cambio
    Usuario actualizar(Long id, Usuario datos, String password, Long idUsuarioActual);

    void eliminar(Long id, Long idUsuarioActual);
}
