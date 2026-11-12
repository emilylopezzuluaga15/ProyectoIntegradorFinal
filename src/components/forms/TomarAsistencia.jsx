import { useEffect, useState } from 'react';
import Campo, { claseInput } from '../ui/Campo';
import Boton from '../ui/Boton';
import { getInscripcionesByGrupo } from '../../services/inscripcionmysqlServices';
import { createAsistencia } from '../../services/asistenciamysqlServices';
import { mensajeError, hoy } from '../../utils/errores';

// Llamado a lista: se elige grupo y fecha, y se marca a cada estudiante inscrito.
// Al guardar se crea un registro de asistencia por estudiante.
export default function TomarAsistencia({ grupos, grupoInicial, onTerminar, onCancelar }) {
  const [idGrupo, setIdGrupo] = useState(grupoInicial || grupos[0]?.idGrupo || '');
  const [fecha, setFecha] = useState(hoy());
  const [lista, setLista] = useState([]);
  const [cargando, setCargando] = useState(false);
  const [guardando, setGuardando] = useState(false);
  const [errores, setErrores] = useState([]);

  useEffect(() => {
    if (!idGrupo) return;
    let vigente = true;
    setCargando(true);
    getInscripcionesByGrupo(idGrupo)
      .then((inscripciones) => {
        if (!vigente) return;
        const unicos = [...new Map(inscripciones.map((i) => [i.idEstudiante, i])).values()];
        setLista(unicos.map((i) => ({ idEstudiante: i.idEstudiante, nombre: i.nombreEstudiante, asistio: true, justificada: false, observacion: '' })));
      })
      .catch(() => vigente && setLista([]))
      .finally(() => vigente && setCargando(false));
    return () => {
      vigente = false;
    };
  }, [idGrupo]);

  const marcar = (idEstudiante, cambios) =>
    setLista((prev) => prev.map((f) => (f.idEstudiante === idEstudiante ? { ...f, ...cambios } : f)));

  const guardar = async () => {
    setGuardando(true);
    const fallidos = [];
    for (const f of lista) {
      try {
        await createAsistencia({
          idEstudiante: f.idEstudiante,
          idGrupo,
          fechaAsistencia: fecha,
          asistio: f.asistio,
          justificada: f.asistio ? null : f.justificada,
          observacion: f.observacion.trim() || null,
        });
      } catch (err) {
        fallidos.push(`${f.nombre}: ${mensajeError(err)}`);
      }
    }
    setGuardando(false);
    if (fallidos.length) {
      setErrores(fallidos);
    } else {
      onTerminar(`Asistencia del ${fecha} guardada para ${lista.length} estudiantes.`);
    }
  };

  const ausentes = lista.filter((f) => !f.asistio).length;

  if (errores.length) {
    return (
      <div>
        <p className="text-sm">Se guardaron {lista.length - errores.length} de {lista.length} registros. Estos no se pudieron guardar:</p>
        <ul className="mt-3 space-y-1 rounded-lg bg-red-50 p-3 text-sm text-red-800">
          {errores.map((e) => <li key={e}>{e}</li>)}
        </ul>
        <div className="mt-5 flex justify-end">
          <Boton onClick={() => onTerminar(null)}>Entendido</Boton>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="grid gap-4 sm:grid-cols-2">
        <Campo etiqueta="Grupo" id="ta-grupo">
          <select id="ta-grupo" value={idGrupo} onChange={(e) => setIdGrupo(e.target.value)} className={claseInput}>
            {grupos.map((g) => <option key={g.idGrupo} value={g.idGrupo}>{g.nombreGrupo}</option>)}
          </select>
        </Campo>
        <Campo etiqueta="Fecha de la clase" id="ta-fecha">
          <input id="ta-fecha" type="date" value={fecha} max={hoy()} onChange={(e) => setFecha(e.target.value)} className={claseInput} />
        </Campo>
      </div>

      {cargando ? (
        <p className="py-6 text-center text-sm text-gris">Cargando estudiantes…</p>
      ) : lista.length === 0 ? (
        <p className="py-6 text-center text-sm text-gris">Este grupo no tiene estudiantes inscritos.</p>
      ) : (
        <>
          <p className="text-sm text-gris">Todos aparecen como presentes. Marca solo a quienes faltaron.</p>
          <ul className="max-h-80 divide-y divide-linea overflow-y-auto rounded-lg border border-linea">
            {lista.map((f) => (
              <li key={f.idEstudiante} className="px-3 py-2.5">
                <div className="flex items-center justify-between gap-3">
                  <span className="text-sm font-medium text-tinta">{f.nombre}</span>
                  <div className="flex overflow-hidden rounded-lg border border-linea text-xs font-semibold" role="group" aria-label={`Asistencia de ${f.nombre}`}>
                    <button type="button" aria-pressed={f.asistio} onClick={() => marcar(f.idEstudiante, { asistio: true })} className={`px-3 py-1.5 ${f.asistio ? 'bg-andino text-white' : 'bg-white text-gris'}`}>Presente</button>
                    <button type="button" aria-pressed={!f.asistio} onClick={() => marcar(f.idEstudiante, { asistio: false })} className={`px-3 py-1.5 ${!f.asistio ? 'bg-red-700 text-white' : 'bg-white text-gris'}`}>Faltó</button>
                  </div>
                </div>
                {!f.asistio && (
                  <div className="mt-2 flex flex-wrap items-center gap-3">
                    <label className="flex items-center gap-2 text-xs">
                      <input type="checkbox" checked={f.justificada} onChange={(e) => marcar(f.idEstudiante, { justificada: e.target.checked })} className="accent-andino" />
                      Justificada
                    </label>
                    <input value={f.observacion} onChange={(e) => marcar(f.idEstudiante, { observacion: e.target.value })} maxLength={100} placeholder="Observación (opcional)" aria-label={`Observación de ${f.nombre}`} className={`${claseInput} flex-1 py-1 text-xs`} />
                  </div>
                )}
              </li>
            ))}
          </ul>
        </>
      )}

      <div className="flex items-center justify-between gap-2 pt-2">
        <span className="text-sm text-gris">{lista.length - ausentes} presentes, {ausentes} ausentes</span>
        <div className="flex gap-2">
          <Boton variante="secundario" onClick={onCancelar}>Cancelar</Boton>
          <Boton onClick={guardar} disabled={guardando || lista.length === 0}>{guardando ? 'Guardando…' : 'Guardar asistencia'}</Boton>
        </div>
      </div>
    </div>
  );
}
