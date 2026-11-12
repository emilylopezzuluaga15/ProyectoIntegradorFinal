package com.example.gestionacademica.presentation.dto;

import com.example.gestionacademica.domain.models.MovimientoNota;
import com.example.gestionacademica.presentation.mapper.Referencias;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public final class MovimientoNotaDtos {

    private MovimientoNotaDtos() {
    }

    public record MovimientoNotaRequest(
            @NotNull(message = "fechaRegistro es obligatorio")
            LocalDate fechaRegistro,

            @NotNull(message = "calificacion es obligatorio")
            @DecimalMin(value = "0.0", message = "calificacion debe estar entre 0.0 y 5.0")
            @DecimalMax(value = "5.0", message = "calificacion debe estar entre 0.0 y 5.0")
            Float calificacion,

            @NotNull(message = "idNota es obligatorio")
            Long idNota) {

        public MovimientoNota toEntity(Referencias ref) {
            return MovimientoNota.builder()
                    .fechaRegistro(fechaRegistro)
                    .calificacion(calificacion)
                    .nota(ref.nota(idNota))
                    .build();
        }
    }

    public record MovimientoNotaResponse(Long idMovimiento, LocalDate fechaRegistro, Float calificacion,
                                         Long idNota, String idEstudiante, String idMateria) {

        public static MovimientoNotaResponse from(MovimientoNota m) {
            return new MovimientoNotaResponse(m.getIdMovimiento(), m.getFechaRegistro(), m.getCalificacion(),
                    m.getNota().getIdNota(),
                    m.getNota().getEstudiante().getIdEstudiante(), m.getNota().getMateria().getIdMateria());
        }
    }
}
