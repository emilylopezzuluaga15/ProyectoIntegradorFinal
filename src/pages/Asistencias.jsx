import { useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import Encabezado from '../components/ui/Encabezado';
import Alerta from '../components/ui/Alerta';
import Boton from '../components/ui/Boton';
import Tabla from '../components/ui/Tabla';
import Modal from '../components/ui/Modal';
import Confirmar from '../components/ui/Confirmar';
import Etiqueta from '../components/ui/Etiqueta';
import { claseInput } from '../components/ui/Campo';
import TomarAsistencia from '../components/forms/TomarAsistencia';
import useCarga from '../hooks/useCarga';
import useAlcance from '../hooks/useAlcance';
import { getAsistencias, deleteAsistencia } from '../services/asistenciamysqlServices';
import { mensajeError } from '../utils/errores';

export default function Asistencias() {
  const alcance = useAlcance();
  const { datos: registros, cargando, error, setError, recargar } = useCarga(getAsistencias);
  const [params] = useSearchParams();
  const [grupo, setGrupo] = useState(params.get('grupo') ?? '');
  const [fecha, setFecha] = useState('');
  const [tomando, setTomando] = useState(params.get('tomar') === '1');
  const [porEliminar, setPorEliminar] = useState(null);
  const [exito, setExito] = useState(null);

  const idsGrupos = new Set(alcance.grupos.map((g) => g.idGrupo));
  const visibles = registros
    .filter((r) => !alcance.esProfesor || idsGrupos.has(r.idGrupo))
    .filter((r) => !grupo || r.idGrupo === grupo)
    .filter((r) => !fecha || r.fechaAsistencia === fecha)
    .sort((a, b) => b.fechaAsistencia.localeCompare(a.fechaAsistencia));

  const terminar = async (mensaje) => {
    setTomando(false);
    setExito(mensaje);
    await recargar();
  };

  const eliminar = async () => {
    const r = porEliminar;
    setPorEliminar(null);
    try {
      await deleteAsistencia(r.idAsistencia);
      await recargar();
    } catch (err) {
      setError(mensajeError(err));
    }
  };

  return (
    <>
      <Encabezado
        titulo="Asistencia"
        descripcion={alcance.esProfesor ? 'Registro de asistencia de tus grupos.' : 'Registro de asistencia de todos los grupos.'}
      >
        <Boton onClick={() => setTomando(true)} disabled={alcance.grupos.length === 0}>Tomar asistencia</Boton>
      </Encabezado>

      <Alerta tipo="error" onCerrar={() => setError(null)}>{error || alcance.error}</Alerta>
      <Alerta tipo="exito" onCerrar={() => setExito(null)}>{exito}</Alerta>

      <div className="mb-4 flex flex-wrap gap-3">
        <div className="w-full sm:w-64">
          <label htmlFor="f-grupo" className="sr-only">Grupo</label>
          <select id="f-grupo" value={grupo} onChange={(e) => setGrupo(e.target.value)} className={claseInput}>
            <option value="">Todos los grupos</option>
            {alcance.grupos.map((g) => <option key={g.idGrupo} value={g.idGrupo}>{g.nombreGrupo}</option>)}
          </select>
        </div>
        <div className="w-full sm:w-48">
          <label htmlFor="f-fecha" className="sr-only">Fecha</label>
          <input id="f-fecha" type="date" value={fecha} onChange={(e) => setFecha(e.target.value)} className={claseInput} />
        </div>
        {(grupo || fecha) && <Boton variante="texto" onClick={() => { setGrupo(''); setFecha(''); }}>Quitar filtros</Boton>}
      </div>

      <Tabla
        cargando={cargando || alcance.cargando}
        filas={visibles}
        claveFila={(r) => r.idAsistencia}
        vacio="No hay registros de asistencia con estos filtros."
        columnas={[
          { titulo: 'Fecha', campo: 'fechaAsistencia' },
          { titulo: 'Estudiante', render: (r) => <span className="font-medium text-tinta">{r.nombreEstudiante}</span> },
          { titulo: 'Grupo', campo: 'nombreGrupo' },
          {
            titulo: 'Asistencia',
            render: (r) => r.asistio
              ? <Etiqueta tono="verde">Presente</Etiqueta>
              : <Etiqueta tono={r.justificada ? 'dorado' : 'rojo'}>{r.justificada ? 'Falta justificada' : 'Faltó'}</Etiqueta>,
          },
          { titulo: 'Observación', campo: 'observacion' },
        ]}
        acciones={(r) => <Boton variante="textoPeligro" tamano="pequeno" onClick={() => setPorEliminar(r)}>Eliminar</Boton>}
      />

      <Modal abierto={tomando && alcance.grupos.length > 0} titulo="Tomar asistencia" onCerrar={() => setTomando(false)} ancho="max-w-2xl">
        {tomando && alcance.grupos.length > 0 && (
          <TomarAsistencia grupos={alcance.grupos} grupoInicial={grupo} onTerminar={terminar} onCancelar={() => setTomando(false)} />
        )}
      </Modal>

      <Confirmar
        abierto={Boolean(porEliminar)}
        titulo="Eliminar registro"
        mensaje={porEliminar ? `¿Eliminar la asistencia de ${porEliminar.nombreEstudiante} del ${porEliminar.fechaAsistencia}?` : ''}
        onConfirmar={eliminar}
        onCancelar={() => setPorEliminar(null)}
      />
    </>
  );
}
