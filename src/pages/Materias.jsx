import PaginaCrud from '../components/PaginaCrud';
import { getMaterias, createMateria, updateMateria, deleteMateria } from '../services/materiamysqlServices';

const CAMPOS = [
  { nombre: 'idMateria', etiqueta: 'Código', requerido: true, max: 20, soloAlCrear: true, placeholder: 'MAT001' },
  { nombre: 'nombreMateria', etiqueta: 'Nombre de la materia', requerido: true, max: 50 },
];

export default function Materias() {
  return (
    <PaginaCrud
      titulo="Materias"
      descripcion="Todas las asignaturas. Para decir en qué nivel se ven, usa el plan de estudios."
      entidad="Materia"
      femenino
      cargar={getMaterias}
      crear={createMateria}
      actualizar={updateMateria}
      eliminar={deleteMateria}
      idDe={(m) => m.idMateria}
      nombreDe={(m) => m.nombreMateria}
      textoBusqueda={(m) => `${m.idMateria} ${m.nombreMateria}`}
      columnas={[
        { titulo: 'Código', campo: 'idMateria' },
        { titulo: 'Nombre', render: (m) => <span className="font-medium text-tinta">{m.nombreMateria}</span> },
      ]}
      campos={CAMPOS}
    />
  );
}
