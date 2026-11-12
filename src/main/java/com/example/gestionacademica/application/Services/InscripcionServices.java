package com.example.gestionacademica.application.Services;

import com.example.gestionacademica.domain.models.Inscripcion;

import java.util.List;
import java.util.Map;

public interface InscripcionServices {

    Inscripcion crear(Inscripcion inscripcion);

    Inscripcion obtenerPorId(Long id);

    List<Inscripcion> obtenerTodos();

    List<Inscripcion> obtenerPorEstudiante(String idEstudiante);

    List<Inscripcion> obtenerPorGrupo(String idGrupo);

    Map<String, Object> consultarCupo(String idGrupo, Integer anio);

    Inscripcion actualizar(Long id, Inscripcion datos);

    void eliminar(Long id);
}
