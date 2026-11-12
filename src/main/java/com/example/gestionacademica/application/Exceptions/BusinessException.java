package com.example.gestionacademica.application.Exceptions;

// Regla de negocio incumplida (409). "codigo" es estable para que el front pueda reaccionar sin leer el mensaje
public class BusinessException extends RuntimeException {

    public static final String CODIGO_GENERICO = "REGLA_NEGOCIO";

    private final String codigo;

    public BusinessException(String message) {
        this(CODIGO_GENERICO, message);
    }

    public BusinessException(String codigo, String message) {
        super(message);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
