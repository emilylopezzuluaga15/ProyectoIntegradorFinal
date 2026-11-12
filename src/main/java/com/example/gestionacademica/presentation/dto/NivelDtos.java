package com.example.gestionacademica.presentation.dto;

import com.example.gestionacademica.domain.models.Nivel;
import com.example.gestionacademica.presentation.mapper.Referencias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class NivelDtos {

    private NivelDtos() {
    }

    public record NivelRequest(
            @NotBlank(groups = AlCrear.class, message = "idNivel es obligatorio")
            @Size(max = 20, message = "idNivel no puede superar 20 caracteres")
            String idNivel,

            @NotBlank(message = "nombreNivel es obligatorio")
            @Size(max = 50, message = "nombreNivel no puede superar 50 caracteres")
            String nombreNivel,

            @NotBlank(message = "idCarrera es obligatorio")
            String idCarrera) {

        public Nivel toEntity(Referencias ref) {
            return Nivel.builder()
                    .idNivel(idNivel)
                    .nombreNivel(nombreNivel)
                    .carrera(ref.carrera(idCarrera))
                    .build();
        }
    }

    public record NivelResponse(String idNivel, String nombreNivel, String idCarrera, String nombreCarrera) {

        public static NivelResponse from(Nivel nivel) {
            return new NivelResponse(nivel.getIdNivel(), nivel.getNombreNivel(),
                    nivel.getCarrera().getIdCarrera(), nivel.getCarrera().getNombreCarrera());
        }
    }
}
