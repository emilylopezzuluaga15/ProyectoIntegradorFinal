package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Services.ProfesorServices;
import com.example.gestionacademica.presentation.dto.AlCrear;
import com.example.gestionacademica.presentation.dto.ProfesorDtos.ProfesorRequest;
import com.example.gestionacademica.presentation.dto.ProfesorDtos.ProfesorResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Consulta: personal (contiene datos de contacto). Cambios: ADMINISTRATIVO y COORDINADOR
@RestController
@RequestMapping("/api/profesores")
public class ProfesorController {

    private final ProfesorServices profesorServices;

    public ProfesorController(ProfesorServices profesorServices) {
        this.profesorServices = profesorServices;
    }

    @PostMapping
    @PreAuthorize("@acceso.esGestor(authentication)")
    public ResponseEntity<ProfesorResponse> crear(@Validated(AlCrear.class) @RequestBody ProfesorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProfesorResponse.from(profesorServices.crear(request.toEntity())));
    }

    @GetMapping
    @PreAuthorize("@acceso.esPersonal(authentication)")
    public List<ProfesorResponse> listar() {
        return profesorServices.obtenerTodos().stream().map(ProfesorResponse::from).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@acceso.esPersonal(authentication)")
    public ProfesorResponse obtener(@PathVariable String id) {
        return ProfesorResponse.from(profesorServices.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@acceso.esGestor(authentication)")
    public ProfesorResponse actualizar(@PathVariable String id, @Valid @RequestBody ProfesorRequest request) {
        return ProfesorResponse.from(profesorServices.actualizar(id, request.toEntity()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@acceso.esGestor(authentication)")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        profesorServices.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
