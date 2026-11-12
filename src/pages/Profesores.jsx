import PaginaCrud from '../components/PaginaCrud';
import Etiqueta from '../components/ui/Etiqueta';
import { OPCIONES_GENERO } from '../config/opciones';
import { getProfesores, createProfesor, updateProfesor, deleteProfesor } from '../services/profesormysqlServices';
import { hoy } from '../utils/errores';

const CAMPOS = [
  { nombre: 'idProfesor', etiqueta: 'Documento', requerido: true, max: 20, soloAlCrear: true, placeholder: 'PROF001' },
  { nombre: 'nombreProfesor', etiqueta: 'Nombre completo', requerido: true, max: 50 },
  { nombre: 'fechaIngreso', etiqueta: 'Fecha de ingreso', tipo: 'date', requerido: true, porDefecto: hoy() },
  { nombre: 'genero', etiqueta: 'Género', tipo: 'select', opciones: OPCIONES_GENERO },
  { nombre: 'celular', etiqueta: 'Celular', tipo: 'number' },
  { nombre: 'correo', etiqueta: 'Correo', tipo: 'email', max: 50 },
  { nombre: 'especializacion', etiqueta: 'Especialización', max: 50, anchoCompleto: true },
  { nombre: 'activo', etiqueta: 'Profesor activo (puede recibir asignaciones)', tipo: 'checkbox', anchoCompleto: true },
];

export default function Profesores() {
  return (
    <PaginaCrud
      titulo="Profesores"
      descripcion="Docentes del instituto. Para que un profesor ingrese a la plataforma, el administrador le crea un usuario."
      entidad="Profesor"
      cargar={getProfesores}
      crear={createProfesor}
      actualizar={updateProfesor}
      eliminar={deleteProfesor}
      idDe={(p) => p.idProfesor}
      nombreDe={(p) => p.nombreProfesor}
      textoBusqueda={(p) => `${p.idProfesor} ${p.nombreProfesor} ${p.correo ?? ''} ${p.especializacion ?? ''}`}
      columnas={[
        { titulo: 'Documento', campo: 'idProfesor' },
        { titulo: 'Nombre', render: (p) => <span className="font-medium text-tinta">{p.nombreProfesor}</span> },
        { titulo: 'Especialización', campo: 'especializacion' },
        { titulo: 'Correo', campo: 'correo' },
        { titulo: 'Estado', render: (p) => <Etiqueta tono={p.activo ? 'verde' : 'gris'}>{p.activo ? 'Activo' : 'Inactivo'}</Etiqueta> },
      ]}
      campos={CAMPOS}
    />
  );
}
