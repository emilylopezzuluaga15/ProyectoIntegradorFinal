package com.example.gestionacademica.application.Services;

import com.example.gestionacademica.domain.models.Usuario;

// Puerto para emitir el token de acceso; la implementación (JWT) vive en infraestructura
public interface TokenServices {

    Sesion generar(Usuario usuario);
}
