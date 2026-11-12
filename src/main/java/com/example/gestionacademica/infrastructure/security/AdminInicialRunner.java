package com.example.gestionacademica.infrastructure.security;

import com.example.gestionacademica.domain.models.Rol;
import com.example.gestionacademica.domain.models.Usuario;
import com.example.gestionacademica.domain.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Sin un ADMINISTRATIVO nadie podría crear cuentas: si no existe ninguno se crea al arrancar
@Component
public class AdminInicialRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInicialRunner.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String username;
    private final String password;

    public AdminInicialRunner(UsuarioRepository usuarioRepository,
                              PasswordEncoder passwordEncoder,
                              @Value("${app.admin.username}") String username,
                              @Value("${app.admin.password}") String password) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            if (usuarioRepository.existsByRol(Rol.ADMINISTRATIVO)) {
                return;
            }
            if (password == null || password.isBlank()) {
                log.warn("No hay ningún ADMINISTRATIVO y app.admin.password está vacío: no se creó el administrador inicial");
                return;
            }
            usuarioRepository.save(Usuario.builder()
                    .username(username)
                    .passwordHash(passwordEncoder.encode(password))
                    .rol(Rol.ADMINISTRATIVO)
                    .activo(true)
                    .build());
            log.info("Usuario administrativo inicial creado: {}", username);
        } catch (DataAccessException ex) {
            log.error("No se pudo revisar la tabla usuarios. ¿Ejecutaste db/schema.sql? {}",
                    ex.getMostSpecificCause().getMessage());
        }
    }
}
