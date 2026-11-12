package com.example.gestionacademica.domain.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;


@Entity
@Table(name = "grupos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grupo {

    @Id
    @NotBlank(message = "idGrupo es obligatorio")
    @Size(max = 20, message = "idGrupo no puede superar 20 caracteres")
    @Column(name = "ID_Grupo", length = 20)
    private String idGrupo;

    @NotBlank(message = "nombreGrupo es obligatorio")
    @Size(max = 50, message = "nombreGrupo no puede superar 50 caracteres")
    @Column(name = "Nombre_Grupo", length = 50)
    private String nombreGrupo;

    @NotNull(message = "nivel es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Nivel")
    private Nivel nivel;

    // null = grupo sin límite de cupo
    @Min(value = 1, message = "cupoMaximo debe ser mayor a 0")
    @Column(name = "Cupo_Maximo")
    private Integer cupoMaximo;

}
