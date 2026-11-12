package com.example.gestionacademica.domain.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "movimiento_notas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoNota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Movimiento")
    private Long idMovimiento;

    @NotNull(message = "fechaRegistro es obligatorio")
    @Column(name = "Fecha_Registro")
    private LocalDate fechaRegistro;

    @NotNull(message = "calificacion es obligatorio")
    @DecimalMin(value = "0.0", message = "calificacion debe estar entre 0.0 y 5.0")
    @DecimalMax(value = "5.0", message = "calificacion debe estar entre 0.0 y 5.0")
    @Column(name = "Calificacion")
    private Float calificacion;

    @NotNull(message = "nota es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Nota")
    private Nota nota;

}
