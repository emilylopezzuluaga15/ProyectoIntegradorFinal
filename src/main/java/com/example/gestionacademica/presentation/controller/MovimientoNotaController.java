package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Services.MovimientoNotaServices;
import com.example.gestionacademica.infrastructure.security.AccesoService;
import com.example.gestionacademica.presentation.dto.MovimientoNotaDtos.MovimientoNotaRequest;
import com.example.gestionacademica.presentation.dto.MovimientoNotaDtos.MovimientoNotaResponse;
import com.example.gestionacademica.presentation.mapper.Referencias;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Consulta: personal (el listado general del PROFESOR se limita a sus materias); el estudiante solo las calificaciones de sus notas.
// Registro: ADMINISTRATIVO, COORDINADOR y el PROFESOR asignado a la materia de la nota
@RestController
@RequestMapping("/api/movimiento-notas")
public class MovimientoNotaController {

    private final MovimientoNotaServices movimientoNotaServices;
    private final Referencias referencias;
    private final AccesoService acceso;

    public MovimientoNotaController(MovimientoNotaServices movimientoNotaServices, Referencias referencias, AccesoService acceso) {
        this.movimientoNotaServices = movimientoNotaServices;
        this.referencias = referencias;
        this.acceso = acceso;
    }

    @PostMapping
    @PreAuthorize("@acceso.puedeModificarNota(authentication, #request.idNota())")
    public ResponseEntity<MovimientoNotaResponse> crear(@Valid @RequestBody MovimientoNotaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MovimientoNotaResponse.from(movimientoNotaServices.crear(request.toEntity(referencias))));
    }

    @GetMapping
    @PreAuthorize("@acceso.esPersonal(authentication)")
    public List<MovimientoNotaResponse> listar(Authentication authentication) {
        return acceso.soloMateriasAsignadas(authentication, movimientoNotaServices.obtenerTodos(),
                        m -> m.getNota().getMateria().getIdMateria()).stream().map(MovimientoNotaResponse::from).toList();
    }

    @GetMapping("/nota/{idNota}")
    @PreAuthorize("@acceso.puedeVerNota(authentication, #idNota)")
    public List<MovimientoNotaResponse> listarPorNota(@PathVariable Long idNota) {
        return movimientoNotaServices.obtenerPorNota(idNota).stream().map(MovimientoNotaResponse::from).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@acceso.esPersonal(authentication)")
    public MovimientoNotaResponse obtener(@PathVariable Long id) {
        return MovimientoNotaResponse.from(movimientoNotaServices.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@acceso.puedeModificarMovimiento(authentication, #id) and @acceso.puedeModificarNota(authentication, #request.idNota())")
    public MovimientoNotaResponse actualizar(@PathVariable Long id, @Valid @RequestBody MovimientoNotaRequest request) {
        return MovimientoNotaResponse.from(movimientoNotaServices.actualizar(id, request.toEntity(referencias)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@acceso.puedeModificarMovimiento(authentication, #id)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        movimientoNotaServices.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
