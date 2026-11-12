package com.example.gestionacademica.application.Services;

import com.example.gestionacademica.domain.models.Profesor;

import java.util.List;

public interface ProfesorServices {

    Profesor crear(Profesor profesor);

    Profesor obtenerPorId(String id);

    List<Profesor> obtenerTodos();

    Profesor actualizar(String id, Profesor datos);

    void eliminar(String id);
}
