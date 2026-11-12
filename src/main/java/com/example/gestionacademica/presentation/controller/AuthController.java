package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Services.AuthServices;
import com.example.gestionacademica.application.Services.UsuarioServices;
import com.example.gestionacademica.presentation.dto.UsuarioDtos.LoginRequest;
import com.example.gestionacademica.presentation.dto.UsuarioDtos.LoginResponse;
import com.example.gestionacademica.presentation.dto.UsuarioDtos.UsuarioResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthServices authServices;
    private final UsuarioServices usuarioServices;

    public AuthController(AuthServices authServices, UsuarioServices usuarioServices) {
        this.authServices = authServices;
        this.usuarioServices = usuarioServices;
    }

    // Público: devuelve el token que se envía luego como "Authorization: Bearer <token>"
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return LoginResponse.from(authServices.login(request.username(), request.password()));
    }

    // Datos del usuario dueño del token
    @GetMapping("/me")
    public UsuarioResponse me(Authentication authentication) {
        return UsuarioResponse.from(usuarioServices.obtenerPorUsername(authentication.getName()));
    }
}
