package com.example.gestionacademica.application.Services;

import java.util.Map;

public interface ReporteServices {

    Map<String, Object> boletin(String idEstudiante);

    Map<String, Object> estudiantesEnRiesgo(String idGrupo, Integer anio);

    Map<String, Object> planillaNotas(String idGrupo, String idMateria, Integer anio);
}
