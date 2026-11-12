package com.example.gestionacademica.application.Services;

import com.example.gestionacademica.domain.models.Estudiante;

import java.util.List;

public interface EstudianteServices {

    Estudiante crear(Estudiante estudiante);

    Estudiante obtenerPorId(String id);

    List<Estudiante> obtenerTodos();

    Estudiante actualizar(String id, Estudiante datos);

    void eliminar(String id);
}
