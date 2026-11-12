package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Services.NivelServices;
import com.example.gestionacademica.presentation.dto.AlCrear;
import com.example.gestionacademica.presentation.dto.NivelDtos.NivelRequest;
import com.example.gestionacademica.presentation.dto.NivelDtos.NivelResponse;
import com.example.gestionacademica.presentation.mapper.Referencias;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Consulta: cualquier usuario autenticado. Cambios: ADMINISTRATIVO y COORDINADOR
@RestController
@RequestMapping("/api/niveles")
public class NivelController {

    private final NivelServices nivelServices;
    private final Referencias referencias;

    public NivelController(NivelServices nivelServices, Referencias referencias) {
        this.nivelServices = nivelServices;
        this.referencias = referencias;
    }

    @PostMapping
    @PreAuthorize("@acceso.esGestor(authentication)")
    public ResponseEntity<NivelResponse> crear(@Validated(AlCrear.class) @RequestBody NivelRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(NivelResponse.from(nivelServices.crear(request.toEntity(referencias))));
    }

    @GetMapping
    public List<NivelResponse> listar() {
        return nivelServices.obtenerTodos().stream().map(NivelResponse::from).toList();
    }

    @GetMapping("/{id}")
    public NivelResponse obtener(@PathVariable String id) {
        return NivelResponse.from(nivelServices.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@acceso.esGestor(authentication)")
    public NivelResponse actualizar(@PathVariable String id, @Valid @RequestBody NivelRequest request) {
        return NivelResponse.from(nivelServices.actualizar(id, request.toEntity(referencias)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@acceso.esGestor(authentication)")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        nivelServices.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
