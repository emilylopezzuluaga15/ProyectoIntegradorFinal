import Etiqueta from './ui/Etiqueta';
import { tonoEstadoNota } from '../utils/estados';
import { formatoNota } from '../utils/errores';

// Planilla de notas de una materia en un grupo (respuesta de /api/reportes/grupo/{id}/planilla-notas)
export default function PlanillaVista({ planilla }) {
  return (
    <div>
      <dl className="mb-5 grid grid-cols-2 gap-4 sm:grid-cols-4">
        <div><dt className="text-sm text-gris">Profesor</dt><dd className="font-semibold text-tinta">{planilla.profesor}</dd></div>
        <div><dt className="text-sm text-gris">Promedio del grupo</dt><dd className="font-titulo text-2xl font-bold text-tinta">{formatoNota(planilla.promedioGrupo)}</dd></div>
        <div><dt className="text-sm text-gris">Aprobados</dt><dd className="font-titulo text-2xl font-bold text-andino">{planilla.aprobados}</dd></div>
        <div><dt className="text-sm text-gris">Reprobados</dt><dd className="font-titulo text-2xl font-bold text-red-700">{planilla.reprobados}</dd></div>
      </dl>
      {planilla.estudiantes.length === 0 ? (
        <p className="text-sm text-gris">No hay estudiantes inscritos en {planilla.anio}.</p>
      ) : (
        <div className="overflow-x-auto">
          <table className="w-full min-w-[480px] border-collapse text-left text-sm">
            <thead>
              <tr className="border-b border-linea text-gris">
                <th className="py-2 font-medium">Estudiante</th>
                <th className="py-2 font-medium">Parciales</th>
                <th className="py-2 font-medium">Definitiva</th>
                <th className="py-2 font-medium">Estado</th>
              </tr>
            </thead>
            <tbody>
              {planilla.estudiantes.map((e) => (
                <tr key={e.idEstudiante} className="border-b border-linea/60">
                  <td className="py-2.5 font-medium text-tinta">{e.nombreEstudiante}</td>
                  <td className="py-2.5">{e.calificaciones}</td>
                  <td className="py-2.5 font-semibold">{formatoNota(e.definitiva)}</td>
                  <td className="py-2.5"><Etiqueta tono={tonoEstadoNota(e.estado)}>{e.estado}</Etiqueta></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

// Estudiantes en riesgo (respuesta de /api/reportes/grupo/{id}/en-riesgo)
export function RiesgoVista({ reporte }) {
  return (
    <div>
      <p className="mb-4 text-sm">
        <span className="font-semibold text-red-700">{reporte.totalEnRiesgo}</span> de {reporte.totalInscritos} estudiantes inscritos en {reporte.anio} tienen
        promedio menor a 3.0 o asistencia menor al 80 %.
      </p>
      {reporte.estudiantes.length === 0 ? (
        <p className="rounded-lg bg-andino-suave px-4 py-3 text-sm text-andino-oscuro">Ningún estudiante está en riesgo en este grupo.</p>
      ) : (
        <ul className="divide-y divide-linea rounded-lg border border-linea">
          {reporte.estudiantes.map((e) => (
            <li key={e.idEstudiante} className="flex flex-wrap items-center justify-between gap-2 px-4 py-3">
              <div>
                <p className="font-medium text-tinta">{e.nombreEstudiante}</p>
                <p className="text-xs text-gris">Promedio {formatoNota(e.promedio)} · Asistencia {e.porcentajeAsistencia ?? '—'}%</p>
              </div>
              <div className="flex flex-wrap gap-1">
                {e.motivos.map((m) => <Etiqueta key={m} tono="rojo">{m}</Etiqueta>)}
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
