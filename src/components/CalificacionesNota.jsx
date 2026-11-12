import { useCallback, useEffect, useState } from 'react';
import Boton from './ui/Boton';
import Alerta from './ui/Alerta';
import Etiqueta from './ui/Etiqueta';
import { estadoDeNota, tonoEstadoNota } from '../utils/estados';
import { claseInput } from './ui/Campo';
import { getMovimientosByNota, createMovimientoNota, deleteMovimientoNota } from '../services/movimientonotamysqlServices';
import { getNotaById } from '../services/notamysqlServices';
import { mensajeError, hoy, formatoNota } from '../utils/errores';

// Calificaciones parciales de una nota. Cada vez que se agrega o borra una, el backend recalcula la definitiva.
export default function CalificacionesNota({ nota: notaInicial, onCambio }) {
  const [nota, setNota] = useState(notaInicial);
  const [movimientos, setMovimientos] = useState([]);
  const [calificacion, setCalificacion] = useState('');
  const [fecha, setFecha] = useState(hoy());
  const [error, setError] = useState(null);

  const cargar = useCallback(async () => {
    try {
      const [lista, actualizada] = await Promise.all([getMovimientosByNota(notaInicial.idNota), getNotaById(notaInicial.idNota)]);
      setMovimientos(lista);
      setNota(actualizada);
      setError(null);
    } catch (err) {
      setError(mensajeError(err));
    }
  }, [notaInicial.idNota]);

  useEffect(() => {
    cargar();
  }, [cargar]);

  const agregar = async (e) => {
    e.preventDefault();
    try {
      await createMovimientoNota({ idNota: nota.idNota, fechaRegistro: fecha, calificacion: Number(calificacion) });
      setCalificacion('');
      await cargar();
      onCambio?.();
    } catch (err) {
      setError(mensajeError(err));
    }
  };

  const borrar = async (id) => {
    try {
      await deleteMovimientoNota(id);
      await cargar();
      onCambio?.();
    } catch (err) {
      setError(mensajeError(err));
    }
  };

  const estado = estadoDeNota(nota.aprobada);

  return (
    <div>
      <div className="mb-5 flex items-center justify-between rounded-xl bg-niebla p-4">
        <div>
          <p className="text-sm text-gris">Definitiva</p>
          <p className="font-titulo text-3xl font-bold text-tinta">{formatoNota(nota.definitiva)}</p>
        </div>
        <Etiqueta tono={tonoEstadoNota(estado)}>{estado}</Etiqueta>
      </div>

      <Alerta tipo="error" onCerrar={() => setError(null)}>{error}</Alerta>

      <h4 className="mb-2 text-sm font-semibold text-pizarra">Calificaciones parciales</h4>
      {movimientos.length === 0 ? (
        <p className="mb-4 text-sm text-gris">Aún no hay calificaciones. Agrega la primera abajo.</p>
      ) : (
        <ul className="mb-4 divide-y divide-linea rounded-lg border border-linea">
          {movimientos.map((m) => (
            <li key={m.idMovimiento} className="flex items-center justify-between px-3 py-2 text-sm">
              <span className="text-gris">{m.fechaRegistro}</span>
              <span className="font-semibold text-tinta">{formatoNota(m.calificacion)}</span>
              <Boton variante="textoPeligro" tamano="pequeno" onClick={() => borrar(m.idMovimiento)}>Quitar</Boton>
            </li>
          ))}
        </ul>
      )}

      <form onSubmit={agregar} className="flex flex-wrap items-end gap-2">
        <div className="w-32">
          <label htmlFor="mov-cal" className="mb-1 block text-xs font-medium">Calificación</label>
          <input id="mov-cal" type="number" step="0.1" min="0" max="5" value={calificacion} onChange={(e) => setCalificacion(e.target.value)} required placeholder="0.0 a 5.0" className={claseInput} />
        </div>
        <div className="w-40">
          <label htmlFor="mov-fecha" className="mb-1 block text-xs font-medium">Fecha</label>
          <input id="mov-fecha" type="date" value={fecha} max={hoy()} onChange={(e) => setFecha(e.target.value)} required className={claseInput} />
        </div>
        <Boton type="submit">Agregar</Boton>
      </form>
    </div>
  );
}
