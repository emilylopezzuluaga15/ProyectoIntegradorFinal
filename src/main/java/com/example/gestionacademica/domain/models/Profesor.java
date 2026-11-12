package com.example.gestionacademica.domain.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "profesores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profesor {

    @Id
    @NotBlank(message = "idProfesor es obligatorio")
    @Size(max = 20, message = "idProfesor no puede superar 20 caracteres")
    @Column(name = "ID_Profesor", length = 20)
    private String idProfesor;

    @NotBlank(message = "nombreProfesor es obligatorio")
    @Size(max = 50, message = "nombreProfesor no puede superar 50 caracteres")
    @Column(name = "Nombre_Profesor", length = 50)
    private String nombreProfesor;

    @NotNull(message = "fechaIngreso es obligatorio")
    @PastOrPresent(message = "fechaIngreso no puede ser una fecha futura")
    @Column(name = "Fecha_Ingreso")
    private LocalDate fechaIngreso;

    @Enumerated(EnumType.STRING)
    @Column(name = "Genero")
    private Genero genero;

    @Column(name = "Celular")
    private Long celular;

    @Email(message = "correo debe ser un correo válido")
    @Size(max = 50, message = "correo no puede superar 50 caracteres")
    @Column(name = "Correo", length = 50)
    private String correo;

    @NotNull(message = "activo es obligatorio")
    @Column(name = "Activo")
    private Boolean activo;

    @Size(max = 50, message = "especializacion no puede superar 50 caracteres")
    @Column(name = "Especializacion", length = 50)
    private String especializacion;

}
