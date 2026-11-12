package com.example.gestionacademica.infrastructure.persistence;

import com.example.gestionacademica.domain.models.Nota;
import com.example.gestionacademica.domain.repository.NotaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class NotaRepositoryAdapter implements NotaRepository {

    private final NotaJpaRepository jpaRepository;

    public NotaRepositoryAdapter(NotaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Nota save(Nota nota) {
        return jpaRepository.save(nota);
    }

    @Override
    public Optional<Nota> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Nota> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Nota> findByEstudianteIdEstudiante(String idEstudiante) {
        return jpaRepository.findByEstudianteIdEstudiante(idEstudiante);
    }

    @Override
    public List<Nota> findByEstudianteIdEstudianteAndMateriaIdMateria(String idEstudiante, String idMateria) {
        return jpaRepository.findByEstudianteIdEstudianteAndMateriaIdMateria(idEstudiante, idMateria);
    }

    @Override
    public long countByEstudianteIdEstudiante(String idEstudiante) {
        return jpaRepository.countByEstudianteIdEstudiante(idEstudiante);
    }

    @Override
    public long countByMateriaIdMateria(String idMateria) {
        return jpaRepository.countByMateriaIdMateria(idMateria);
    }
}
