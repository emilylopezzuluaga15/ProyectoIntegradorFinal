package com.example.gestionacademica.application.Services;

import com.example.gestionacademica.domain.models.Asistencia;

import java.util.List;
import java.util.Map;

public interface AsistenciaServices {

    Asistencia crear(Asistencia asistencia);

    Asistencia obtenerPorId(Long id);

    List<Asistencia> obtenerTodos();

    List<Asistencia> obtenerPorEstudiante(String idEstudiante);

    Map<String, Object> resumenPorEstudiante(String idEstudiante);

    Map<String, Object> resumenPorEstudianteYGrupo(String idEstudiante, String idGrupo);

    Asistencia actualizar(Long id, Asistencia datos);

    void eliminar(Long id);
}
