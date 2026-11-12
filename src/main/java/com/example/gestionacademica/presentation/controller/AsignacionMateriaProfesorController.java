package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Services.AsignacionMateriaProfesorServices;
import com.example.gestionacademica.presentation.dto.AsignacionMateriaProfesorDtos.AsignacionRequest;
import com.example.gestionacademica.presentation.dto.AsignacionMateriaProfesorDtos.AsignacionResponse;
import com.example.gestionacademica.presentation.mapper.Referencias;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Consulta: cualquier usuario autenticado. Cambios: ADMINISTRATIVO y COORDINADOR
@RestController
@RequestMapping("/api/asignaciones-materia-profesor")
public class AsignacionMateriaProfesorController {

    private final AsignacionMateriaProfesorServices asignacionMateriaProfesorServices;
    private final Referencias referencias;

    public AsignacionMateriaProfesorController(AsignacionMateriaProfesorServices asignacionMateriaProfesorServices,
                                               Referencias referencias) {
        this.asignacionMateriaProfesorServices = asignacionMateriaProfesorServices;
        this.referencias = referencias;
    }

    @PostMapping
    @PreAuthorize("@acceso.esGestor(authentication)")
    public ResponseEntity<AsignacionResponse> crear(@Valid @RequestBody AsignacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AsignacionResponse.from(asignacionMateriaProfesorServices.crear(request.toEntity(referencias))));
    }

    @GetMapping
    public List<AsignacionResponse> listar() {
        return asignacionMateriaProfesorServices.obtenerTodos().stream().map(AsignacionResponse::from).toList();
    }

    @GetMapping("/profesor/{idProfesor}")
    public List<AsignacionResponse> listarPorProfesor(@PathVariable String idProfesor) {
        return asignacionMateriaProfesorServices.obtenerPorProfesor(idProfesor).stream()
                .map(AsignacionResponse::from).toList();
    }

    @GetMapping("/{id}")
    public AsignacionResponse obtener(@PathVariable Long id) {
        return AsignacionResponse.from(asignacionMateriaProfesorServices.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@acceso.esGestor(authentication)")
    public AsignacionResponse actualizar(@PathVariable Long id, @Valid @RequestBody AsignacionRequest request) {
        return AsignacionResponse.from(asignacionMateriaProfesorServices.actualizar(id, request.toEntity(referencias)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@acceso.esGestor(authentication)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        asignacionMateriaProfesorServices.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
