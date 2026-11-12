import { useState } from 'react';
import PaginaCrud from '../components/PaginaCrud';
import Boton from '../components/ui/Boton';
import Modal from '../components/ui/Modal';
import useCarga from '../hooks/useCarga';
import { getGrupos, createGrupo, updateGrupo, deleteGrupo, getCupoGrupo } from '../services/grupomysqlServices';
import { getNiveles } from '../services/nivelmysqlServices';
import { mensajeError } from '../utils/errores';

export default function Grupos() {
  const { datos: niveles } = useCarga(getNiveles);
  const [cupo, setCupo] = useState(null);

  const verCupo = async (grupo) => {
    try {
      setCupo({ ...(await getCupoGrupo(grupo.idGrupo)), nombreGrupo: grupo.nombreGrupo });
    } catch (err) {
      setCupo({ error: mensajeError(err), nombreGrupo: grupo.nombreGrupo });
    }
  };

  const campos = [
    { nombre: 'idGrupo', etiqueta: 'Código', requerido: true, max: 20, soloAlCrear: true, placeholder: 'GRP001' },
    { nombre: 'nombreGrupo', etiqueta: 'Nombre del grupo', requerido: true, max: 50, placeholder: 'Sistemas 1A' },
    {
      nombre: 'idNivel',
      etiqueta: 'Nivel',
      tipo: 'select',
      requerido: true,
      opciones: niveles.map((n) => ({ valor: n.idNivel, texto: `${n.nombreNivel} — ${n.nombreCarrera}` })),
    },
    { nombre: 'cupoMaximo', etiqueta: 'Cupo máximo', tipo: 'number', min: 1, ayuda: 'Déjalo vacío si el grupo no tiene límite.' },
  ];

  return (
    <PaginaCrud
      titulo="Grupos"
      descripcion="Cada grupo pertenece a un nivel. Los estudiantes se inscriben a grupos."
      entidad="Grupo"
      cargar={getGrupos}
      crear={createGrupo}
      actualizar={updateGrupo}
      eliminar={deleteGrupo}
      idDe={(g) => g.idGrupo}
      nombreDe={(g) => g.nombreGrupo}
      textoBusqueda={(g) => `${g.idGrupo} ${g.nombreGrupo} ${g.nombreNivel}`}
      aviso={niveles.length === 0 ? 'Primero registra al menos un nivel para poder crear grupos.' : null}
      columnas={[
        { titulo: 'Código', campo: 'idGrupo' },
        { titulo: 'Grupo', render: (g) => <span className="font-medium text-tinta">{g.nombreGrupo}</span> },
        { titulo: 'Nivel', campo: 'nombreNivel' },
        { titulo: 'Cupo máximo', render: (g) => g.cupoMaximo ?? 'Sin límite' },
      ]}
      campos={campos}
      accionesExtra={(g) => <Boton variante="texto" tamano="pequeno" onClick={() => verCupo(g)}>Ver cupo</Boton>}
    >
      <Modal abierto={Boolean(cupo)} titulo={`Cupo de ${cupo?.nombreGrupo ?? ''}`} onCerrar={() => setCupo(null)} ancho="max-w-sm">
        {cupo?.error ? (
          <p className="text-sm text-red-800">{cupo.error}</p>
        ) : cupo && (
          <dl className="grid grid-cols-2 gap-4 text-sm">
            <div><dt className="text-gris">Año</dt><dd className="text-lg font-semibold">{cupo.anio}</dd></div>
            <div><dt className="text-gris">Cupo máximo</dt><dd className="text-lg font-semibold">{cupo.cupoMaximo ?? 'Sin límite'}</dd></div>
            <div><dt className="text-gris">Inscritos</dt><dd className="text-lg font-semibold">{cupo.inscritos}</dd></div>
            <div><dt className="text-gris">Disponibles</dt><dd className="text-lg font-semibold text-andino">{cupo.disponibles ?? 'Sin límite'}</dd></div>
          </dl>
        )}
      </Modal>
    </PaginaCrud>
  );
}
