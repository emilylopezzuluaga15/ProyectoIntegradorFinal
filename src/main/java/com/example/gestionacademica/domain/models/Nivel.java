package com.example.gestionacademica.domain.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;


@Entity
@Table(name = "niveles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Nivel {

    @Id
    @NotBlank(message = "idNivel es obligatorio")
    @Size(max = 20, message = "idNivel no puede superar 20 caracteres")
    @Column(name = "ID_Nivel", length = 20)
    private String idNivel;

    @NotBlank(message = "nombreNivel es obligatorio")
    @Size(max = 50, message = "nombreNivel no puede superar 50 caracteres")
    @Column(name = "Nombre_Nivel", length = 50)
    private String nombreNivel;

    @NotNull(message = "carrera es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Carrera")
    private Carrera carrera;

}
