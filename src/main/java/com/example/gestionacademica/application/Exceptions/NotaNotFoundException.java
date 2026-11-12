package com.example.gestionacademica.application.Exceptions;

public class NotaNotFoundException extends RuntimeException {
    public NotaNotFoundException(String message) {
        super(message);
    }
}
