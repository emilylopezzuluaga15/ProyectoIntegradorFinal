package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Services.MateriaServices;
import com.example.gestionacademica.presentation.dto.AlCrear;
import com.example.gestionacademica.presentation.dto.MateriaDtos.MateriaRequest;
import com.example.gestionacademica.presentation.dto.MateriaDtos.MateriaResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Consulta: cualquier usuario autenticado. Cambios: ADMINISTRATIVO y COORDINADOR
@RestController
@RequestMapping("/api/materias")
public class MateriaController {

    private final MateriaServices materiaServices;

    public MateriaController(MateriaServices materiaServices) {
        this.materiaServices = materiaServices;
    }

    @PostMapping
    @PreAuthorize("@acceso.esGestor(authentication)")
    public ResponseEntity<MateriaResponse> crear(@Validated(AlCrear.class) @RequestBody MateriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MateriaResponse.from(materiaServices.crear(request.toEntity())));
    }

    @GetMapping
    public List<MateriaResponse> listar() {
        return materiaServices.obtenerTodos().stream().map(MateriaResponse::from).toList();
    }

    @GetMapping("/{id}")
    public MateriaResponse obtener(@PathVariable String id) {
        return MateriaResponse.from(materiaServices.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@acceso.esGestor(authentication)")
    public MateriaResponse actualizar(@PathVariable String id, @Valid @RequestBody MateriaRequest request) {
        return MateriaResponse.from(materiaServices.actualizar(id, request.toEntity()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@acceso.esGestor(authentication)")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        materiaServices.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
