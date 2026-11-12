import Encabezado from '../../components/ui/Encabezado';
import Alerta from '../../components/ui/Alerta';
import Tabla from '../../components/ui/Tabla';
import Panel from '../../components/ui/Panel';
import Etiqueta from '../../components/ui/Etiqueta';
import useCarga from '../../hooks/useCarga';
import { useAuth } from '../../context/AuthContext';
import { getAsistenciasByEstudiante, getResumenAsistencia } from '../../services/asistenciamysqlServices';

const MINIMO = 80; // Porcentaje mínimo de asistencia que exige el instituto

export default function MiAsistencia() {
  const { usuario } = useAuth();
  const id = usuario.idEstudiante;
  const { datos, cargando, error } = useCarga(
    async () => {
      const [registros, resumen] = await Promise.all([getAsistenciasByEstudiante(id), getResumenAsistencia(id)]);
      return { registros: registros.sort((a, b) => b.fechaAsistencia.localeCompare(a.fechaAsistencia)), resumen };
    },
    [id],
    { registros: [], resumen: null }
  );

  const { registros, resumen } = datos;
  const porcentaje = resumen?.porcentajeAsistencia;
  const enRiesgo = porcentaje !== null && porcentaje !== undefined && porcentaje < MINIMO;

  return (
    <>
      <Encabezado titulo="Mi asistencia" descripcion={`Debes asistir como mínimo al ${MINIMO} % de las clases.`} />
      <Alerta tipo="error">{error}</Alerta>

      <Panel className="mb-6">
        <div className="flex flex-wrap items-end justify-between gap-4">
          <div>
            <p className="text-sm text-gris">Porcentaje de asistencia</p>
            <p className={`font-titulo text-5xl font-bold ${enRiesgo ? 'text-red-700' : 'text-andino'}`}>
              {porcentaje === null || porcentaje === undefined ? '—' : `${porcentaje}%`}
            </p>
          </div>
          <dl className="flex gap-8 text-sm">
            <div><dt className="text-gris">Clases</dt><dd className="text-xl font-semibold text-tinta">{resumen?.totalClases ?? 0}</dd></div>
            <div><dt className="text-gris">Faltas</dt><dd className="text-xl font-semibold text-tinta">{resumen?.inasistencias ?? 0}</dd></div>
            <div><dt className="text-gris">Justificadas</dt><dd className="text-xl font-semibold text-tinta">{resumen?.inasistenciasJustificadas ?? 0}</dd></div>
          </dl>
        </div>
        <div className="relative mt-5 h-3 rounded-full bg-niebla" role="img" aria-label={`Asistencia ${porcentaje ?? 0} por ciento; mínimo ${MINIMO}`}>
          <div className={`h-3 rounded-full ${enRiesgo ? 'bg-red-700' : 'bg-andino'}`} style={{ width: `${porcentaje ?? 0}%` }} />
          <div className="absolute -top-1 h-5 w-0.5 bg-maiz" style={{ left: `${MINIMO}%` }} />
        </div>
        {enRiesgo && <p className="mt-3 text-sm text-red-800">Estás por debajo del mínimo. Habla con tu coordinador si tienes faltas por justificar.</p>}
      </Panel>

      <Tabla
        cargando={cargando}
        filas={registros}
        claveFila={(r) => r.idAsistencia}
        vacio="Todavía no hay registros de asistencia."
        columnas={[
          { titulo: 'Fecha', campo: 'fechaAsistencia' },
          { titulo: 'Grupo', campo: 'nombreGrupo' },
          {
            titulo: 'Asistencia',
            render: (r) => r.asistio
              ? <Etiqueta tono="verde">Presente</Etiqueta>
              : <Etiqueta tono={r.justificada ? 'dorado' : 'rojo'}>{r.justificada ? 'Falta justificada' : 'Faltó'}</Etiqueta>,
          },
          { titulo: 'Observación', campo: 'observacion' },
        ]}
      />
    </>
  );
}
