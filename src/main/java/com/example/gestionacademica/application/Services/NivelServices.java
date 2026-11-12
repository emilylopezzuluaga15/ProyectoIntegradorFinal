package com.example.gestionacademica.application.Services;

import com.example.gestionacademica.domain.models.Nivel;

import java.util.List;

public interface NivelServices {

    Nivel crear(Nivel nivel);

    Nivel obtenerPorId(String id);

    List<Nivel> obtenerTodos();

    Nivel actualizar(String id, Nivel datos);

    void eliminar(String id);
}
