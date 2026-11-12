package com.example.gestionacademica.presentation.dto;

import com.example.gestionacademica.domain.models.NivelMateria;
import com.example.gestionacademica.presentation.mapper.Referencias;
import jakarta.validation.constraints.NotBlank;

public final class NivelMateriaDtos {

    private NivelMateriaDtos() {
    }

    public record NivelMateriaRequest(
            @NotBlank(message = "idNivel es obligatorio")
            String idNivel,

            @NotBlank(message = "idMateria es obligatorio")
            String idMateria) {

        public NivelMateria toEntity(Referencias ref) {
            return NivelMateria.builder()
                    .nivel(ref.nivel(idNivel))
                    .materia(ref.materia(idMateria))
                    .build();
        }
    }

    public record NivelMateriaResponse(Long idNivelMateria,
                                       String idNivel, String nombreNivel,
                                       String idMateria, String nombreMateria) {

        public static NivelMateriaResponse from(NivelMateria nm) {
            return new NivelMateriaResponse(nm.getIdNivelMateria(),
                    nm.getNivel().getIdNivel(), nm.getNivel().getNombreNivel(),
                    nm.getMateria().getIdMateria(), nm.getMateria().getNombreMateria());
        }
    }
}
