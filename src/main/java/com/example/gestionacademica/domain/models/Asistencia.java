package com.example.gestionacademica.domain.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "asistencia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Asistencia")
    private Long idAsistencia;

    @NotNull(message = "fechaAsistencia es obligatorio")
    @Column(name = "Fecha_Asistencia")
    private LocalDate fechaAsistencia;

    @NotNull(message = "asistio es obligatorio")
    @Column(name = "Asistio")
    private Boolean asistio;

    // Solo aplica a inasistencias (asistio = false): excusa médica, permiso, etc.
    @Column(name = "Justificada")
    private Boolean justificada;

    @Size(max = 100, message = "observacion no puede superar 100 caracteres")
    @Column(name = "Observacion", length = 100)
    private String observacion;

    @NotNull(message = "estudiante es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Estudiante")
    private Estudiante estudiante;

    @NotNull(message = "grupo es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Grupo")
    private Grupo grupo;

}
