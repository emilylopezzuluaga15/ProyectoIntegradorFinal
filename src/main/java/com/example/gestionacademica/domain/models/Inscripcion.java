package com.example.gestionacademica.domain.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;


@Entity
@Table(name = "inscripciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Inscripcion")
    private Long idInscripcion;

    @NotNull(message = "anioInscripcion es obligatorio")
    @Column(name = "anio_inscripcion")
    private Integer anioInscripcion;

    @Column(name = "cohorte")
    private Long cohorte;

    @NotNull(message = "estudiante es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Estudiante")
    private Estudiante estudiante;

    @NotNull(message = "grupo es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Grupo")
    private Grupo grupo;

}
