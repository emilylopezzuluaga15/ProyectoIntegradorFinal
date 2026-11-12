package com.example.gestionacademica.application.Services;

import com.example.gestionacademica.domain.models.Materia;

import java.util.List;

public interface MateriaServices {

    Materia crear(Materia materia);

    Materia obtenerPorId(String id);

    List<Materia> obtenerTodos();

    Materia actualizar(String id, Materia datos);

    void eliminar(String id);
}
