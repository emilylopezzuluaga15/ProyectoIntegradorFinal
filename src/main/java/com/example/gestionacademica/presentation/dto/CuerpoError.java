package com.example.gestionacademica.presentation.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

// Formato único de los errores de la API (también lo usan los 401/403 de los filtros de seguridad):
// {timestamp, status, error, codigo, message}; las validaciones agregan "errores": {campo: mensaje}
public final class CuerpoError {

    private CuerpoError() {
    }

    public static Map<String, Object> crear(HttpStatus status, String codigo, String mensaje) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", descripcion(status));
        body.put("codigo", codigo);
        body.put("message", mensaje);
        return body;
    }

    public static String descripcion(HttpStatus status) {
        return switch (status) {
            case BAD_REQUEST -> "Solicitud inválida";
            case UNAUTHORIZED -> "No autenticado";
            case FORBIDDEN -> "Acceso denegado";
            case NOT_FOUND -> "No encontrado";
            case METHOD_NOT_ALLOWED -> "Método no permitido";
            case CONFLICT -> "Conflicto";
            case UNSUPPORTED_MEDIA_TYPE -> "Tipo de contenido no soportado";
            case INTERNAL_SERVER_ERROR -> "Error interno del servidor";
            default -> status.getReasonPhrase();
        };
    }
}
