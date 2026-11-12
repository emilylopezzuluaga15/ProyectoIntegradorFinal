package com.example.gestionacademica.application.Services;

import com.example.gestionacademica.domain.models.MovimientoNota;

import java.util.List;

public interface MovimientoNotaServices {

    MovimientoNota crear(MovimientoNota movimientoNota);

    MovimientoNota obtenerPorId(Long id);

    List<MovimientoNota> obtenerTodos();

    List<MovimientoNota> obtenerPorNota(Long idNota);

    MovimientoNota actualizar(Long id, MovimientoNota datos);

    void eliminar(Long id);
}
