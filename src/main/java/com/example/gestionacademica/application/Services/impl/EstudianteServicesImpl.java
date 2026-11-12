package com.example.gestionacademica.application.Services.impl;

import com.example.gestionacademica.application.Exceptions.BusinessException;
import com.example.gestionacademica.application.Exceptions.EstudianteNotFoundException;
import com.example.gestionacademica.application.Services.EstudianteServices;
import com.example.gestionacademica.domain.models.Estudiante;
import com.example.gestionacademica.domain.repository.AsistenciaRepository;
import com.example.gestionacademica.domain.repository.EstudianteRepository;
import com.example.gestionacademica.domain.repository.InscripcionRepository;
import com.example.gestionacademica.domain.repository.NotaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EstudianteServicesImpl implements EstudianteServices {

    private final EstudianteRepository estudianteRepository;
    private final InscripcionRepository inscripcionRepository;
    private final NotaRepository notaRepository;
    private final AsistenciaRepository asistenciaRepository;

    public EstudianteServicesImpl(EstudianteRepository estudianteRepository,
                                  InscripcionRepository inscripcionRepository,
                                  NotaRepository notaRepository,
                                  AsistenciaRepository asistenciaRepository) {
        this.estudianteRepository = estudianteRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.notaRepository = notaRepository;
        this.asistenciaRepository = asistenciaRepository;
    }

    @Override
    public Estudiante crear(Estudiante estudiante) {
        if (estudianteRepository.existsById(estudiante.getIdEstudiante())) {
            throw new BusinessException("YA_EXISTE", "Estudiante ya existe con id: " + estudiante.getIdEstudiante());
        }
        if (estudiante.getCorreo() != null && estudianteRepository.existsByCorreo(estudiante.getCorreo())) {
            throw new BusinessException("CORREO_DUPLICADO", "Ya existe un estudiante con el correo: " + estudiante.getCorreo());
        }
        return estudianteRepository.save(estudiante);
    }

    @Override
    public Estudiante obtenerPorId(String id) {
        return estudianteRepository.findById(id)
                .orElseThrow(() -> new EstudianteNotFoundException("Estudiante no encontrado con id: " + id));
    }

    @Override
    public List<Estudiante> obtenerTodos() {
        return estudianteRepository.findAll();
    }

    @Override
    public Estudiante actualizar(String id, Estudiante datos) {
        Estudiante existente = obtenerPorId(id);

        // Solo se revisa el correo si cambió, para no chocar con el mismo estudiante
        boolean cambioCorreo = datos.getCorreo() != null && !datos.getCorreo().equals(existente.getCorreo());
        if (cambioCorreo && estudianteRepository.existsByCorreo(datos.getCorreo())) {
            throw new BusinessException("CORREO_DUPLICADO", "Ya existe un estudiante con el correo: " + datos.getCorreo());
        }

        existente.setNombreEstudiante(datos.getNombreEstudiante());
        existente.setFechaIngreso(datos.getFechaIngreso());
        existente.setGenero(datos.getGenero());
        existente.setCelular(datos.getCelular());
        existente.setCorreo(datos.getCorreo());
        existente.setActivo(datos.getActivo());
        existente.setEstado(datos.getEstado());
        return estudianteRepository.save(existente);
    }

    @Override
    public void eliminar(String id) {
        if (!estudianteRepository.existsById(id)) {
            throw new EstudianteNotFoundException("Estudiante no encontrado con id: " + id);
        }
        long inscripciones = inscripcionRepository.countByEstudianteIdEstudiante(id);
        long notas = notaRepository.countByEstudianteIdEstudiante(id);
        long asistencias = asistenciaRepository.countByEstudianteIdEstudiante(id);
        if (inscripciones > 0 || notas > 0 || asistencias > 0) {
            throw new BusinessException("TIENE_DEPENDENCIAS", "No se puede eliminar el estudiante " + id + " porque tiene "
                    + inscripciones + " inscripciones, " + notas + " notas y " + asistencias
                    + " registros de asistencia");
        }
        estudianteRepository.deleteById(id);
    }
}
