package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Exceptions.AsignacionMateriaProfesorNotFoundException;
import com.example.gestionacademica.application.Exceptions.AsistenciaNotFoundException;
import com.example.gestionacademica.application.Exceptions.BusinessException;
import com.example.gestionacademica.application.Exceptions.CarreraNotFoundException;
import com.example.gestionacademica.application.Exceptions.CredencialesInvalidasException;
import com.example.gestionacademica.application.Exceptions.CuentaDesactivadaException;
import com.example.gestionacademica.application.Exceptions.EstudianteNotFoundException;
import com.example.gestionacademica.application.Exceptions.GrupoNotFoundException;
import com.example.gestionacademica.application.Exceptions.InscripcionNotFoundException;
import com.example.gestionacademica.application.Exceptions.MateriaNotFoundException;
import com.example.gestionacademica.application.Exceptions.MovimientoNotaNotFoundException;
import com.example.gestionacademica.application.Exceptions.NivelMateriaNotFoundException;
import com.example.gestionacademica.application.Exceptions.NivelNotFoundException;
import com.example.gestionacademica.application.Exceptions.NotaNotFoundException;
import com.example.gestionacademica.application.Exceptions.ProfesorNotFoundException;
import com.example.gestionacademica.application.Exceptions.UsuarioNotFoundException;
import com.example.gestionacademica.presentation.dto.CuerpoError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

// Todos los errores salen con CuerpoError: status, descripción en español, código de negocio y mensaje
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    private static final Map<Class<? extends RuntimeException>, String> CODIGOS_NO_ENCONTRADO = Map.ofEntries(
            Map.entry(CarreraNotFoundException.class, "CARRERA_NO_ENCONTRADA"),
            Map.entry(NivelNotFoundException.class, "NIVEL_NO_ENCONTRADO"),
            Map.entry(MateriaNotFoundException.class, "MATERIA_NO_ENCONTRADA"),
            Map.entry(NivelMateriaNotFoundException.class, "NIVEL_MATERIA_NO_ENCONTRADO"),
            Map.entry(ProfesorNotFoundException.class, "PROFESOR_NO_ENCONTRADO"),
            Map.entry(GrupoNotFoundException.class, "GRUPO_NO_ENCONTRADO"),
            Map.entry(AsignacionMateriaProfesorNotFoundException.class, "ASIGNACION_NO_ENCONTRADA"),
            Map.entry(EstudianteNotFoundException.class, "ESTUDIANTE_NO_ENCONTRADO"),
            Map.entry(InscripcionNotFoundException.class, "INSCRIPCION_NO_ENCONTRADA"),
            Map.entry(AsistenciaNotFoundException.class, "ASISTENCIA_NO_ENCONTRADA"),
            Map.entry(NotaNotFoundException.class, "NOTA_NO_ENCONTRADA"),
            Map.entry(MovimientoNotaNotFoundException.class, "MOVIMIENTO_NO_ENCONTRADO"),
            Map.entry(UsuarioNotFoundException.class, "USUARIO_NO_ENCONTRADO"));

    @ExceptionHandler({
            CarreraNotFoundException.class,
            NivelNotFoundException.class,
            MateriaNotFoundException.class,
            NivelMateriaNotFoundException.class,
            ProfesorNotFoundException.class,
            GrupoNotFoundException.class,
            AsignacionMateriaProfesorNotFoundException.class,
            EstudianteNotFoundException.class,
            InscripcionNotFoundException.class,
            AsistenciaNotFoundException.class,
            NotaNotFoundException.class,
            MovimientoNotaNotFoundException.class,
            UsuarioNotFoundException.class
    })
    public ResponseEntity<Map<String, Object>> handleNotFound(RuntimeException ex) {
        return respuesta(HttpStatus.NOT_FOUND, CODIGOS_NO_ENCONTRADO.getOrDefault(ex.getClass(), "NO_ENCONTRADO"), ex.getMessage());
    }

    // Ruta que no existe
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResource(NoResourceFoundException ex) {
        return respuesta(HttpStatus.NOT_FOUND, "RUTA_NO_ENCONTRADA", "La ruta /" + ex.getResourcePath() + " no existe");
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusiness(BusinessException ex) {
        return respuesta(HttpStatus.CONFLICT, ex.getCodigo(), ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        return respuesta(HttpStatus.CONFLICT, "INTEGRIDAD_DATOS",
                "Violación de integridad de datos: revisa las referencias (llaves foráneas) o campos únicos/obligatorios.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = CuerpoError.crear(HttpStatus.BAD_REQUEST, "VALIDACION", "Campos inválidos u obligatorios faltantes");
        Map<String, String> errores = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }
        body.put("errores", errores);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleNotReadable(HttpMessageNotReadableException ex) {
        return respuesta(HttpStatus.BAD_REQUEST, "CUERPO_INVALIDO",
                "Cuerpo de la petición inválido o mal formado (revisa tipos y valores de ENUM).");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParam(MissingServletRequestParameterException ex) {
        return respuesta(HttpStatus.BAD_REQUEST, "PARAMETRO_FALTANTE", "Falta el parámetro obligatorio: " + ex.getParameterName());
    }

    // p. ej. /api/notas/abc cuando se espera un número
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return respuesta(HttpStatus.BAD_REQUEST, "PARAMETRO_INVALIDO",
                "El valor '" + ex.getValue() + "' no es válido para el parámetro '" + ex.getName() + "'");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return respuesta(HttpStatus.METHOD_NOT_ALLOWED, "METODO_NO_PERMITIDO",
                "El método " + ex.getMethod() + " no está permitido en esta ruta");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMediaType(HttpMediaTypeNotSupportedException ex) {
        return respuesta(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "TIPO_CONTENIDO_NO_SOPORTADO",
                "Tipo de contenido no soportado: envía el cuerpo como application/json");
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, Object>> handleCredenciales(CredencialesInvalidasException ex) {
        return respuesta(HttpStatus.UNAUTHORIZED, "CREDENCIALES_INVALIDAS", ex.getMessage());
    }

    @ExceptionHandler(CuentaDesactivadaException.class)
    public ResponseEntity<Map<String, Object>> handleCuentaDesactivada(CuentaDesactivadaException ex) {
        return respuesta(HttpStatus.FORBIDDEN, "CUENTA_DESACTIVADA", ex.getMessage());
    }

    // Lo lanza @PreAuthorize cuando el rol no alcanza
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        return respuesta(HttpStatus.FORBIDDEN, "SIN_PERMISO", "Tu rol no tiene permiso para esta operación");
    }

    // El detalle queda en el log; al cliente no se le exponen mensajes internos
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        log.error("Error no controlado", ex);
        return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR_INTERNO",
                "Ocurrió un error interno. Intenta de nuevo o contacta al administrador.");
    }

    private ResponseEntity<Map<String, Object>> respuesta(HttpStatus status, String codigo, String mensaje) {
        return ResponseEntity.status(status).body(CuerpoError.crear(status, codigo, mensaje));
    }
}
