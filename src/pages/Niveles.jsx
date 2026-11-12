import PaginaCrud from '../components/PaginaCrud';
import useCarga from '../hooks/useCarga';
import { getNiveles, createNivel, updateNivel, deleteNivel } from '../services/nivelmysqlServices';
import { getCarreras } from '../services/carreramysqlServices';

export default function Niveles() {
  const { datos: carreras } = useCarga(getCarreras);

  const campos = [
    { nombre: 'idNivel', etiqueta: 'Código', requerido: true, max: 20, soloAlCrear: true, placeholder: 'NIV001' },
    { nombre: 'nombreNivel', etiqueta: 'Nombre del nivel', requerido: true, max: 50, placeholder: 'Primer semestre' },
    {
      nombre: 'idCarrera',
      etiqueta: 'Carrera',
      tipo: 'select',
      requerido: true,
      anchoCompleto: true,
      opciones: carreras.map((c) => ({ valor: c.idCarrera, texto: c.nombreCarrera })),
    },
  ];

  return (
    <PaginaCrud
      titulo="Niveles"
      descripcion="Los semestres o etapas de cada carrera."
      entidad="Nivel"
      cargar={getNiveles}
      crear={createNivel}
      actualizar={updateNivel}
      eliminar={deleteNivel}
      idDe={(n) => n.idNivel}
      nombreDe={(n) => n.nombreNivel}
      textoBusqueda={(n) => `${n.idNivel} ${n.nombreNivel} ${n.nombreCarrera}`}
      aviso={carreras.length === 0 ? 'Primero registra al menos una carrera para poder crear niveles.' : null}
      columnas={[
        { titulo: 'Código', campo: 'idNivel' },
        { titulo: 'Nivel', render: (n) => <span className="font-medium text-tinta">{n.nombreNivel}</span> },
        { titulo: 'Carrera', campo: 'nombreCarrera' },
      ]}
      campos={campos}
    />
  );
}
