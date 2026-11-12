package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Services.NivelMateriaServices;
import com.example.gestionacademica.presentation.dto.NivelMateriaDtos.NivelMateriaRequest;
import com.example.gestionacademica.presentation.dto.NivelMateriaDtos.NivelMateriaResponse;
import com.example.gestionacademica.presentation.mapper.Referencias;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Consulta: cualquier usuario autenticado. Cambios: ADMINISTRATIVO y COORDINADOR
@RestController
@RequestMapping("/api/niveles-materias")
public class NivelMateriaController {

    private final NivelMateriaServices nivelMateriaServices;
    private final Referencias referencias;

    public NivelMateriaController(NivelMateriaServices nivelMateriaServices, Referencias referencias) {
        this.nivelMateriaServices = nivelMateriaServices;
        this.referencias = referencias;
    }

    @PostMapping
    @PreAuthorize("@acceso.esGestor(authentication)")
    public ResponseEntity<NivelMateriaResponse> crear(@Valid @RequestBody NivelMateriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(NivelMateriaResponse.from(nivelMateriaServices.crear(request.toEntity(referencias))));
    }

    @GetMapping
    public List<NivelMateriaResponse> listar() {
        return nivelMateriaServices.obtenerTodos().stream().map(NivelMateriaResponse::from).toList();
    }

    @GetMapping("/{id}")
    public NivelMateriaResponse obtener(@PathVariable Long id) {
        return NivelMateriaResponse.from(nivelMateriaServices.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@acceso.esGestor(authentication)")
    public NivelMateriaResponse actualizar(@PathVariable Long id, @Valid @RequestBody NivelMateriaRequest request) {
        return NivelMateriaResponse.from(nivelMateriaServices.actualizar(id, request.toEntity(referencias)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@acceso.esGestor(authentication)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        nivelMateriaServices.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
