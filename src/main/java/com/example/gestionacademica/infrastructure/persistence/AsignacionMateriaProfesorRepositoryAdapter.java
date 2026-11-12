package com.example.gestionacademica.infrastructure.persistence;

import com.example.gestionacademica.domain.models.AsignacionMateriaProfesor;
import com.example.gestionacademica.domain.repository.AsignacionMateriaProfesorRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AsignacionMateriaProfesorRepositoryAdapter implements AsignacionMateriaProfesorRepository {

    private final AsignacionMateriaProfesorJpaRepository jpaRepository;

    public AsignacionMateriaProfesorRepositoryAdapter(AsignacionMateriaProfesorJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public AsignacionMateriaProfesor save(AsignacionMateriaProfesor asignacionMateriaProfesor) {
        return jpaRepository.save(asignacionMateriaProfesor);
    }

    @Override
    public Optional<AsignacionMateriaProfesor> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<AsignacionMateriaProfesor> findAll() {
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
    public boolean existsByGrupoIdGrupoAndMateriaIdMateria(String idGrupo, String idMateria) {
        return jpaRepository.existsByGrupoIdGrupoAndMateriaIdMateria(idGrupo, idMateria);
    }

    @Override
    public List<AsignacionMateriaProfesor> findByProfesorIdProfesor(String idProfesor) {
        return jpaRepository.findByProfesorIdProfesor(idProfesor);
    }

    @Override
    public List<AsignacionMateriaProfesor> findByGrupoIdGrupoAndMateriaIdMateria(String idGrupo, String idMateria) {
        return jpaRepository.findByGrupoIdGrupoAndMateriaIdMateria(idGrupo, idMateria);
    }

    @Override
    public long countByProfesorIdProfesor(String idProfesor) {
        return jpaRepository.countByProfesorIdProfesor(idProfesor);
    }

    @Override
    public long countByGrupoIdGrupo(String idGrupo) {
        return jpaRepository.countByGrupoIdGrupo(idGrupo);
    }

    @Override
    public long countByMateriaIdMateria(String idMateria) {
        return jpaRepository.countByMateriaIdMateria(idMateria);
    }

    @Override
    public boolean existsByProfesorIdProfesorAndMateriaIdMateria(String idProfesor, String idMateria) {
        return jpaRepository.existsByProfesorIdProfesorAndMateriaIdMateria(idProfesor, idMateria);
    }

    @Override
    public boolean existsByProfesorIdProfesorAndGrupoIdGrupo(String idProfesor, String idGrupo) {
        return jpaRepository.existsByProfesorIdProfesorAndGrupoIdGrupo(idProfesor, idGrupo);
    }

    @Override
    public boolean existsByProfesorIdProfesorAndGrupoIdGrupoAndMateriaIdMateria(String idProfesor, String idGrupo, String idMateria) {
        return jpaRepository.existsByProfesorIdProfesorAndGrupoIdGrupoAndMateriaIdMateria(idProfesor, idGrupo, idMateria);
    }
}
