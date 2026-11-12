package com.example.gestionacademica.application.Services.impl;

import com.example.gestionacademica.application.Exceptions.BusinessException;
import com.example.gestionacademica.application.Exceptions.MateriaNotFoundException;
import com.example.gestionacademica.application.Services.MateriaServices;
import com.example.gestionacademica.domain.models.Materia;
import com.example.gestionacademica.domain.repository.AsignacionMateriaProfesorRepository;
import com.example.gestionacademica.domain.repository.MateriaRepository;
import com.example.gestionacademica.domain.repository.NivelMateriaRepository;
import com.example.gestionacademica.domain.repository.NotaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MateriaServicesImpl implements MateriaServices {

    private final MateriaRepository materiaRepository;
    private final NivelMateriaRepository nivelMateriaRepository;
    private final AsignacionMateriaProfesorRepository asignacionMateriaProfesorRepository;
    private final NotaRepository notaRepository;

    public MateriaServicesImpl(MateriaRepository materiaRepository,
                               NivelMateriaRepository nivelMateriaRepository,
                               AsignacionMateriaProfesorRepository asignacionMateriaProfesorRepository,
                               NotaRepository notaRepository) {
        this.materiaRepository = materiaRepository;
        this.nivelMateriaRepository = nivelMateriaRepository;
        this.asignacionMateriaProfesorRepository = asignacionMateriaProfesorRepository;
        this.notaRepository = notaRepository;
    }

    @Override
    public Materia crear(Materia materia) {
        if (materiaRepository.existsById(materia.getIdMateria())) {
            throw new BusinessException("YA_EXISTE", "Materia ya existe con id: " + materia.getIdMateria());
        }
        return materiaRepository.save(materia);
    }

    @Override
    public Materia obtenerPorId(String id) {
        return materiaRepository.findById(id)
                .orElseThrow(() -> new MateriaNotFoundException("Materia no encontrada con id: " + id));
    }

    @Override
    public List<Materia> obtenerTodos() {
        return materiaRepository.findAll();
    }

    @Override
    public Materia actualizar(String id, Materia datos) {
        Materia existente = obtenerPorId(id);
        existente.setNombreMateria(datos.getNombreMateria());
        return materiaRepository.save(existente);
    }

    @Override
    public void eliminar(String id) {
        if (!materiaRepository.existsById(id)) {
            throw new MateriaNotFoundException("Materia no encontrada con id: " + id);
        }
        long niveles = nivelMateriaRepository.countByMateriaIdMateria(id);
        long asignaciones = asignacionMateriaProfesorRepository.countByMateriaIdMateria(id);
        long notas = notaRepository.countByMateriaIdMateria(id);
        if (niveles > 0 || asignaciones > 0 || notas > 0) {
            throw new BusinessException("TIENE_DEPENDENCIAS", "No se puede eliminar la materia " + id + " porque tiene "
                    + niveles + " niveles asociados, " + asignaciones + " asignaciones de profesor y " + notas + " notas");
        }
        materiaRepository.deleteById(id);
    }
}
