package com.example.gestionacademica.presentation.dto;

import com.example.gestionacademica.domain.models.Inscripcion;
import com.example.gestionacademica.presentation.mapper.Referencias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class InscripcionDtos {

    private InscripcionDtos() {
    }

    public record InscripcionRequest(
            @NotNull(message = "anioInscripcion es obligatorio")
            Integer anioInscripcion,

            Long cohorte,

            @NotBlank(message = "idEstudiante es obligatorio")
            String idEstudiante,

            @NotBlank(message = "idGrupo es obligatorio")
            String idGrupo) {

        public Inscripcion toEntity(Referencias ref) {
            return Inscripcion.builder()
                    .anioInscripcion(anioInscripcion)
                    .cohorte(cohorte)
                    .estudiante(ref.estudiante(idEstudiante))
                    .grupo(ref.grupo(idGrupo))
                    .build();
        }
    }

    public record InscripcionResponse(Long idInscripcion, Integer anioInscripcion, Long cohorte,
                                      String idEstudiante, String nombreEstudiante,
                                      String idGrupo, String nombreGrupo) {

        public static InscripcionResponse from(Inscripcion i) {
            return new InscripcionResponse(i.getIdInscripcion(), i.getAnioInscripcion(), i.getCohorte(),
                    i.getEstudiante().getIdEstudiante(), i.getEstudiante().getNombreEstudiante(),
                    i.getGrupo().getIdGrupo(), i.getGrupo().getNombreGrupo());
        }
    }
}
