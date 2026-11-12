package com.example.gestionacademica.domain.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;


@Entity
@Table(name = "niveles_materias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NivelMateria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Nivel_Materia")
    private Long idNivelMateria;

    @NotNull(message = "nivel es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Nivel")
    private Nivel nivel;

    @NotNull(message = "materia es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Materia")
    private Materia materia;

}
