import PaginaCrud from '../components/PaginaCrud';
import useCarga from '../hooks/useCarga';
import { getInscripciones, createInscripcion, updateInscripcion, deleteInscripcion } from '../services/inscripcionmysqlServices';
import { getEstudiantes } from '../services/estudiantemysqlServices';
import { getGrupos } from '../services/grupomysqlServices';

export default function Inscripciones() {
  const { datos: estudiantes } = useCarga(getEstudiantes);
  const { datos: grupos } = useCarga(getGrupos);
  const anioActual = new Date().getFullYear();

  const campos = [
    {
      nombre: 'idEstudiante',
      etiqueta: 'Estudiante',
      tipo: 'select',
      requerido: true,
      anchoCompleto: true,
      opciones: estudiantes.filter((e) => e.activo).map((e) => ({ valor: e.idEstudiante, texto: `${e.nombreEstudiante} (${e.idEstudiante})` })),
    },
    {
      nombre: 'idGrupo',
      etiqueta: 'Grupo',
      tipo: 'select',
      requerido: true,
      anchoCompleto: true,
      opciones: grupos.map((g) => ({ valor: g.idGrupo, texto: `${g.nombreGrupo} — ${g.nombreNivel}` })),
    },
    { nombre: 'anioInscripcion', etiqueta: 'Año', tipo: 'number', requerido: true, porDefecto: anioActual, min: 2000 },
    { nombre: 'cohorte', etiqueta: 'Cohorte', tipo: 'number', placeholder: `${anioActual}1`, ayuda: 'Por ejemplo 20261 para el primer semestre de 2026.' },
  ];

  return (
    <PaginaCrud
      titulo="Inscripciones"
      descripcion="Matricula a cada estudiante en su grupo. El sistema revisa que haya cupo y que el estudiante esté activo."
      entidad="Inscripción"
      femenino
      cargar={getInscripciones}
      crear={createInscripcion}
      actualizar={updateInscripcion}
      eliminar={deleteInscripcion}
      idDe={(i) => i.idInscripcion}
      nombreDe={(i) => `${i.nombreEstudiante} en ${i.nombreGrupo}`}
      textoBusqueda={(i) => `${i.nombreEstudiante} ${i.idEstudiante} ${i.nombreGrupo} ${i.anioInscripcion}`}
      aviso={estudiantes.length === 0 || grupos.length === 0 ? 'Necesitas al menos un estudiante activo y un grupo.' : null}
      columnas={[
        { titulo: 'Estudiante', render: (i) => <span className="font-medium text-tinta">{i.nombreEstudiante}</span> },
        { titulo: 'Documento', campo: 'idEstudiante' },
        { titulo: 'Grupo', campo: 'nombreGrupo' },
        { titulo: 'Año', campo: 'anioInscripcion' },
        { titulo: 'Cohorte', campo: 'cohorte' },
      ]}
      campos={campos}
    />
  );
}
