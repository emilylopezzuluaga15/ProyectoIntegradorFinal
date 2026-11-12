package com.example.gestionacademica.application.Exceptions;

public class CuentaDesactivadaException extends RuntimeException {
    public CuentaDesactivadaException(String message) {
        super(message);
    }
}
