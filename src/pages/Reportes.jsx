import { useState } from 'react';
import Encabezado from '../components/ui/Encabezado';
import Alerta from '../components/ui/Alerta';
import Boton from '../components/ui/Boton';
import Panel from '../components/ui/Panel';
import Campo, { claseInput } from '../components/ui/Campo';
import BoletinVista from '../components/BoletinVista';
import PlanillaVista, { RiesgoVista } from '../components/PlanillaVista';
import useCarga from '../hooks/useCarga';
import useAlcance from '../hooks/useAlcance';
import { getEstudiantesEnRiesgo, getPlanillaNotas, getBoletinEstudiante } from '../services/reportemysqlServices';
import { getEstudiantes } from '../services/estudiantemysqlServices';
import { getNivelMaterias } from '../services/nivelmateriamysqlServices';
import { mensajeError } from '../utils/errores';

const PESTANAS = [
  { id: 'riesgo', texto: 'Estudiantes en riesgo' },
  { id: 'planilla', texto: 'Planilla de notas' },
  { id: 'boletin', texto: 'Boletín de un estudiante' },
];

export default function Reportes() {
  const alcance = useAlcance();
  const { datos: estudiantes } = useCarga(getEstudiantes);
  const { datos: plan } = useCarga(() => (alcance.esProfesor ? Promise.resolve([]) : getNivelMaterias()), [alcance.esProfesor]);
  const anioActual = new Date().getFullYear();

  const [pestana, setPestana] = useState('riesgo');
  const [idGrupo, setIdGrupo] = useState('');
  const [idMateria, setIdMateria] = useState('');
  const [idEstudiante, setIdEstudiante] = useState('');
  const [anio, setAnio] = useState(anioActual);
  const [resultado, setResultado] = useState(null);
  const [error, setError] = useState(null);
  const [consultando, setConsultando] = useState(false);

  const grupoElegido = idGrupo || alcance.grupos[0]?.idGrupo || '';
  // Materias que se pueden consultar en la planilla del grupo elegido
  const materias = alcance.esProfesor
    ? alcance.materiasDelGrupo(grupoElegido)
    : plan
        .filter((pm) => pm.idNivel === alcance.grupos.find((g) => g.idGrupo === grupoElegido)?.idNivel)
        .map((pm) => ({ idMateria: pm.idMateria, nombreMateria: pm.nombreMateria }));
  const materiaElegida = materias.some((m) => m.idMateria === idMateria) ? idMateria : materias[0]?.idMateria || '';
  const estudianteElegido = idEstudiante || estudiantes[0]?.idEstudiante || '';

  const cambiarPestana = (id) => {
    setPestana(id);
    setResultado(null);
    setError(null);
  };

  const consultar = async (e) => {
    e.preventDefault();
    setConsultando(true);
    try {
      if (pestana === 'riesgo') setResultado({ tipo: 'riesgo', datos: await getEstudiantesEnRiesgo(grupoElegido, anio) });
      if (pestana === 'planilla') setResultado({ tipo: 'planilla', datos: await getPlanillaNotas(grupoElegido, materiaElegida, anio) });
      if (pestana === 'boletin') setResultado({ tipo: 'boletin', datos: await getBoletinEstudiante(estudianteElegido) });
      setError(null);
    } catch (err) {
      setResultado(null);
      setError(mensajeError(err));
    } finally {
      setConsultando(false);
    }
  };

  return (
    <>
      <Encabezado
        titulo="Reportes"
        descripcion={alcance.esProfesor ? 'Consulta el avance de los grupos donde dictas clase.' : 'Consulta el avance académico por grupo o por estudiante.'}
      />

      <div role="tablist" aria-label="Tipo de reporte" className="no-imprimir mb-5 flex flex-wrap gap-1 rounded-xl bg-white p-1 ring-1 ring-linea sm:inline-flex">
        {PESTANAS.map((p) => (
          <button
            key={p.id}
            role="tab"
            aria-selected={pestana === p.id}
            onClick={() => cambiarPestana(p.id)}
            className={`rounded-lg px-4 py-2 text-sm font-medium ${pestana === p.id ? 'bg-tinta text-white' : 'text-gris hover:text-tinta'}`}
          >
            {p.texto}
          </button>
        ))}
      </div>

      <Panel className="no-imprimir mb-6">
        <form onSubmit={consultar} className="flex flex-wrap items-end gap-4">
          {pestana !== 'boletin' && (
            <div className="w-full sm:w-60">
              <Campo etiqueta="Grupo" id="r-grupo">
                <select id="r-grupo" value={grupoElegido} onChange={(e) => setIdGrupo(e.target.value)} required className={claseInput}>
                  {alcance.grupos.map((g) => <option key={g.idGrupo} value={g.idGrupo}>{g.nombreGrupo}</option>)}
                </select>
              </Campo>
            </div>
          )}
          {pestana === 'planilla' && (
            <div className="w-full sm:w-60">
              <Campo etiqueta="Materia" id="r-materia">
                <select id="r-materia" value={materiaElegida} onChange={(e) => setIdMateria(e.target.value)} required className={claseInput}>
                  {materias.map((m) => <option key={m.idMateria} value={m.idMateria}>{m.nombreMateria}</option>)}
                </select>
              </Campo>
            </div>
          )}
          {pestana !== 'boletin' && (
            <div className="w-28">
              <Campo etiqueta="Año" id="r-anio">
                <input id="r-anio" type="number" value={anio} min={2000} max={anioActual + 1} onChange={(e) => setAnio(Number(e.target.value))} className={claseInput} />
              </Campo>
            </div>
          )}
          {pestana === 'boletin' && (
            <div className="w-full sm:w-80">
              <Campo etiqueta="Estudiante" id="r-estudiante">
                <select id="r-estudiante" value={estudianteElegido} onChange={(e) => setIdEstudiante(e.target.value)} required className={claseInput}>
                  {estudiantes.map((e) => <option key={e.idEstudiante} value={e.idEstudiante}>{e.nombreEstudiante} ({e.idEstudiante})</option>)}
                </select>
              </Campo>
            </div>
          )}
          <Boton type="submit" disabled={consultando}>{consultando ? 'Consultando…' : 'Consultar'}</Boton>
          {resultado?.tipo === 'boletin' && <Boton variante="secundario" onClick={() => window.print()}>Imprimir</Boton>}
        </form>
      </Panel>

      <Alerta tipo="error" onCerrar={() => setError(null)}>{error}</Alerta>

      {resultado?.tipo === 'riesgo' && <Panel titulo="Estudiantes en riesgo"><RiesgoVista reporte={resultado.datos} /></Panel>}
      {resultado?.tipo === 'planilla' && (
        <Panel titulo={`${resultado.datos.nombreMateria} — ${resultado.datos.nombreGrupo}`}><PlanillaVista planilla={resultado.datos} /></Panel>
      )}
      {resultado?.tipo === 'boletin' && <BoletinVista boletin={resultado.datos} />}
    </>
  );
}
