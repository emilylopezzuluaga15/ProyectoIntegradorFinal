package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Services.GrupoServices;
import com.example.gestionacademica.application.Services.InscripcionServices;
import com.example.gestionacademica.presentation.dto.AlCrear;
import com.example.gestionacademica.presentation.dto.GrupoDtos.GrupoRequest;
import com.example.gestionacademica.presentation.dto.GrupoDtos.GrupoResponse;
import com.example.gestionacademica.presentation.mapper.Referencias;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

// Consulta: cualquier usuario autenticado. Cambios: ADMINISTRATIVO y COORDINADOR
@RestController
@RequestMapping("/api/grupos")
public class GrupoController {

    private final GrupoServices grupoServices;
    private final InscripcionServices inscripcionServices;
    private final Referencias referencias;

    public GrupoController(GrupoServices grupoServices, InscripcionServices inscripcionServices, Referencias referencias) {
        this.grupoServices = grupoServices;
        this.inscripcionServices = inscripcionServices;
        this.referencias = referencias;
    }

    @PostMapping
    @PreAuthorize("@acceso.esGestor(authentication)")
    public ResponseEntity<GrupoResponse> crear(@Validated(AlCrear.class) @RequestBody GrupoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GrupoResponse.from(grupoServices.crear(request.toEntity(referencias))));
    }

    @GetMapping
    public List<GrupoResponse> listar() {
        return grupoServices.obtenerTodos().stream().map(GrupoResponse::from).toList();
    }

    @GetMapping("/{id}")
    public GrupoResponse obtener(@PathVariable String id) {
        return GrupoResponse.from(grupoServices.obtenerPorId(id));
    }

    // Cupo del grupo para un año (por defecto el año actual)
    @GetMapping("/{id}/cupo")
    public Map<String, Object> cupo(@PathVariable String id, @RequestParam(required = false) Integer anio) {
        if (anio == null) {
            anio = LocalDate.now().getYear();
        }
        return inscripcionServices.consultarCupo(id, anio);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@acceso.esGestor(authentication)")
    public GrupoResponse actualizar(@PathVariable String id, @Valid @RequestBody GrupoRequest request) {
        return GrupoResponse.from(grupoServices.actualizar(id, request.toEntity(referencias)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@acceso.esGestor(authentication)")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        grupoServices.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
