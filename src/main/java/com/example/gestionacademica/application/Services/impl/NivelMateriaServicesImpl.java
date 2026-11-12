package com.example.gestionacademica.application.Services.impl;

import com.example.gestionacademica.application.Exceptions.BusinessException;
import com.example.gestionacademica.application.Exceptions.NivelMateriaNotFoundException;
import com.example.gestionacademica.application.Services.NivelMateriaServices;
import com.example.gestionacademica.domain.models.NivelMateria;
import com.example.gestionacademica.domain.repository.NivelMateriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NivelMateriaServicesImpl implements NivelMateriaServices {

    private final NivelMateriaRepository nivelMateriaRepository;

    public NivelMateriaServicesImpl(NivelMateriaRepository nivelMateriaRepository) {
        this.nivelMateriaRepository = nivelMateriaRepository;
    }

    @Override
    public NivelMateria crear(NivelMateria nivelMateria) {
        validarDuplicado(nivelMateria);
        return nivelMateriaRepository.save(nivelMateria);
    }

    @Override
    public NivelMateria obtenerPorId(Long id) {
        return nivelMateriaRepository.findById(id)
                .orElseThrow(() -> new NivelMateriaNotFoundException("Materia del plan de estudios no encontrada con id: " + id));
    }

    @Override
    public List<NivelMateria> obtenerTodos() {
        return nivelMateriaRepository.findAll();
    }

    @Override
    public NivelMateria actualizar(Long id, NivelMateria datos) {
        NivelMateria existente = obtenerPorId(id);
        boolean cambioNivel = !existente.getNivel().getIdNivel().equals(datos.getNivel().getIdNivel());
        boolean cambioMateria = !existente.getMateria().getIdMateria().equals(datos.getMateria().getIdMateria());
        if (cambioNivel || cambioMateria) {
            validarDuplicado(datos);
        }
        existente.setNivel(datos.getNivel());
        existente.setMateria(datos.getMateria());
        return nivelMateriaRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        if (!nivelMateriaRepository.existsById(id)) {
            throw new NivelMateriaNotFoundException("Materia del plan de estudios no encontrada con id: " + id);
        }
        nivelMateriaRepository.deleteById(id);
    }

    private void validarDuplicado(NivelMateria nivelMateria) {
        String idNivel = nivelMateria.getNivel().getIdNivel();
        String idMateria = nivelMateria.getMateria().getIdMateria();
        if (nivelMateriaRepository.existsByNivelIdNivelAndMateriaIdMateria(idNivel, idMateria)) {
            throw new BusinessException("MATERIA_YA_ASOCIADA", "La materia " + idMateria + " ya está asociada al nivel " + idNivel);
        }
    }
}
