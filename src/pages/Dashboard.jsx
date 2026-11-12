import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { ROLES } from '../config/roles';
import { Dato } from '../components/ui/Panel';
import Panel from '../components/ui/Panel';
import Alerta from '../components/ui/Alerta';
import Etiqueta from '../components/ui/Etiqueta';
import { estadoDeNota, tonoEstadoNota } from '../utils/estados';
import useCarga from '../hooks/useCarga';
import { getEstudiantes, getEstudianteById } from '../services/estudiantemysqlServices';
import { getProfesores, getProfesorById } from '../services/profesormysqlServices';
import { getGrupos } from '../services/grupomysqlServices';
import { getCarreras } from '../services/carreramysqlServices';
import { getInscripciones, getInscripcionesByGrupo } from '../services/inscripcionmysqlServices';
import { getAsignacionesByProfesor } from '../services/asignacionmysqlServices';
import { getNotasByEstudiante, getPromedioEstudiante } from '../services/notamysqlServices';
import { getResumenAsistencia } from '../services/asistenciamysqlServices';
import { getUsuarios } from '../services/usuarioServices';
import { formatoNota } from '../utils/errores';

// Cada rol tiene su propio inicio
export default function Dashboard() {
  const { usuario } = useAuth();
  if (usuario.rol === ROLES.ESTUDIANTE) return <InicioEstudiante />;
  if (usuario.rol === ROLES.PROFESOR) return <InicioProfesor />;
  return <InicioGestion />;
}

function Saludo({ nombre, texto }) {
  const hora = new Date().getHours();
  const saludo = hora < 12 ? 'Buenos días' : hora < 19 ? 'Buenas tardes' : 'Buenas noches';
  return (
    <div className="mb-8">
      <h1 className="text-2xl font-bold sm:text-3xl">{saludo}, {nombre}</h1>
      <p className="mt-1 text-gris">{texto}</p>
    </div>
  );
}

function Atajo({ a, titulo, texto }) {
  return (
    <Link to={a} className="group block rounded-xl border border-linea bg-white p-5 hover:border-andino">
      <p className="font-titulo font-semibold text-tinta group-hover:text-andino">{titulo}</p>
      <p className="mt-1 text-sm text-gris">{texto}</p>
    </Link>
  );
}

function InicioGestion() {
  const { usuario } = useAuth();
  const esAdmin = usuario.rol === ROLES.ADMINISTRATIVO;
  const anio = new Date().getFullYear();
  const { datos, error } = useCarga(
    async () => {
      const [estudiantes, profesores, grupos, carreras, inscripciones, usuarios] = await Promise.all([
        getEstudiantes(), getProfesores(), getGrupos(), getCarreras(), getInscripciones(),
        esAdmin ? getUsuarios() : Promise.resolve(null),
      ]);
      return {
        estudiantesActivos: estudiantes.filter((e) => e.activo).length,
        estudiantes: estudiantes.length,
        profesores: profesores.filter((p) => p.activo).length,
        grupos: grupos.length,
        carreras: carreras.length,
        inscripcionesAnio: inscripciones.filter((i) => i.anioInscripcion === anio).length,
        usuarios: usuarios?.length,
      };
    },
    [esAdmin],
    null
  );

  return (
    <>
      <Saludo nombre={usuario.username} texto="Así está el instituto hoy." />
      <Alerta tipo="error">{error}</Alerta>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <Dato etiqueta="Estudiantes activos" valor={datos?.estudiantesActivos ?? '…'} detalle={datos ? `de ${datos.estudiantes} registrados` : null} />
        <Dato etiqueta={`Inscripciones ${anio}`} valor={datos?.inscripcionesAnio ?? '…'} tono="andino" />
        <Dato etiqueta="Profesores activos" valor={datos?.profesores ?? '…'} />
        <Dato etiqueta="Grupos" valor={datos?.grupos ?? '…'} detalle={datos ? `en ${datos.carreras} ${datos.carreras === 1 ? 'carrera' : 'carreras'}` : null} tono="maiz" />
      </div>

      <h2 className="mb-3 mt-10 text-lg font-semibold">Tareas frecuentes</h2>
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <Atajo a="/app/inscripciones" titulo="Inscribir estudiantes" texto="Matricula estudiantes en sus grupos del semestre." />
        <Atajo a="/app/asignaciones" titulo="Asignar profesores" texto="Define quién dicta cada materia en cada grupo." />
        <Atajo a="/app/reportes" titulo="Ver estudiantes en riesgo" texto="Detecta a tiempo bajo promedio o inasistencia." />
        {esAdmin && <Atajo a="/app/usuarios" titulo="Crear cuentas" texto={`${datos?.usuarios ?? '…'} cuentas creadas. Da acceso a estudiantes y profesores.`} />}
        <Atajo a="/app/plan-estudios" titulo="Organizar el plan de estudios" texto="Qué materias se ven en cada nivel." />
        <Atajo a="/app/notas" titulo="Revisar calificaciones" texto="Notas de todos los grupos y materias." />
      </div>
    </>
  );
}

function InicioProfesor() {
  const { usuario } = useAuth();
  const { datos, error } = useCarga(
    async () => {
      const [profesor, cursos] = await Promise.all([getProfesorById(usuario.idProfesor), getAsignacionesByProfesor(usuario.idProfesor)]);
      const grupos = [...new Set(cursos.map((c) => c.idGrupo))];
      const inscripciones = (await Promise.all(grupos.map((g) => getInscripcionesByGrupo(g)))).flat();
      return { profesor, cursos, grupos: grupos.length, estudiantes: new Set(inscripciones.map((i) => i.idEstudiante)).size };
    },
    [usuario.idProfesor],
    null
  );

  return (
    <>
      <Saludo nombre={datos?.profesor.nombreProfesor.split(' ')[0] ?? usuario.username} texto="Estos son tus cursos de este semestre." />
      <Alerta tipo="error">{error}</Alerta>

      <div className="grid gap-4 sm:grid-cols-3">
        <Dato etiqueta="Cursos a cargo" valor={datos?.cursos.length ?? '…'} />
        <Dato etiqueta="Grupos" valor={datos?.grupos ?? '…'} tono="andino" />
        <Dato etiqueta="Estudiantes" valor={datos?.estudiantes ?? '…'} tono="maiz" />
      </div>

      <Panel titulo="Tus cursos" className="mt-8" acciones={<Link to="/app/mis-cursos" className="text-sm font-semibold text-andino hover:underline">Ver detalle</Link>}>
        {datos?.cursos.length === 0 ? (
          <p className="text-sm text-gris">Aún no tienes cursos asignados.</p>
        ) : (
          <ul className="divide-y divide-linea">
            {datos?.cursos.map((c) => (
              <li key={c.idAsignacion} className="flex flex-wrap items-center justify-between gap-2 py-3">
                <div>
                  <p className="font-medium text-tinta">{c.nombreMateria}</p>
                  <p className="text-sm text-gris">{c.nombreGrupo}</p>
                </div>
                <div className="flex gap-3 text-sm font-semibold">
                  <Link to={`/app/notas?materia=${c.idMateria}`} className="text-andino hover:underline">Calificar</Link>
                  <Link to={`/app/asistencias?grupo=${c.idGrupo}&tomar=1`} className="text-andino hover:underline">Tomar asistencia</Link>
                </div>
              </li>
            ))}
          </ul>
        )}
      </Panel>
    </>
  );
}

function InicioEstudiante() {
  const { usuario } = useAuth();
  const id = usuario.idEstudiante;
  const { datos, error } = useCarga(
    async () => {
      const [estudiante, promedio, asistencia, notas] = await Promise.all([
        getEstudianteById(id), getPromedioEstudiante(id), getResumenAsistencia(id), getNotasByEstudiante(id),
      ]);
      return { estudiante, promedio, asistencia, notas };
    },
    [id],
    null
  );

  const porcentaje = datos?.asistencia.porcentajeAsistencia;
  const enCurso = datos?.notas.filter((n) => n.aprobada === null || n.aprobada === undefined).length;

  return (
    <>
      <Saludo nombre={datos?.estudiante.nombreEstudiante.split(' ')[0] ?? usuario.username} texto="Así va tu semestre." />
      <Alerta tipo="error">{error}</Alerta>

      <div className="grid gap-4 sm:grid-cols-3">
        <Dato etiqueta="Promedio general" valor={datos ? formatoNota(datos.promedio.promedio) : '…'} tono={datos?.promedio.promedio !== null && datos?.promedio.promedio < 3 ? 'rojo' : 'tinta'} />
        <Dato etiqueta="Asistencia" valor={datos ? (porcentaje === null ? '—' : `${porcentaje}%`) : '…'} tono={porcentaje !== null && porcentaje < 80 ? 'rojo' : 'andino'} detalle="Mínimo exigido: 80 %" />
        <Dato etiqueta="Materias en curso" valor={enCurso ?? '…'} tono="maiz" detalle={datos ? `Aprobadas hasta ahora: ${datos.promedio.aprobadas}` : null} />
      </div>

      <Panel titulo="Tus materias" className="mt-8" acciones={<Link to="/app/mis-notas" className="text-sm font-semibold text-andino hover:underline">Ver notas</Link>}>
        {datos?.notas.length === 0 ? (
          <p className="text-sm text-gris">Aún no tienes notas registradas.</p>
        ) : (
          <ul className="divide-y divide-linea">
            {datos?.notas.map((n) => {
              const estado = estadoDeNota(n.aprobada);
              return (
                <li key={n.idNota} className="flex items-center justify-between gap-3 py-3">
                  <span className="font-medium text-tinta">{n.nombreMateria}</span>
                  <span className="flex items-center gap-3">
                    <Etiqueta tono={tonoEstadoNota(estado)}>{estado}</Etiqueta>
                    <span className="w-10 text-right font-semibold">{formatoNota(n.definitiva)}</span>
                  </span>
                </li>
              );
            })}
          </ul>
        )}
      </Panel>

      <div className="mt-6 grid gap-4 sm:grid-cols-2">
        <Atajo a="/app/mi-asistencia" titulo="Mi asistencia" texto="Revisa tus clases y faltas." />
        <Atajo a="/app/mi-boletin" titulo="Mi boletín" texto="Descárgalo o imprímelo." />
      </div>
    </>
  );
}
