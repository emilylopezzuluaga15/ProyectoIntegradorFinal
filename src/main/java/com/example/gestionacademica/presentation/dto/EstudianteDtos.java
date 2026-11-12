package com.example.gestionacademica.presentation.dto;

import com.example.gestionacademica.domain.models.EstadoEstudiante;
import com.example.gestionacademica.domain.models.Estudiante;
import com.example.gestionacademica.domain.models.Genero;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public final class EstudianteDtos {

    private EstudianteDtos() {
    }

    public record EstudianteRequest(
            @NotBlank(groups = AlCrear.class, message = "idEstudiante es obligatorio")
            @Size(max = 20, message = "idEstudiante no puede superar 20 caracteres")
            String idEstudiante,

            @NotBlank(message = "nombreEstudiante es obligatorio")
            @Size(max = 50, message = "nombreEstudiante no puede superar 50 caracteres")
            String nombreEstudiante,

            @NotNull(message = "fechaIngreso es obligatorio")
            @PastOrPresent(message = "fechaIngreso no puede ser una fecha futura")
            LocalDate fechaIngreso,

            Genero genero,

            Long celular,

            @Email(message = "correo debe ser un correo válido")
            @Size(max = 50, message = "correo no puede superar 50 caracteres")
            String correo,

            @NotNull(message = "activo es obligatorio")
            Boolean activo,

            EstadoEstudiante estado) {

        public Estudiante toEntity() {
            return Estudiante.builder()
                    .idEstudiante(idEstudiante)
                    .nombreEstudiante(nombreEstudiante)
                    .fechaIngreso(fechaIngreso)
                    .genero(genero)
                    .celular(celular)
                    .correo(correo)
                    .activo(activo)
                    .estado(estado)
                    .build();
        }
    }

    public record EstudianteResponse(String idEstudiante, String nombreEstudiante, LocalDate fechaIngreso,
                                     Genero genero, Long celular, String correo,
                                     Boolean activo, EstadoEstudiante estado) {

        public static EstudianteResponse from(Estudiante e) {
            return new EstudianteResponse(e.getIdEstudiante(), e.getNombreEstudiante(), e.getFechaIngreso(),
                    e.getGenero(), e.getCelular(), e.getCorreo(), e.getActivo(), e.getEstado());
        }
    }
}
