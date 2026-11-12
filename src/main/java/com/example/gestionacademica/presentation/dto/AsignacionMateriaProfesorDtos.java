package com.example.gestionacademica.presentation.dto;

import com.example.gestionacademica.domain.models.AsignacionMateriaProfesor;
import com.example.gestionacademica.presentation.mapper.Referencias;
import jakarta.validation.constraints.NotBlank;

public final class AsignacionMateriaProfesorDtos {

    private AsignacionMateriaProfesorDtos() {
    }

    public record AsignacionRequest(
            @NotBlank(message = "idProfesor es obligatorio")
            String idProfesor,

            @NotBlank(message = "idGrupo es obligatorio")
            String idGrupo,

            @NotBlank(message = "idMateria es obligatorio")
            String idMateria) {

        public AsignacionMateriaProfesor toEntity(Referencias ref) {
            return AsignacionMateriaProfesor.builder()
                    .profesor(ref.profesor(idProfesor))
                    .grupo(ref.grupo(idGrupo))
                    .materia(ref.materia(idMateria))
                    .build();
        }
    }

    public record AsignacionResponse(Long idAsignacion,
                                     String idProfesor, String nombreProfesor,
                                     String idGrupo, String nombreGrupo,
                                     String idMateria, String nombreMateria) {

        public static AsignacionResponse from(AsignacionMateriaProfesor a) {
            return new AsignacionResponse(a.getIdAsignacion(),
                    a.getProfesor().getIdProfesor(), a.getProfesor().getNombreProfesor(),
                    a.getGrupo().getIdGrupo(), a.getGrupo().getNombreGrupo(),
                    a.getMateria().getIdMateria(), a.getMateria().getNombreMateria());
        }
    }
}
