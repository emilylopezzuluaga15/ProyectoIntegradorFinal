package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Services.CarreraServices;
import com.example.gestionacademica.presentation.dto.AlCrear;
import com.example.gestionacademica.presentation.dto.CarreraDtos.CarreraRequest;
import com.example.gestionacademica.presentation.dto.CarreraDtos.CarreraResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Consulta: cualquier usuario autenticado. Cambios: ADMINISTRATIVO y COORDINADOR
@RestController
@RequestMapping("/api/carreras")
public class CarreraController {

    private final CarreraServices carreraServices;

    public CarreraController(CarreraServices carreraServices) {
        this.carreraServices = carreraServices;
    }

    @PostMapping
    @PreAuthorize("@acceso.esGestor(authentication)")
    public ResponseEntity<CarreraResponse> crear(@Validated(AlCrear.class) @RequestBody CarreraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CarreraResponse.from(carreraServices.crear(request.toEntity())));
    }

    @GetMapping
    public List<CarreraResponse> listar() {
        return carreraServices.obtenerTodos().stream().map(CarreraResponse::from).toList();
    }

    @GetMapping("/{id}")
    public CarreraResponse obtener(@PathVariable String id) {
        return CarreraResponse.from(carreraServices.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@acceso.esGestor(authentication)")
    public CarreraResponse actualizar(@PathVariable String id, @Valid @RequestBody CarreraRequest request) {
        return CarreraResponse.from(carreraServices.actualizar(id, request.toEntity()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@acceso.esGestor(authentication)")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        carreraServices.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
