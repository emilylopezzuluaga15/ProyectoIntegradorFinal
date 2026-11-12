package com.example.gestionacademica.presentation.dto;

import com.example.gestionacademica.domain.models.Asistencia;
import com.example.gestionacademica.presentation.mapper.Referencias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public final class AsistenciaDtos {

    private AsistenciaDtos() {
    }

    public record AsistenciaRequest(
            @NotNull(message = "fechaAsistencia es obligatorio")
            LocalDate fechaAsistencia,

            @NotNull(message = "asistio es obligatorio")
            Boolean asistio,

            // Solo aplica a inasistencias (asistio = false)
            Boolean justificada,

            @Size(max = 100, message = "observacion no puede superar 100 caracteres")
            String observacion,

            @NotBlank(message = "idEstudiante es obligatorio")
            String idEstudiante,

            @NotBlank(message = "idGrupo es obligatorio")
            String idGrupo) {

        public Asistencia toEntity(Referencias ref) {
            return Asistencia.builder()
                    .fechaAsistencia(fechaAsistencia)
                    .asistio(asistio)
                    .justificada(justificada)
                    .observacion(observacion)
                    .estudiante(ref.estudiante(idEstudiante))
                    .grupo(ref.grupo(idGrupo))
                    .build();
        }
    }

    public record AsistenciaResponse(Long idAsistencia, LocalDate fechaAsistencia, Boolean asistio,
                                     Boolean justificada, String observacion,
                                     String idEstudiante, String nombreEstudiante,
                                     String idGrupo, String nombreGrupo) {

        public static AsistenciaResponse from(Asistencia a) {
            return new AsistenciaResponse(a.getIdAsistencia(), a.getFechaAsistencia(), a.getAsistio(),
                    a.getJustificada(), a.getObservacion(),
                    a.getEstudiante().getIdEstudiante(), a.getEstudiante().getNombreEstudiante(),
                    a.getGrupo().getIdGrupo(), a.getGrupo().getNombreGrupo());
        }
    }
}
