import PaginaCrud from '../components/PaginaCrud';
import { getCarreras, createCarrera, updateCarrera, deleteCarrera } from '../services/carreramysqlServices';

const CAMPOS = [
  { nombre: 'idCarrera', etiqueta: 'Código', requerido: true, max: 20, soloAlCrear: true, placeholder: 'CAR001' },
  { nombre: 'nombreCarrera', etiqueta: 'Nombre de la carrera', requerido: true, max: 50 },
];

export default function Carreras() {
  return (
    <PaginaCrud
      titulo="Carreras"
      descripcion="Los programas que ofrece el instituto. Cada carrera se divide en niveles."
      entidad="Carrera"
      femenino
      cargar={getCarreras}
      crear={createCarrera}
      actualizar={updateCarrera}
      eliminar={deleteCarrera}
      idDe={(c) => c.idCarrera}
      nombreDe={(c) => c.nombreCarrera}
      textoBusqueda={(c) => `${c.idCarrera} ${c.nombreCarrera}`}
      columnas={[
        { titulo: 'Código', campo: 'idCarrera' },
        { titulo: 'Nombre', render: (c) => <span className="font-medium text-tinta">{c.nombreCarrera}</span> },
      ]}
      campos={CAMPOS}
    />
  );
}
