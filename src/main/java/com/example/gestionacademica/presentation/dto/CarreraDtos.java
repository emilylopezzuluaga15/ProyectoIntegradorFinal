package com.example.gestionacademica.presentation.dto;

import com.example.gestionacademica.domain.models.Carrera;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class CarreraDtos {

    private CarreraDtos() {
    }

    public record CarreraRequest(
            @NotBlank(groups = AlCrear.class, message = "idCarrera es obligatorio")
            @Size(max = 20, message = "idCarrera no puede superar 20 caracteres")
            String idCarrera,

            @NotBlank(message = "nombreCarrera es obligatorio")
            @Size(max = 50, message = "nombreCarrera no puede superar 50 caracteres")
            String nombreCarrera) {

        public Carrera toEntity() {
            return Carrera.builder()
                    .idCarrera(idCarrera)
                    .nombreCarrera(nombreCarrera)
                    .build();
        }
    }

    public record CarreraResponse(String idCarrera, String nombreCarrera) {

        public static CarreraResponse from(Carrera carrera) {
            return new CarreraResponse(carrera.getIdCarrera(), carrera.getNombreCarrera());
        }
    }
}
