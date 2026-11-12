import { useState } from 'react';
import Encabezado from '../../components/ui/Encabezado';
import Alerta from '../../components/ui/Alerta';
import { Dato } from '../../components/ui/Panel';
import Etiqueta from '../../components/ui/Etiqueta';
import { estadoDeNota, tonoEstadoNota } from '../../utils/estados';
import useCarga from '../../hooks/useCarga';
import { useAuth } from '../../context/AuthContext';
import { getNotasByEstudiante, getPromedioEstudiante } from '../../services/notamysqlServices';
import { getMovimientosByNota } from '../../services/movimientonotamysqlServices';
import { formatoNota } from '../../utils/errores';

export default function MisNotas() {
  const { usuario } = useAuth();
  const id = usuario.idEstudiante;
  const { datos, cargando, error } = useCarga(
    async () => {
      const [notas, promedio] = await Promise.all([getNotasByEstudiante(id), getPromedioEstudiante(id)]);
      return { notas, promedio };
    },
    [id],
    { notas: [], promedio: null }
  );
  const [abierta, setAbierta] = useState(null);
  const [parciales, setParciales] = useState({});

  const verParciales = async (idNota) => {
    if (abierta === idNota) {
      setAbierta(null);
      return;
    }
    setAbierta(idNota);
    if (!parciales[idNota]) {
      const lista = await getMovimientosByNota(idNota).catch(() => []);
      setParciales((prev) => ({ ...prev, [idNota]: lista }));
    }
  };

  const { notas, promedio } = datos;

  return (
    <>
      <Encabezado titulo="Mis notas" descripcion="Tus materias, con la nota definitiva y las calificaciones parciales." />
      <Alerta tipo="error">{error}</Alerta>

      <div className="mb-8 grid gap-4 sm:grid-cols-3">
        <Dato etiqueta="Promedio general" valor={formatoNota(promedio?.promedio)} detalle={`${promedio?.materiasCalificadas ?? 0} materias con definitiva`} />
        <Dato etiqueta="Materias aprobadas" valor={promedio?.aprobadas ?? 0} tono="andino" />
        <Dato etiqueta="Materias reprobadas" valor={promedio?.reprobadas ?? 0} tono="rojo" />
      </div>

      {cargando ? (
        <p className="text-gris">Cargando tus notas…</p>
      ) : notas.length === 0 ? (
        <Alerta tipo="aviso">Todavía no tienes notas registradas. Aparecerán cuando tus profesores te califiquen.</Alerta>
      ) : (
        <ul className="space-y-3">
          {notas.map((n) => {
            const estado = estadoDeNota(n.aprobada);
            const abiertaAhora = abierta === n.idNota;
            return (
              <li key={n.idNota} className="rounded-xl border border-linea bg-white">
                <button
                  onClick={() => verParciales(n.idNota)}
                  aria-expanded={abiertaAhora}
                  className="flex w-full items-center justify-between gap-4 p-4 text-left sm:p-5"
                >
                  <div>
                    <p className="font-titulo text-lg font-semibold text-tinta">{n.nombreMateria}</p>
                    <p className="text-sm text-gris">Registrada el {n.fechaRegistro}</p>
                  </div>
                  <div className="flex items-center gap-4">
                    <Etiqueta tono={tonoEstadoNota(estado)}>{estado}</Etiqueta>
                    <span className="w-12 text-right font-titulo text-2xl font-bold text-tinta">{formatoNota(n.definitiva)}</span>
                  </div>
                </button>
                {abiertaAhora && (
                  <div className="border-t border-linea px-4 py-3 sm:px-5">
                    {!parciales[n.idNota] ? (
                      <p className="text-sm text-gris">Cargando…</p>
                    ) : parciales[n.idNota].length === 0 ? (
                      <p className="text-sm text-gris">Aún no hay calificaciones parciales.</p>
                    ) : (
                      <ul className="flex flex-wrap gap-2">
                        {parciales[n.idNota].map((m) => (
                          <li key={m.idMovimiento} className="rounded-lg bg-niebla px-3 py-2 text-sm">
                            <span className="font-semibold text-tinta">{formatoNota(m.calificacion)}</span>
                            <span className="ml-2 text-gris">{m.fechaRegistro}</span>
                          </li>
                        ))}
                      </ul>
                    )}
                  </div>
                )}
              </li>
            );
          })}
        </ul>
      )}
    </>
  );
}
