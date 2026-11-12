package com.example.gestionacademica.application.Services.impl;

import com.example.gestionacademica.application.Exceptions.MovimientoNotaNotFoundException;
import com.example.gestionacademica.application.Services.MovimientoNotaServices;
import com.example.gestionacademica.application.Services.NotaServices;
import com.example.gestionacademica.domain.models.MovimientoNota;
import com.example.gestionacademica.domain.models.Nota;
import com.example.gestionacademica.domain.repository.MovimientoNotaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MovimientoNotaServicesImpl implements MovimientoNotaServices {

    private final MovimientoNotaRepository movimientoNotaRepository;
    private final NotaServices notaServices;

    public MovimientoNotaServicesImpl(MovimientoNotaRepository movimientoNotaRepository, NotaServices notaServices) {
        this.movimientoNotaRepository = movimientoNotaRepository;
        this.notaServices = notaServices;
    }

    @Override
    public MovimientoNota crear(MovimientoNota movimientoNota) {
        Nota nota = notaServices.obtenerPorId(movimientoNota.getNota().getIdNota());
        movimientoNota.setNota(nota);
        MovimientoNota guardado = movimientoNotaRepository.save(movimientoNota);
        notaServices.recalcularDefinitiva(nota.getIdNota());
        return guardado;
    }

    @Override
    public MovimientoNota obtenerPorId(Long id) {
        return movimientoNotaRepository.findById(id)
                .orElseThrow(() -> new MovimientoNotaNotFoundException("Movimiento de nota no encontrado con id: " + id));
    }

    @Override
    public List<MovimientoNota> obtenerTodos() {
        return movimientoNotaRepository.findAll();
    }

    @Override
    public List<MovimientoNota> obtenerPorNota(Long idNota) {
        notaServices.obtenerPorId(idNota); // lanza 404 si la nota no existe
        return movimientoNotaRepository.findByNotaIdNota(idNota);
    }

    @Override
    public MovimientoNota actualizar(Long id, MovimientoNota datos) {
        MovimientoNota existente = obtenerPorId(id);
        Long idNotaAnterior = existente.getNota().getIdNota();
        Nota notaNueva = notaServices.obtenerPorId(datos.getNota().getIdNota());

        existente.setFechaRegistro(datos.getFechaRegistro());
        existente.setCalificacion(datos.getCalificacion());
        existente.setNota(notaNueva);
        MovimientoNota guardado = movimientoNotaRepository.save(existente);

        notaServices.recalcularDefinitiva(notaNueva.getIdNota());
        // Si el movimiento se pasó a otra nota, la anterior también cambia su promedio
        if (!idNotaAnterior.equals(notaNueva.getIdNota())) {
            notaServices.recalcularDefinitiva(idNotaAnterior);
        }
        return guardado;
    }

    @Override
    public void eliminar(Long id) {
        MovimientoNota existente = obtenerPorId(id);
        Long idNota = existente.getNota().getIdNota();
        movimientoNotaRepository.deleteById(id);
        notaServices.recalcularDefinitiva(idNota);
    }
}
