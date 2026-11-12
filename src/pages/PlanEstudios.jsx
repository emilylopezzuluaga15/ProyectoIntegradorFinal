import PaginaCrud from '../components/PaginaCrud';
import useCarga from '../hooks/useCarga';
import { getNivelMaterias, createNivelMateria, deleteNivelMateria } from '../services/nivelmateriamysqlServices';
import { getNiveles } from '../services/nivelmysqlServices';
import { getMaterias } from '../services/materiamysqlServices';

export default function PlanEstudios() {
  const { datos: niveles } = useCarga(getNiveles);
  const { datos: materias } = useCarga(getMaterias);

  const campos = [
    {
      nombre: 'idNivel',
      etiqueta: 'Nivel',
      tipo: 'select',
      requerido: true,
      anchoCompleto: true,
      opciones: niveles.map((n) => ({ valor: n.idNivel, texto: `${n.nombreNivel} — ${n.nombreCarrera}` })),
    },
    {
      nombre: 'idMateria',
      etiqueta: 'Materia',
      tipo: 'select',
      requerido: true,
      anchoCompleto: true,
      opciones: materias.map((m) => ({ valor: m.idMateria, texto: m.nombreMateria })),
    },
  ];

  return (
    <PaginaCrud
      titulo="Plan de estudios"
      descripcion="Define qué materias se ven en cada nivel. Un profesor solo se puede asignar a materias del nivel del grupo."
      entidad="Materia al plan"
      femenino
      cargar={getNivelMaterias}
      crear={createNivelMateria}
      eliminar={deleteNivelMateria}
      idDe={(nm) => nm.idNivelMateria}
      nombreDe={(nm) => `${nm.nombreMateria} en ${nm.nombreNivel}`}
      textoBusqueda={(nm) => `${nm.nombreNivel} ${nm.nombreMateria}`}
      aviso={niveles.length === 0 || materias.length === 0 ? 'Necesitas al menos un nivel y una materia registrados.' : null}
      columnas={[
        { titulo: 'Nivel', render: (nm) => <span className="font-medium text-tinta">{nm.nombreNivel}</span> },
        { titulo: 'Materia', campo: 'nombreMateria' },
        { titulo: 'Código materia', campo: 'idMateria' },
      ]}
      campos={campos}
    />
  );
}
