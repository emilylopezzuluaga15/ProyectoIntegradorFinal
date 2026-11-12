package com.example.gestionacademica.presentation.dto;

import com.example.gestionacademica.domain.models.Nota;
import com.example.gestionacademica.presentation.mapper.Referencias;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public final class NotaDtos {

    private NotaDtos() {
    }

    // "aprobada" no se recibe: el servicio la calcula a partir de la definitiva
    public record NotaRequest(
            @DecimalMin(value = "0.0", message = "definitiva debe estar entre 0.0 y 5.0")
            @DecimalMax(value = "5.0", message = "definitiva debe estar entre 0.0 y 5.0")
            Float definitiva,

            @NotNull(message = "fechaRegistro es obligatorio")
            LocalDate fechaRegistro,

            @NotBlank(message = "idEstudiante es obligatorio")
            String idEstudiante,

            @NotBlank(message = "idMateria es obligatorio")
            String idMateria) {

        public Nota toEntity(Referencias ref) {
            return Nota.builder()
                    .definitiva(definitiva)
                    .fechaRegistro(fechaRegistro)
                    .estudiante(ref.estudiante(idEstudiante))
                    .materia(ref.materia(idMateria))
                    .build();
        }
    }

    public record NotaResponse(Long idNota, Float definitiva, LocalDate fechaRegistro, Boolean aprobada,
                               String idEstudiante, String nombreEstudiante,
                               String idMateria, String nombreMateria) {

        public static NotaResponse from(Nota n) {
            return new NotaResponse(n.getIdNota(), n.getDefinitiva(), n.getFechaRegistro(), n.getAprobada(),
                    n.getEstudiante().getIdEstudiante(), n.getEstudiante().getNombreEstudiante(),
                    n.getMateria().getIdMateria(), n.getMateria().getNombreMateria());
        }
    }
}
