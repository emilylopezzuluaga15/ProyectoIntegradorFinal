package com.example.gestionacademica.presentation.dto;

import com.example.gestionacademica.domain.models.Grupo;
import com.example.gestionacademica.presentation.mapper.Referencias;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class GrupoDtos {

    private GrupoDtos() {
    }

    public record GrupoRequest(
            @NotBlank(groups = AlCrear.class, message = "idGrupo es obligatorio")
            @Size(max = 20, message = "idGrupo no puede superar 20 caracteres")
            String idGrupo,

            @NotBlank(message = "nombreGrupo es obligatorio")
            @Size(max = 50, message = "nombreGrupo no puede superar 50 caracteres")
            String nombreGrupo,

            @NotBlank(message = "idNivel es obligatorio")
            String idNivel,

            // null = grupo sin límite de cupo
            @Min(value = 1, message = "cupoMaximo debe ser mayor a 0")
            Integer cupoMaximo) {

        public Grupo toEntity(Referencias ref) {
            return Grupo.builder()
                    .idGrupo(idGrupo)
                    .nombreGrupo(nombreGrupo)
                    .nivel(ref.nivel(idNivel))
                    .cupoMaximo(cupoMaximo)
                    .build();
        }
    }

    public record GrupoResponse(String idGrupo, String nombreGrupo,
                                String idNivel, String nombreNivel,
                                Integer cupoMaximo) {

        public static GrupoResponse from(Grupo grupo) {
            return new GrupoResponse(grupo.getIdGrupo(), grupo.getNombreGrupo(),
                    grupo.getNivel().getIdNivel(), grupo.getNivel().getNombreNivel(),
                    grupo.getCupoMaximo());
        }
    }
}
