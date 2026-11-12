package com.example.gestionacademica.application.Services;

import com.example.gestionacademica.domain.models.Usuario;

import java.time.Instant;

// Resultado de un inicio de sesión exitoso
public record Sesion(String token, Instant expiraEn, Usuario usuario) {
}
