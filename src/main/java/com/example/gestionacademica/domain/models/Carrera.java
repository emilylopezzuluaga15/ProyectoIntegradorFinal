package com.example.gestionacademica.domain.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;


@Entity
@Table(name = "carreras")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carrera {

    @Id
    @NotBlank(message = "idCarrera es obligatorio")
    @Size(max = 20, message = "idCarrera no puede superar 20 caracteres")
    @Column(name = "ID_Carrera", length = 20)
    private String idCarrera;

    @NotBlank(message = "nombreCarrera es obligatorio")
    @Size(max = 50, message = "nombreCarrera no puede superar 50 caracteres")
    @Column(name = "Nombre_Carrera", length = 50)
    private String nombreCarrera;

}
