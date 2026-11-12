package com.example.gestionacademica.domain.models;

import jakarta.persistence.*;
import lombok.*;

// Cuenta de acceso al sistema. Un ESTUDIANTE o PROFESOR queda vinculado a su registro académico
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Usuario")
    private Long idUsuario;

    @Column(name = "Username", length = 50, nullable = false, unique = true)
    private String username;

    // Hash BCrypt, nunca la contraseña en texto plano
    @Column(name = "Password_Hash", length = 100, nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "Rol", nullable = false)
    private Rol rol;

    @Column(name = "Activo", nullable = false)
    private Boolean activo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Estudiante")
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Profesor")
    private Profesor profesor;

}
