package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Services.NotaServices;
import com.example.gestionacademica.infrastructure.security.AccesoService;
import com.example.gestionacademica.presentation.dto.NotaDtos.NotaRequest;
import com.example.gestionacademica.presentation.dto.NotaDtos.NotaResponse;
import com.example.gestionacademica.presentation.mapper.Referencias;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// Consulta: personal (el listado general del PROFESOR se limita a sus materias); el estudiante solo las suyas.
// Registro: ADMINISTRATIVO, COORDINADOR y el PROFESOR asignado a esa materia
@RestController
@RequestMapping("/api/notas")
public class NotaController {

    private final NotaServices notaServices;
    private final Referencias referencias;
    private final AccesoService acceso;

    public NotaController(NotaServices notaServices, Referencias referencias, AccesoService acceso) {
        this.notaServices = notaServices;
        this.referencias = referencias;
        this.acceso = acceso;
    }

    @PostMapping
    @PreAuthorize("@acceso.puedeCalificarMateria(authentication, #request.idMateria())")
    public ResponseEntity<NotaResponse> crear(@Valid @RequestBody NotaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(NotaResponse.from(notaServices.crear(request.toEntity(referencias))));
    }

    @GetMapping
    @PreAuthorize("@acceso.esPersonal(authentication)")
    public List<NotaResponse> listar(Authentication authentication) {
        return acceso.soloMateriasAsignadas(authentication, notaServices.obtenerTodos(), n -> n.getMateria().getIdMateria()).stream().map(NotaResponse::from).toList();
    }

    @GetMapping("/estudiante/{idEstudiante}")
    @PreAuthorize("@acceso.puedeVerEstudiante(authentication, #idEstudiante)")
    public List<NotaResponse> listarPorEstudiante(@PathVariable String idEstudiante) {
        return notaServices.obtenerPorEstudiante(idEstudiante).stream().map(NotaResponse::from).toList();
    }

    @GetMapping("/estudiante/{idEstudiante}/promedio")
    @PreAuthorize("@acceso.puedeVerEstudiante(authentication, #idEstudiante)")
    public Map<String, Object> promedio(@PathVariable String idEstudiante) {
        return notaServices.calcularPromedio(idEstudiante);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@acceso.puedeVerNota(authentication, #id)")
    public NotaResponse obtener(@PathVariable Long id) {
        return NotaResponse.from(notaServices.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@acceso.puedeModificarNota(authentication, #id) and @acceso.puedeCalificarMateria(authentication, #request.idMateria())")
    public NotaResponse actualizar(@PathVariable Long id, @Valid @RequestBody NotaRequest request) {
        return NotaResponse.from(notaServices.actualizar(id, request.toEntity(referencias)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@acceso.puedeModificarNota(authentication, #id)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        notaServices.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
