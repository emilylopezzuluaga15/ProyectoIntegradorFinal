import Etiqueta from './ui/Etiqueta';
import { tonoEstadoNota } from '../utils/estados';
import { INSTITUCION } from '../config/institucion';
import { formatoNota } from '../utils/errores';

// Boletín académico. Lo usan el estudiante (Mi boletín) y el personal (Reportes).
export default function BoletinVista({ boletin }) {
  const { resumenNotas: notas, asistencia } = boletin;

  return (
    <article className="rounded-xl border border-linea bg-white p-6 sm:p-8 print:border-0 print:p-0">
      <header className="flex flex-wrap items-start justify-between gap-4 border-b-2 border-tinta pb-5">
        <div>
          <p className="text-sm text-gris">{INSTITUCION.nombre}</p>
          <h2 className="mt-1 text-2xl font-bold">Boletín académico</h2>
        </div>
        <div className="text-right">
          <p className="font-titulo text-lg font-semibold text-tinta">{boletin.nombreEstudiante}</p>
          <p className="text-sm text-gris">Documento {boletin.idEstudiante}</p>
          {boletin.estado && <p className="mt-1"><Etiqueta tono="verde">{boletin.estado}</Etiqueta></p>}
        </div>
      </header>

      <dl className="grid grid-cols-2 gap-6 border-b border-linea py-6 sm:grid-cols-4">
        <div>
          <dt className="text-sm text-gris">Promedio general</dt>
          <dd className="font-titulo text-3xl font-bold text-tinta">{formatoNota(notas?.promedio)}</dd>
        </div>
        <div>
          <dt className="text-sm text-gris">Materias aprobadas</dt>
          <dd className="font-titulo text-3xl font-bold text-andino">{notas?.aprobadas ?? 0}</dd>
        </div>
        <div>
          <dt className="text-sm text-gris">Materias reprobadas</dt>
          <dd className="font-titulo text-3xl font-bold text-red-700">{notas?.reprobadas ?? 0}</dd>
        </div>
        <div>
          <dt className="text-sm text-gris">Asistencia</dt>
          <dd className="font-titulo text-3xl font-bold text-tinta">
            {asistencia?.porcentajeAsistencia === null || asistencia?.porcentajeAsistencia === undefined ? '—' : `${asistencia.porcentajeAsistencia}%`}
          </dd>
        </div>
      </dl>

      <section className="py-6">
        <h3 className="mb-3 text-lg font-semibold">Materias</h3>
        {boletin.materias?.length ? (
          <table className="w-full border-collapse text-left text-sm">
            <thead>
              <tr className="border-b border-linea text-gris">
                <th className="py-2 font-medium">Materia</th>
                <th className="py-2 font-medium">Calificaciones</th>
                <th className="py-2 font-medium">Definitiva</th>
                <th className="py-2 font-medium">Estado</th>
              </tr>
            </thead>
            <tbody>
              {boletin.materias.map((m) => (
                <tr key={m.idNota} className="border-b border-linea/60">
                  <td className="py-2.5 font-medium text-tinta">{m.nombreMateria}</td>
                  <td className="py-2.5">{m.calificaciones}</td>
                  <td className="py-2.5 font-semibold">{formatoNota(m.definitiva)}</td>
                  <td className="py-2.5"><Etiqueta tono={tonoEstadoNota(m.estado)}>{m.estado}</Etiqueta></td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p className="text-sm text-gris">Todavía no hay materias registradas.</p>
        )}
      </section>

      <section className="grid gap-6 border-t border-linea pt-6 sm:grid-cols-2">
        <div>
          <h3 className="mb-2 text-lg font-semibold">Grupos</h3>
          {boletin.grupos?.length ? (
            <ul className="space-y-1 text-sm">
              {boletin.grupos.map((g) => <li key={`${g.idGrupo}-${g.anio}`}>{g.nombreGrupo} <span className="text-gris">({g.anio})</span></li>)}
            </ul>
          ) : <p className="text-sm text-gris">Sin inscripciones.</p>}
        </div>
        <div>
          <h3 className="mb-2 text-lg font-semibold">Detalle de asistencia</h3>
          <p className="text-sm">
            {asistencia?.asistidas ?? 0} clases asistidas de {asistencia?.totalClases ?? 0}.{' '}
            {asistencia?.inasistencias ?? 0} inasistencias, {asistencia?.inasistenciasJustificadas ?? 0} justificadas.
          </p>
        </div>
      </section>
    </article>
  );
}
