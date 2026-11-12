package com.example.gestionacademica.application.Services.impl;

import com.example.gestionacademica.application.Exceptions.BusinessException;
import com.example.gestionacademica.application.Exceptions.CarreraNotFoundException;
import com.example.gestionacademica.application.Services.CarreraServices;
import com.example.gestionacademica.domain.models.Carrera;
import com.example.gestionacademica.domain.repository.CarreraRepository;
import com.example.gestionacademica.domain.repository.NivelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CarreraServicesImpl implements CarreraServices {

    private final CarreraRepository carreraRepository;
    private final NivelRepository nivelRepository;

    public CarreraServicesImpl(CarreraRepository carreraRepository,
                               NivelRepository nivelRepository) {
        this.carreraRepository = carreraRepository;
        this.nivelRepository = nivelRepository;
    }

    @Override
    public Carrera crear(Carrera carrera) {
        if (carreraRepository.existsById(carrera.getIdCarrera())) {
            throw new BusinessException("YA_EXISTE", "Carrera ya existe con id: " + carrera.getIdCarrera());
        }
        return carreraRepository.save(carrera);
    }

    @Override
    public Carrera obtenerPorId(String id) {
        return carreraRepository.findById(id)
                .orElseThrow(() -> new CarreraNotFoundException("Carrera no encontrada con id: " + id));
    }

    @Override
    public List<Carrera> obtenerTodos() {
        return carreraRepository.findAll();
    }

    @Override
    public Carrera actualizar(String id, Carrera datos) {
        Carrera existente = obtenerPorId(id);
        existente.setNombreCarrera(datos.getNombreCarrera());
        return carreraRepository.save(existente);
    }

    @Override
    public void eliminar(String id) {
        if (!carreraRepository.existsById(id)) {
            throw new CarreraNotFoundException("Carrera no encontrada con id: " + id);
        }
        long niveles = nivelRepository.countByCarreraIdCarrera(id);
        if (niveles > 0) {
            throw new BusinessException("TIENE_DEPENDENCIAS", "No se puede eliminar la carrera " + id + " porque tiene "
                    + niveles + " niveles");
        }
        carreraRepository.deleteById(id);
    }
}
