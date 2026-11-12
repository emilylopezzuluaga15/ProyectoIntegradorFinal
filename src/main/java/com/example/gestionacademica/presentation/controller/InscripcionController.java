package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Services.InscripcionServices;
import com.example.gestionacademica.infrastructure.security.AccesoService;
import com.example.gestionacademica.presentation.dto.InscripcionDtos.InscripcionRequest;
import com.example.gestionacademica.presentation.dto.InscripcionDtos.InscripcionResponse;
import com.example.gestionacademica.presentation.mapper.Referencias;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Consulta: personal (el listado general del PROFESOR se limita a sus grupos); el estudiante solo las suyas. Cambios: ADMINISTRATIVO y COORDINADOR
@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    private final InscripcionServices inscripcionServices;
    private final Referencias referencias;
    private final AccesoService acceso;

    public InscripcionController(InscripcionServices inscripcionServices, Referencias referencias, AccesoService acceso) {
        this.inscripcionServices = inscripcionServices;
        this.referencias = referencias;
        this.acceso = acceso;
    }

    @PostMapping
    @PreAuthorize("@acceso.esGestor(authentication)")
    public ResponseEntity<InscripcionResponse> crear(@Valid @RequestBody InscripcionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(InscripcionResponse.from(inscripcionServices.crear(request.toEntity(referencias))));
    }

    @GetMapping
    @PreAuthorize("@acceso.esPersonal(authentication)")
    public List<InscripcionResponse> listar(Authentication authentication) {
        return acceso.soloGruposAsignados(authentication, inscripcionServices.obtenerTodos(), i -> i.getGrupo().getIdGrupo()).stream().map(InscripcionResponse::from).toList();
    }

    @GetMapping("/estudiante/{idEstudiante}")
    @PreAuthorize("@acceso.puedeVerEstudiante(authentication, #idEstudiante)")
    public List<InscripcionResponse> listarPorEstudiante(@PathVariable String idEstudiante) {
        return inscripcionServices.obtenerPorEstudiante(idEstudiante).stream().map(InscripcionResponse::from).toList();
    }

    @GetMapping("/grupo/{idGrupo}")
    @PreAuthorize("@acceso.esPersonal(authentication)")
    public List<InscripcionResponse> listarPorGrupo(@PathVariable String idGrupo) {
        return inscripcionServices.obtenerPorGrupo(idGrupo).stream().map(InscripcionResponse::from).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@acceso.esPersonal(authentication)")
    public InscripcionResponse obtener(@PathVariable Long id) {
        return InscripcionResponse.from(inscripcionServices.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@acceso.esGestor(authentication)")
    public InscripcionResponse actualizar(@PathVariable Long id, @Valid @RequestBody InscripcionRequest request) {
        return InscripcionResponse.from(inscripcionServices.actualizar(id, request.toEntity(referencias)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@acceso.esGestor(authentication)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        inscripcionServices.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
