package com.example.gestionacademica.domain.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "notas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Nota")
    private Long idNota;

    @DecimalMin(value = "0.0", message = "definitiva debe estar entre 0.0 y 5.0")
    @DecimalMax(value = "5.0", message = "definitiva debe estar entre 0.0 y 5.0")
    @Column(name = "Definitiva")
    private Float definitiva;

    @NotNull(message = "fechaRegistro es obligatorio")
    @Column(name = "Fecha_Registro")
    private LocalDate fechaRegistro;

    @Column(name = "Aprobada")
    private Boolean aprobada;

    @NotNull(message = "estudiante es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Estudiante")
    private Estudiante estudiante;

    @NotNull(message = "materia es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Materia")
    private Materia materia;

}
