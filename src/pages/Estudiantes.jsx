import PaginaCrud from '../components/PaginaCrud';
import Etiqueta from '../components/ui/Etiqueta';
import { OPCIONES_GENERO, OPCIONES_ESTADO_ESTUDIANTE } from '../config/opciones';
import { getEstudiantes, createEstudiante, updateEstudiante, deleteEstudiante } from '../services/estudiantemysqlServices';
import { useAuth } from '../context/AuthContext';
import { esGestor } from '../config/roles';
import { hoy } from '../utils/errores';

const TONO_ESTADO = { Activo: 'verde', Retirado: 'rojo', Graduado: 'azul', Suspendido: 'dorado' };

const CAMPOS = [
  { nombre: 'idEstudiante', etiqueta: 'Documento', requerido: true, max: 20, soloAlCrear: true, placeholder: 'EST001' },
  { nombre: 'nombreEstudiante', etiqueta: 'Nombre completo', requerido: true, max: 50 },
  { nombre: 'fechaIngreso', etiqueta: 'Fecha de ingreso', tipo: 'date', requerido: true, porDefecto: hoy() },
  { nombre: 'genero', etiqueta: 'Género', tipo: 'select', opciones: OPCIONES_GENERO },
  { nombre: 'celular', etiqueta: 'Celular', tipo: 'number' },
  { nombre: 'correo', etiqueta: 'Correo', tipo: 'email', max: 50 },
  { nombre: 'estado', etiqueta: 'Estado académico', tipo: 'select', opciones: OPCIONES_ESTADO_ESTUDIANTE, porDefecto: 'Activo' },
  { nombre: 'activo', etiqueta: 'Estudiante activo (puede inscribirse)', tipo: 'checkbox' },
];

// Coordinación y administración pueden editar. El profesor solo consulta la lista.
export default function Estudiantes() {
  const { usuario } = useAuth();
  const puedeEditar = esGestor(usuario);

  return (
    <PaginaCrud
      titulo="Estudiantes"
      descripcion={puedeEditar ? 'Registro de estudiantes del instituto.' : 'Consulta los datos de contacto de los estudiantes.'}
      entidad="Estudiante"
      cargar={getEstudiantes}
      crear={puedeEditar ? createEstudiante : undefined}
      actualizar={puedeEditar ? updateEstudiante : undefined}
      eliminar={puedeEditar ? deleteEstudiante : undefined}
      idDe={(e) => e.idEstudiante}
      nombreDe={(e) => e.nombreEstudiante}
      textoBusqueda={(e) => `${e.idEstudiante} ${e.nombreEstudiante} ${e.correo ?? ''}`}
      columnas={[
        { titulo: 'Documento', campo: 'idEstudiante' },
        { titulo: 'Nombre', render: (e) => <span className="font-medium text-tinta">{e.nombreEstudiante}</span> },
        { titulo: 'Correo', campo: 'correo' },
        { titulo: 'Celular', campo: 'celular' },
        { titulo: 'Estado', render: (e) => (e.estado ? <Etiqueta tono={TONO_ESTADO[e.estado]}>{e.estado}</Etiqueta> : '—') },
      ]}
      campos={CAMPOS}
    />
  );
}
