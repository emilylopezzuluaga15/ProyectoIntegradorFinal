package com.example.gestionacademica.domain.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;


@Entity
@Table(name = "materias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Materia {

    @Id
    @NotBlank(message = "idMateria es obligatorio")
    @Size(max = 20, message = "idMateria no puede superar 20 caracteres")
    @Column(name = "ID_Materia", length = 20)
    private String idMateria;

    @NotBlank(message = "nombreMateria es obligatorio")
    @Size(max = 50, message = "nombreMateria no puede superar 50 caracteres")
    @Column(name = "Nombre_Materia", length = 50)
    private String nombreMateria;

}
