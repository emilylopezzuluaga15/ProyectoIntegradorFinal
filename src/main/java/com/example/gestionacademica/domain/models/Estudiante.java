package com.example.gestionacademica.domain.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "estudiantes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Estudiante {

    @Id
    @NotBlank(message = "idEstudiante es obligatorio")
    @Size(max = 20, message = "idEstudiante no puede superar 20 caracteres")
    @Column(name = "ID_Estudiante", length = 20)
    private String idEstudiante;

    @NotBlank(message = "nombreEstudiante es obligatorio")
    @Size(max = 50, message = "nombreEstudiante no puede superar 50 caracteres")
    @Column(name = "Nombre_Estudiante", length = 50)
    private String nombreEstudiante;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "Estado")
    private EstadoEstudiante estado;

}
