package com.example.gestionacademica.application.Services.impl;

import com.example.gestionacademica.application.Exceptions.BusinessException;
import com.example.gestionacademica.application.Exceptions.NivelNotFoundException;
import com.example.gestionacademica.application.Services.NivelServices;
import com.example.gestionacademica.domain.models.Nivel;
import com.example.gestionacademica.domain.repository.GrupoRepository;
import com.example.gestionacademica.domain.repository.NivelMateriaRepository;
import com.example.gestionacademica.domain.repository.NivelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NivelServicesImpl implements NivelServices {

    private final NivelRepository nivelRepository;
    private final GrupoRepository grupoRepository;
    private final NivelMateriaRepository nivelMateriaRepository;

    public NivelServicesImpl(NivelRepository nivelRepository,
                             GrupoRepository grupoRepository,
                             NivelMateriaRepository nivelMateriaRepository) {
        this.nivelRepository = nivelRepository;
        this.grupoRepository = grupoRepository;
        this.nivelMateriaRepository = nivelMateriaRepository;
    }

    @Override
    public Nivel crear(Nivel nivel) {
        if (nivelRepository.existsById(nivel.getIdNivel())) {
            throw new BusinessException("YA_EXISTE", "Nivel ya existe con id: " + nivel.getIdNivel());
        }
        return nivelRepository.save(nivel);
    }

    @Override
    public Nivel obtenerPorId(String id) {
        return nivelRepository.findById(id)
                .orElseThrow(() -> new NivelNotFoundException("Nivel no encontrado con id: " + id));
    }

    @Override
    public List<Nivel> obtenerTodos() {
        return nivelRepository.findAll();
    }

    @Override
    public Nivel actualizar(String id, Nivel datos) {
        Nivel existente = obtenerPorId(id);
        existente.setNombreNivel(datos.getNombreNivel());
        existente.setCarrera(datos.getCarrera());
        return nivelRepository.save(existente);
    }

    @Override
    public void eliminar(String id) {
        if (!nivelRepository.existsById(id)) {
            throw new NivelNotFoundException("Nivel no encontrado con id: " + id);
        }
        long grupos = grupoRepository.countByNivelIdNivel(id);
        long materias = nivelMateriaRepository.countByNivelIdNivel(id);
        if (grupos > 0 || materias > 0) {
            throw new BusinessException("TIENE_DEPENDENCIAS", "No se puede eliminar el nivel " + id + " porque tiene "
                    + grupos + " grupos y " + materias + " materias asociadas");
        }
        nivelRepository.deleteById(id);
    }
}
