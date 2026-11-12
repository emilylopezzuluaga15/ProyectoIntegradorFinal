import { useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import Encabezado from '../components/ui/Encabezado';
import Alerta from '../components/ui/Alerta';
import Boton from '../components/ui/Boton';
import Tabla from '../components/ui/Tabla';
import Modal from '../components/ui/Modal';
import Confirmar from '../components/ui/Confirmar';
import Etiqueta from '../components/ui/Etiqueta';
import { estadoDeNota, tonoEstadoNota } from '../utils/estados';
import { claseInput } from '../components/ui/Campo';
import NotaForm from '../components/forms/NotaForm';
import CalificacionesNota from '../components/CalificacionesNota';
import useCarga from '../hooks/useCarga';
import useAlcance from '../hooks/useAlcance';
import { getNotas, createNota, deleteNota } from '../services/notamysqlServices';
import { mensajeError, formatoNota } from '../utils/errores';

// Calificaciones. Coordinación ve todas; el profesor solo las de las materias que dicta.
export default function Notas() {
  const alcance = useAlcance();
  const { datos: notas, cargando, error, setError, recargar } = useCarga(getNotas);
  const [params] = useSearchParams();
  const [materia, setMateria] = useState(params.get('materia') ?? '');
  const [busqueda, setBusqueda] = useState('');
  const [formAbierto, setFormAbierto] = useState(false);
  const [notaAbierta, setNotaAbierta] = useState(null);
  const [porEliminar, setPorEliminar] = useState(null);
  const [guardando, setGuardando] = useState(false);

  const idsMaterias = new Set(alcance.materias.map((m) => m.idMateria));
  const visibles = notas
    .filter((n) => !alcance.esProfesor || idsMaterias.has(n.idMateria))
    .filter((n) => !materia || n.idMateria === materia)
    .filter((n) => `${n.nombreEstudiante} ${n.idEstudiante}`.toLowerCase().includes(busqueda.toLowerCase()));

  const guardar = async (valores) => {
    setGuardando(true);
    try {
      const nueva = await createNota(valores);
      setFormAbierto(false);
      await recargar();
      setNotaAbierta(nueva);
    } catch (err) {
      setError(mensajeError(err));
      setFormAbierto(false);
    } finally {
      setGuardando(false);
    }
  };

  const eliminar = async () => {
    const nota = porEliminar;
    setPorEliminar(null);
    try {
      await deleteNota(nota.idNota);
      await recargar();
    } catch (err) {
      setError(mensajeError(err));
    }
  };

  return (
    <>
      <Encabezado
        titulo="Calificaciones"
        descripcion={alcance.esProfesor ? 'Notas de las materias que dictas. Abre una nota para agregar calificaciones parciales.' : 'Notas de todos los estudiantes por materia.'}
      >
        <Boton onClick={() => setFormAbierto(true)} disabled={alcance.grupos.length === 0}>Abrir nota</Boton>
      </Encabezado>

      <Alerta tipo="error" onCerrar={() => setError(null)}>{error || alcance.error}</Alerta>
      {!alcance.cargando && alcance.grupos.length === 0 && (
        <Alerta tipo="aviso">{alcance.esProfesor ? 'Todavía no tienes materias asignadas. Pídele a coordinación que te asigne.' : 'Primero crea grupos y materias.'}</Alerta>
      )}

      <div className="mb-4 flex flex-wrap gap-3">
        <div className="w-full sm:w-64">
          <label htmlFor="filtro-materia" className="sr-only">Materia</label>
          <select id="filtro-materia" value={materia} onChange={(e) => setMateria(e.target.value)} className={claseInput}>
            <option value="">Todas las materias</option>
            {alcance.materias.map((m) => <option key={m.idMateria} value={m.idMateria}>{m.nombreMateria}</option>)}
          </select>
        </div>
        <div className="w-full sm:w-72">
          <label htmlFor="buscar-nota" className="sr-only">Buscar estudiante</label>
          <input id="buscar-nota" type="search" value={busqueda} onChange={(e) => setBusqueda(e.target.value)} placeholder="Buscar estudiante…" className={claseInput} />
        </div>
      </div>

      <Tabla
        cargando={cargando || alcance.cargando}
        filas={visibles}
        claveFila={(n) => n.idNota}
        vacio="No hay notas con estos filtros."
        columnas={[
          { titulo: 'Estudiante', render: (n) => <span className="font-medium text-tinta">{n.nombreEstudiante}</span> },
          { titulo: 'Materia', campo: 'nombreMateria' },
          { titulo: 'Definitiva', render: (n) => <span className="font-semibold">{formatoNota(n.definitiva)}</span> },
          { titulo: 'Estado', render: (n) => { const e = estadoDeNota(n.aprobada); return <Etiqueta tono={tonoEstadoNota(e)}>{e}</Etiqueta>; } },
          { titulo: 'Fecha', campo: 'fechaRegistro' },
        ]}
        acciones={(n) => (
          <div className="flex justify-end gap-1">
            <Boton variante="texto" tamano="pequeno" onClick={() => setNotaAbierta(n)}>Calificar</Boton>
            <Boton variante="textoPeligro" tamano="pequeno" onClick={() => setPorEliminar(n)}>Eliminar</Boton>
          </div>
        )}
      />

      <Modal abierto={formAbierto} titulo="Abrir nota" onCerrar={() => setFormAbierto(false)}>
        {formAbierto && (
          <NotaForm grupos={alcance.grupos} materiasDelGrupo={alcance.materiasDelGrupo} onGuardar={guardar} onCancelar={() => setFormAbierto(false)} guardando={guardando} />
        )}
      </Modal>

      <Modal abierto={Boolean(notaAbierta)} titulo={notaAbierta ? `${notaAbierta.nombreEstudiante} — ${notaAbierta.nombreMateria}` : ''} onCerrar={() => setNotaAbierta(null)}>
        {notaAbierta && <CalificacionesNota nota={notaAbierta} onCambio={recargar} />}
      </Modal>

      <Confirmar
        abierto={Boolean(porEliminar)}
        titulo="Eliminar nota"
        mensaje={porEliminar ? `¿Eliminar la nota de ${porEliminar.nombreEstudiante} en ${porEliminar.nombreMateria}? Solo se puede si no tiene calificaciones parciales.` : ''}
        onConfirmar={eliminar}
        onCancelar={() => setPorEliminar(null)}
      />
    </>
  );
}
