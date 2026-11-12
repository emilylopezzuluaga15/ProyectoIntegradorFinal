package com.example.gestionacademica.application.Services;

import com.example.gestionacademica.domain.models.Carrera;

import java.util.List;

public interface CarreraServices {

    Carrera crear(Carrera carrera);

    Carrera obtenerPorId(String id);

    List<Carrera> obtenerTodos();

    Carrera actualizar(String id, Carrera datos);

    void eliminar(String id);
}
