package com.example.gestionacademica.application.Services;

import com.example.gestionacademica.domain.models.Nota;

import java.util.List;
import java.util.Map;

public interface NotaServices {

    Nota crear(Nota nota);

    Nota obtenerPorId(Long id);

    List<Nota> obtenerTodos();

    List<Nota> obtenerPorEstudiante(String idEstudiante);

    Map<String, Object> calcularPromedio(String idEstudiante);

    Nota actualizar(Long id, Nota datos);

    void eliminar(Long id);

    void recalcularDefinitiva(Long idNota);
}
