package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Services.AsistenciaServices;
import com.example.gestionacademica.infrastructure.security.AccesoService;
import com.example.gestionacademica.presentation.dto.AsistenciaDtos.AsistenciaRequest;
import com.example.gestionacademica.presentation.dto.AsistenciaDtos.AsistenciaResponse;
import com.example.gestionacademica.presentation.mapper.Referencias;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// Consulta: personal (el listado general del PROFESOR se limita a sus grupos); el estudiante solo la suya.
// Registro: ADMINISTRATIVO, COORDINADOR y el PROFESOR que dicta en ese grupo
@RestController
@RequestMapping("/api/asistencia")
public class AsistenciaController {

    private final AsistenciaServices asistenciaServices;
    private final Referencias referencias;
    private final AccesoService acceso;

    public AsistenciaController(AsistenciaServices asistenciaServices, Referencias referencias, AccesoService acceso) {
        this.asistenciaServices = asistenciaServices;
        this.referencias = referencias;
        this.acceso = acceso;
    }

    @PostMapping
    @PreAuthorize("@acceso.puedeGestionarGrupo(authentication, #request.idGrupo())")
    public ResponseEntity<AsistenciaResponse> crear(@Valid @RequestBody AsistenciaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AsistenciaResponse.from(asistenciaServices.crear(request.toEntity(referencias))));
    }

    @GetMapping
    @PreAuthorize("@acceso.esPersonal(authentication)")
    public List<AsistenciaResponse> listar(Authentication authentication) {
        return acceso.soloGruposAsignados(authentication, asistenciaServices.obtenerTodos(), a -> a.getGrupo().getIdGrupo()).stream().map(AsistenciaResponse::from).toList();
    }

    @GetMapping("/estudiante/{idEstudiante}")
    @PreAuthorize("@acceso.puedeVerEstudiante(authentication, #idEstudiante)")
    public List<AsistenciaResponse> listarPorEstudiante(@PathVariable String idEstudiante) {
        return asistenciaServices.obtenerPorEstudiante(idEstudiante).stream().map(AsistenciaResponse::from).toList();
    }

    @GetMapping("/estudiante/{idEstudiante}/resumen")
    @PreAuthorize("@acceso.puedeVerEstudiante(authentication, #idEstudiante)")
    public Map<String, Object> resumen(@PathVariable String idEstudiante) {
        return asistenciaServices.resumenPorEstudiante(idEstudiante);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@acceso.esPersonal(authentication)")
    public AsistenciaResponse obtener(@PathVariable Long id) {
        return AsistenciaResponse.from(asistenciaServices.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@acceso.puedeModificarAsistencia(authentication, #id) and @acceso.puedeGestionarGrupo(authentication, #request.idGrupo())")
    public AsistenciaResponse actualizar(@PathVariable Long id, @Valid @RequestBody AsistenciaRequest request) {
        return AsistenciaResponse.from(asistenciaServices.actualizar(id, request.toEntity(referencias)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@acceso.puedeModificarAsistencia(authentication, #id)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        asistenciaServices.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
