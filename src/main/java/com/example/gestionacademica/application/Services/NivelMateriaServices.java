package com.example.gestionacademica.application.Services;

import com.example.gestionacademica.domain.models.NivelMateria;

import java.util.List;

public interface NivelMateriaServices {

    NivelMateria crear(NivelMateria nivelMateria);

    NivelMateria obtenerPorId(Long id);

    List<NivelMateria> obtenerTodos();

    NivelMateria actualizar(Long id, NivelMateria datos);

    void eliminar(Long id);
}
