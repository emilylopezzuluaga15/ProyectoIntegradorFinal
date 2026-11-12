package com.example.gestionacademica.presentation.dto;

import com.example.gestionacademica.application.Services.Sesion;
import com.example.gestionacademica.domain.models.Rol;
import com.example.gestionacademica.domain.models.Usuario;
import com.example.gestionacademica.presentation.mapper.Referencias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public final class UsuarioDtos {

    private UsuarioDtos() {
    }

    public record LoginRequest(
            @NotBlank(message = "username es obligatorio")
            String username,

            @NotBlank(message = "password es obligatorio")
            String password) {
    }

    public record LoginResponse(String token, String tipo, Instant expiraEn, UsuarioResponse usuario) {

        public static LoginResponse from(Sesion sesion) {
            return new LoginResponse(sesion.token(), "Bearer", sesion.expiraEn(), UsuarioResponse.from(sesion.usuario()));
        }
    }

    // password obligatorio al crear; al actualizar, si no se envía se conserva la actual
    public record UsuarioRequest(
            @NotBlank(message = "username es obligatorio")
            @Size(max = 50, message = "username no puede superar 50 caracteres")
            String username,

            @NotBlank(groups = AlCrear.class, message = "password es obligatorio")
            @Size(min = 8, max = 72, message = "password debe tener entre 8 y 72 caracteres")
            String password,

            @NotNull(message = "rol es obligatorio (ESTUDIANTE, PROFESOR, COORDINADOR o ADMINISTRATIVO)")
            Rol rol,

            @NotNull(message = "activo es obligatorio")
            Boolean activo,

            // Obligatorio solo si rol = ESTUDIANTE
            String idEstudiante,

            // Obligatorio solo si rol = PROFESOR
            String idProfesor) {

        public Usuario toEntity(Referencias ref) {
            return Usuario.builder()
                    .username(username)
                    .rol(rol)
                    .activo(activo)
                    .estudiante(ref.estudianteOpcional(idEstudiante))
                    .profesor(ref.profesorOpcional(idProfesor))
                    .build();
        }
    }

    // Nunca se devuelve el hash de la contraseña
    public record UsuarioResponse(Long idUsuario, String username, Rol rol, Boolean activo,
                                  String idEstudiante, String idProfesor) {

        public static UsuarioResponse from(Usuario u) {
            return new UsuarioResponse(u.getIdUsuario(), u.getUsername(), u.getRol(), u.getActivo(),
                    u.getEstudiante() == null ? null : u.getEstudiante().getIdEstudiante(),
                    u.getProfesor() == null ? null : u.getProfesor().getIdProfesor());
        }
    }
}
