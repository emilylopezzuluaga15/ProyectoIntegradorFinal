package com.example.gestionacademica.application.Services.impl;

import com.example.gestionacademica.application.Exceptions.BusinessException;
import com.example.gestionacademica.application.Exceptions.ProfesorNotFoundException;
import com.example.gestionacademica.application.Services.ProfesorServices;
import com.example.gestionacademica.domain.models.Profesor;
import com.example.gestionacademica.domain.repository.AsignacionMateriaProfesorRepository;
import com.example.gestionacademica.domain.repository.ProfesorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProfesorServicesImpl implements ProfesorServices {

    private final ProfesorRepository profesorRepository;
    private final AsignacionMateriaProfesorRepository asignacionMateriaProfesorRepository;

    public ProfesorServicesImpl(ProfesorRepository profesorRepository,
                                AsignacionMateriaProfesorRepository asignacionMateriaProfesorRepository) {
        this.profesorRepository = profesorRepository;
        this.asignacionMateriaProfesorRepository = asignacionMateriaProfesorRepository;
    }

    @Override
    public Profesor crear(Profesor profesor) {
        if (profesorRepository.existsById(profesor.getIdProfesor())) {
            throw new BusinessException("YA_EXISTE", "Profesor ya existe con id: " + profesor.getIdProfesor());
        }
        if (profesor.getCorreo() != null && profesorRepository.existsByCorreo(profesor.getCorreo())) {
            throw new BusinessException("CORREO_DUPLICADO", "Ya existe un profesor con el correo: " + profesor.getCorreo());
        }
        return profesorRepository.save(profesor);
    }

    @Override
    public Profesor obtenerPorId(String id) {
        return profesorRepository.findById(id)
                .orElseThrow(() -> new ProfesorNotFoundException("Profesor no encontrado con id: " + id));
    }

    @Override
    public List<Profesor> obtenerTodos() {
        return profesorRepository.findAll();
    }

    @Override
    public Profesor actualizar(String id, Profesor datos) {
        Profesor existente = obtenerPorId(id);

        // Solo se revisa el correo si cambió, para no chocar con el mismo profesor
        boolean cambioCorreo = datos.getCorreo() != null && !datos.getCorreo().equals(existente.getCorreo());
        if (cambioCorreo && profesorRepository.existsByCorreo(datos.getCorreo())) {
            throw new BusinessException("CORREO_DUPLICADO", "Ya existe un profesor con el correo: " + datos.getCorreo());
        }

        existente.setNombreProfesor(datos.getNombreProfesor());
        existente.setFechaIngreso(datos.getFechaIngreso());
        existente.setGenero(datos.getGenero());
        existente.setCelular(datos.getCelular());
        existente.setCorreo(datos.getCorreo());
        existente.setActivo(datos.getActivo());
        existente.setEspecializacion(datos.getEspecializacion());
        return profesorRepository.save(existente);
    }

    @Override
    public void eliminar(String id) {
        if (!profesorRepository.existsById(id)) {
            throw new ProfesorNotFoundException("Profesor no encontrado con id: " + id);
        }
        long asignaciones = asignacionMateriaProfesorRepository.countByProfesorIdProfesor(id);
        if (asignaciones > 0) {
            throw new BusinessException("TIENE_DEPENDENCIAS", "No se puede eliminar el profesor " + id + " porque tiene "
                    + asignaciones + " asignaciones de materias");
        }
        profesorRepository.deleteById(id);
    }
}
