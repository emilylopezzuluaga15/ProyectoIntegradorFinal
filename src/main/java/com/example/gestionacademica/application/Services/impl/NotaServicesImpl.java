package com.example.gestionacademica.application.Services.impl;

import com.example.gestionacademica.application.Exceptions.BusinessException;
import com.example.gestionacademica.application.Exceptions.EstudianteNotFoundException;
import com.example.gestionacademica.application.Exceptions.NotaNotFoundException;
import com.example.gestionacademica.application.Services.NotaServices;
import com.example.gestionacademica.domain.models.MovimientoNota;
import com.example.gestionacademica.domain.models.Nota;
import com.example.gestionacademica.domain.repository.EstudianteRepository;
import com.example.gestionacademica.domain.repository.MovimientoNotaRepository;
import com.example.gestionacademica.domain.repository.NotaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class NotaServicesImpl implements NotaServices {

    private static final float NOTA_MINIMA_APROBACION = 3.0f;

    private final NotaRepository notaRepository;
    private final MovimientoNotaRepository movimientoNotaRepository;
    private final EstudianteRepository estudianteRepository;

    public NotaServicesImpl(NotaRepository notaRepository,
                            MovimientoNotaRepository movimientoNotaRepository,
                            EstudianteRepository estudianteRepository) {
        this.notaRepository = notaRepository;
        this.movimientoNotaRepository = movimientoNotaRepository;
        this.estudianteRepository = estudianteRepository;
    }

    @Override
    public Nota crear(Nota nota) {
        validarDuplicado(nota);
        nota.setAprobada(calcularAprobada(nota.getDefinitiva()));
        return notaRepository.save(nota);
    }

    @Override
    public Nota obtenerPorId(Long id) {
        return notaRepository.findById(id)
                .orElseThrow(() -> new NotaNotFoundException("Nota no encontrada con id: " + id));
    }

    @Override
    public List<Nota> obtenerTodos() {
        return notaRepository.findAll();
    }

    @Override
    public List<Nota> obtenerPorEstudiante(String idEstudiante) {
        validarEstudianteExiste(idEstudiante);
        return notaRepository.findByEstudianteIdEstudiante(idEstudiante);
    }

    @Override
    public Map<String, Object> calcularPromedio(String idEstudiante) {
        validarEstudianteExiste(idEstudiante);
        List<Nota> notas = notaRepository.findByEstudianteIdEstudiante(idEstudiante);

        int calificadas = 0;
        int aprobadas = 0;
        double suma = 0;
        for (Nota nota : notas) {
            if (nota.getDefinitiva() != null) {
                calificadas++;
                suma += nota.getDefinitiva();
                if (nota.getAprobada() != null && nota.getAprobada()) {
                    aprobadas++;
                }
            }
        }

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("idEstudiante", idEstudiante);
        resultado.put("materiasCalificadas", calificadas);
        if (calificadas == 0) {
            resultado.put("promedio", null); // aún no tiene notas definitivas
        } else {
            resultado.put("promedio", redondear(suma / calificadas));
        }
        resultado.put("aprobadas", aprobadas);
        resultado.put("reprobadas", calificadas - aprobadas);
        return resultado;
    }

    @Override
    public Nota actualizar(Long id, Nota datos) {
        Nota existente = obtenerPorId(id);

        boolean cambioEstudiante = !existente.getEstudiante().getIdEstudiante().equals(datos.getEstudiante().getIdEstudiante());
        boolean cambioMateria = !existente.getMateria().getIdMateria().equals(datos.getMateria().getIdMateria());
        if (cambioEstudiante || cambioMateria) {
            validarDuplicado(datos);
        }

        existente.setFechaRegistro(datos.getFechaRegistro());
        existente.setEstudiante(datos.getEstudiante());
        existente.setMateria(datos.getMateria());

        // Si ya tiene calificaciones parciales, la definitiva sale del promedio y no se edita a mano
        List<MovimientoNota> movimientos = movimientoNotaRepository.findByNotaIdNota(id);
        if (movimientos.isEmpty()) {
            existente.setDefinitiva(datos.getDefinitiva());
        } else {
            existente.setDefinitiva(promedioMovimientos(movimientos));
        }
        existente.setAprobada(calcularAprobada(existente.getDefinitiva()));
        return notaRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        if (!notaRepository.existsById(id)) {
            throw new NotaNotFoundException("Nota no encontrada con id: " + id);
        }
        int movimientos = movimientoNotaRepository.findByNotaIdNota(id).size();
        if (movimientos > 0) {
            throw new BusinessException("TIENE_DEPENDENCIAS", "La nota " + id + " tiene " + movimientos
                    + " calificaciones registradas; elimínalas antes de borrar la nota");
        }
        notaRepository.deleteById(id);
    }

    // Se llama cada vez que se crea, edita o borra un movimiento de nota
    @Override
    public void recalcularDefinitiva(Long idNota) {
        Nota nota = obtenerPorId(idNota);
        List<MovimientoNota> movimientos = movimientoNotaRepository.findByNotaIdNota(idNota);
        if (movimientos.isEmpty()) {
            nota.setDefinitiva(null);
        } else {
            nota.setDefinitiva(promedioMovimientos(movimientos));
        }
        nota.setAprobada(calcularAprobada(nota.getDefinitiva()));
        notaRepository.save(nota);
    }

    private Float promedioMovimientos(List<MovimientoNota> movimientos) {
        double suma = 0;
        for (MovimientoNota movimiento : movimientos) {
            suma += movimiento.getCalificacion();
        }
        return (float) redondear(suma / movimientos.size());
    }

    // Un estudiante no puede tener dos notas en curso o aprobadas de la misma materia; si la reprobó, puede repetirla
    private void validarDuplicado(Nota nota) {
        String idEstudiante = nota.getEstudiante().getIdEstudiante();
        String idMateria = nota.getMateria().getIdMateria();
        List<Nota> notasMateria = notaRepository.findByEstudianteIdEstudianteAndMateriaIdMateria(idEstudiante, idMateria);
        for (Nota existente : notasMateria) {
            if (existente.getAprobada() == null || existente.getAprobada()) {
                throw new BusinessException("NOTA_DUPLICADA", "El estudiante " + idEstudiante
                        + " ya tiene una nota en curso o aprobada para la materia " + idMateria);
            }
        }
    }

    private void validarEstudianteExiste(String idEstudiante) {
        if (!estudianteRepository.existsById(idEstudiante)) {
            throw new EstudianteNotFoundException("Estudiante no encontrado con id: " + idEstudiante);
        }
    }

    // null = la materia sigue en curso (sin definitiva)
    private Boolean calcularAprobada(Float definitiva) {
        if (definitiva == null) {
            return null;
        }
        return definitiva >= NOTA_MINIMA_APROBACION;
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
