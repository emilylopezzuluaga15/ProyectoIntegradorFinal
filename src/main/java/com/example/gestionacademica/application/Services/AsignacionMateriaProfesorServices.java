package com.example.gestionacademica.application.Services;

import com.example.gestionacademica.domain.models.AsignacionMateriaProfesor;

import java.util.List;

public interface AsignacionMateriaProfesorServices {

    AsignacionMateriaProfesor crear(AsignacionMateriaProfesor asignacionMateriaProfesor);

    AsignacionMateriaProfesor obtenerPorId(Long id);

    List<AsignacionMateriaProfesor> obtenerTodos();

    List<AsignacionMateriaProfesor> obtenerPorProfesor(String idProfesor);

    AsignacionMateriaProfesor actualizar(Long id, AsignacionMateriaProfesor datos);

    void eliminar(Long id);
}
