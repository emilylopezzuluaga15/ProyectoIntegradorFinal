import PaginaCrud from '../components/PaginaCrud';
import AsignacionForm from '../components/forms/AsignacionForm';
import useCarga from '../hooks/useCarga';
import { getAsignaciones, createAsignacion, updateAsignacion, deleteAsignacion } from '../services/asignacionmysqlServices';
import { getProfesores } from '../services/profesormysqlServices';
import { getGrupos } from '../services/grupomysqlServices';
import { getNivelMaterias } from '../services/nivelmateriamysqlServices';

export default function Asignaciones() {
  const { datos: profesores } = useCarga(getProfesores);
  const { datos: grupos } = useCarga(getGrupos);
  const { datos: planEstudios } = useCarga(getNivelMaterias);

  const Formulario = (props) => (
    <AsignacionForm {...props} profesores={profesores} grupos={grupos} planEstudios={planEstudios} />
  );

  return (
    <PaginaCrud
      titulo="Asignaciones"
      descripcion="Qué profesor dicta cada materia en cada grupo. Cada materia tiene un solo profesor por grupo."
      entidad="Asignación"
      femenino
      cargar={getAsignaciones}
      crear={createAsignacion}
      actualizar={updateAsignacion}
      eliminar={deleteAsignacion}
      idDe={(a) => a.idAsignacion}
      nombreDe={(a) => `${a.nombreProfesor} — ${a.nombreMateria} (${a.nombreGrupo})`}
      textoBusqueda={(a) => `${a.nombreProfesor} ${a.nombreMateria} ${a.nombreGrupo}`}
      aviso={profesores.length === 0 || grupos.length === 0 ? 'Necesitas al menos un profesor activo y un grupo.' : null}
      columnas={[
        { titulo: 'Profesor', render: (a) => <span className="font-medium text-tinta">{a.nombreProfesor}</span> },
        { titulo: 'Materia', campo: 'nombreMateria' },
        { titulo: 'Grupo', campo: 'nombreGrupo' },
      ]}
      Formulario={Formulario}
    />
  );
}
