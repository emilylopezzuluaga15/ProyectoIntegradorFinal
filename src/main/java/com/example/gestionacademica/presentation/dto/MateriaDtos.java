package com.example.gestionacademica.presentation.dto;

import com.example.gestionacademica.domain.models.Materia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class MateriaDtos {

    private MateriaDtos() {
    }

    public record MateriaRequest(
            @NotBlank(groups = AlCrear.class, message = "idMateria es obligatorio")
            @Size(max = 20, message = "idMateria no puede superar 20 caracteres")
            String idMateria,

            @NotBlank(message = "nombreMateria es obligatorio")
            @Size(max = 50, message = "nombreMateria no puede superar 50 caracteres")
            String nombreMateria) {

        public Materia toEntity() {
            return Materia.builder()
                    .idMateria(idMateria)
                    .nombreMateria(nombreMateria)
                    .build();
        }
    }

    public record MateriaResponse(String idMateria, String nombreMateria) {

        public static MateriaResponse from(Materia materia) {
            return new MateriaResponse(materia.getIdMateria(), materia.getNombreMateria());
        }
    }
}
