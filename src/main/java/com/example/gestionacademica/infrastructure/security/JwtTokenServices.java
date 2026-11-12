package com.example.gestionacademica.infrastructure.security;

import com.example.gestionacademica.application.Services.Sesion;
import com.example.gestionacademica.application.Services.TokenServices;
import com.example.gestionacademica.domain.models.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JwtTokenServices implements TokenServices {

    private final JwtEncoder jwtEncoder;
    private final long expiracionMinutos;

    public JwtTokenServices(JwtEncoder jwtEncoder,
                            @Value("${app.jwt.expiracion-minutos}") long expiracionMinutos) {
        this.jwtEncoder = jwtEncoder;
        this.expiracionMinutos = expiracionMinutos;
    }

    // El token lleva el rol y, si aplica, el estudiante o profesor vinculado (los usa AccesoService)
    @Override
    public Sesion generar(Usuario usuario) {
        Instant ahora = Instant.now();
        Instant expiraEn = ahora.plus(expiracionMinutos, ChronoUnit.MINUTES);

        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer("gestion-academica")
                .issuedAt(ahora)
                .expiresAt(expiraEn)
                .subject(usuario.getUsername())
                .claim(AccesoService.CLAIM_USUARIO, usuario.getIdUsuario())
                .claim(AccesoService.CLAIM_ROL, usuario.getRol().name());
        if (usuario.getEstudiante() != null) {
            claims.claim(AccesoService.CLAIM_ESTUDIANTE, usuario.getEstudiante().getIdEstudiante());
        }
        if (usuario.getProfesor() != null) {
            claims.claim(AccesoService.CLAIM_PROFESOR, usuario.getProfesor().getIdProfesor());
        }

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims.build())).getTokenValue();
        return new Sesion(token, expiraEn, usuario);
    }
}
