package com.example.gestionacademica.presentation.mapper;

import com.example.gestionacademica.application.Services.CarreraServices;
import com.example.gestionacademica.application.Services.EstudianteServices;
import com.example.gestionacademica.application.Services.GrupoServices;
import com.example.gestionacademica.application.Services.MateriaServices;
import com.example.gestionacademica.application.Services.NivelServices;
import com.example.gestionacademica.application.Services.NotaServices;
import com.example.gestionacademica.application.Services.ProfesorServices;
import com.example.gestionacademica.domain.models.Carrera;
import com.example.gestionacademica.domain.models.Estudiante;
import com.example.gestionacademica.domain.models.Grupo;
import com.example.gestionacademica.domain.models.Materia;
import com.example.gestionacademica.domain.models.Nivel;
import com.example.gestionacademica.domain.models.Nota;
import com.example.gestionacademica.domain.models.Profesor;
import org.springframework.stereotype.Component;

// Convierte los ids que llegan en los DTO en entidades reales (404 si no existen),
// así las respuestas salen con los nombres completos y no con referencias vacías
@Component
public class Referencias {

    private final CarreraServices carreraServices;
    private final NivelServices nivelServices;
    private final MateriaServices materiaServices;
    private final GrupoServices grupoServices;
    private final ProfesorServices profesorServices;
    private final EstudianteServices estudianteServices;
    private final NotaServices notaServices;

    public Referencias(CarreraServices carreraServices,
                       NivelServices nivelServices,
                       MateriaServices materiaServices,
                       GrupoServices grupoServices,
                       ProfesorServices profesorServices,
                       EstudianteServices estudianteServices,
                       NotaServices notaServices) {
        this.carreraServices = carreraServices;
        this.nivelServices = nivelServices;
        this.materiaServices = materiaServices;
        this.grupoServices = grupoServices;
        this.profesorServices = profesorServices;
        this.estudianteServices = estudianteServices;
        this.notaServices = notaServices;
    }

    public Carrera carrera(String id) {
        return carreraServices.obtenerPorId(id);
    }

    public Nivel nivel(String id) {
        return nivelServices.obtenerPorId(id);
    }

    public Materia materia(String id) {
        return materiaServices.obtenerPorId(id);
    }

    public Grupo grupo(String id) {
        return grupoServices.obtenerPorId(id);
    }

    public Profesor profesor(String id) {
        return profesorServices.obtenerPorId(id);
    }

    public Estudiante estudiante(String id) {
        return estudianteServices.obtenerPorId(id);
    }

    public Nota nota(Long id) {
        return notaServices.obtenerPorId(id);
    }

    // Para vínculos opcionales (usuarios): null se mantiene null
    public Estudiante estudianteOpcional(String id) {
        return id == null || id.isBlank() ? null : estudiante(id);
    }

    public Profesor profesorOpcional(String id) {
        return id == null || id.isBlank() ? null : profesor(id);
    }
}
