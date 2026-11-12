package com.example.gestionacademica.application.Services;

import com.example.gestionacademica.domain.models.Grupo;

import java.util.List;

public interface GrupoServices {

    Grupo crear(Grupo grupo);

    Grupo obtenerPorId(String id);

    List<Grupo> obtenerTodos();

    Grupo actualizar(String id, Grupo datos);

    void eliminar(String id);
}
