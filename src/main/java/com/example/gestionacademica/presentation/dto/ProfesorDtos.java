package com.example.gestionacademica.presentation.dto;

import com.example.gestionacademica.domain.models.Genero;
import com.example.gestionacademica.domain.models.Profesor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public final class ProfesorDtos {

    private ProfesorDtos() {
    }

    public record ProfesorRequest(
            @NotBlank(groups = AlCrear.class, message = "idProfesor es obligatorio")
            @Size(max = 20, message = "idProfesor no puede superar 20 caracteres")
            String idProfesor,

            @NotBlank(message = "nombreProfesor es obligatorio")
            @Size(max = 50, message = "nombreProfesor no puede superar 50 caracteres")
            String nombreProfesor,

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

            @Size(max = 50, message = "especializacion no puede superar 50 caracteres")
            String especializacion) {

        public Profesor toEntity() {
            return Profesor.builder()
                    .idProfesor(idProfesor)
                    .nombreProfesor(nombreProfesor)
                    .fechaIngreso(fechaIngreso)
                    .genero(genero)
                    .celular(celular)
                    .correo(correo)
                    .activo(activo)
                    .especializacion(especializacion)
                    .build();
        }
    }

    public record ProfesorResponse(String idProfesor, String nombreProfesor, LocalDate fechaIngreso,
                                   Genero genero, Long celular, String correo,
                                   Boolean activo, String especializacion) {

        public static ProfesorResponse from(Profesor p) {
            return new ProfesorResponse(p.getIdProfesor(), p.getNombreProfesor(), p.getFechaIngreso(),
                    p.getGenero(), p.getCelular(), p.getCorreo(), p.getActivo(), p.getEspecializacion());
        }
    }
}
