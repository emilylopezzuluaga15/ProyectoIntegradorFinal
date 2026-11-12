package com.example.gestionacademica.infrastructure.security;

import com.example.gestionacademica.presentation.dto.CuerpoError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// 401/403 que ocurren en los filtros de seguridad, con el mismo formato JSON que ApiExceptionHandler
@Component
public class ErroresSeguridadHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final JsonMapper jsonMapper;

    public ErroresSeguridadHandler(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    // Sin cabecera Authorization: no inició sesión; con cabecera: el token no sirve (mal formado, firma o vencido)
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException ex) throws IOException {
        if (request.getHeader(HttpHeaders.AUTHORIZATION) == null) {
            escribir(response, HttpStatus.UNAUTHORIZED, "NO_AUTENTICADO", "Debes iniciar sesión para usar este recurso");
        } else {
            escribir(response, HttpStatus.UNAUTHORIZED, "TOKEN_INVALIDO", "El token es inválido o está vencido: inicia sesión de nuevo");
        }
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {
        escribir(response, HttpStatus.FORBIDDEN, "SIN_PERMISO", "Tu rol no tiene permiso para esta operación");
    }

    private void escribir(HttpServletResponse response, HttpStatus status, String codigo, String mensaje) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(jsonMapper.writeValueAsString(CuerpoError.crear(status, codigo, mensaje)));
    }
}
