import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Encabezado from '../../components/ui/Encabezado';
import Alerta from '../../components/ui/Alerta';
import Panel from '../../components/ui/Panel';
import PlanillaVista from '../../components/PlanillaVista';
import useAlcance from '../../hooks/useAlcance';
import { getPlanillaNotas } from '../../services/reportemysqlServices';
import { mensajeError } from '../../utils/errores';

// El profesor ve cada materia que dicta en cada grupo, y la planilla de notas del curso elegido
export default function MisCursos() {
  const { asignaciones, cargando, error } = useAlcance();
  const [elegida, setElegida] = useState(null);
  const [planilla, setPlanilla] = useState(null);
  const [errorPlanilla, setErrorPlanilla] = useState(null);

  const cursos = asignaciones ?? [];
  const actual = elegida ?? cursos[0] ?? null;

  useEffect(() => {
    if (!actual) return;
    let vigente = true;
    setPlanilla(null);
    getPlanillaNotas(actual.idGrupo, actual.idMateria)
      .then((p) => vigente && (setPlanilla(p), setErrorPlanilla(null)))
      .catch((err) => vigente && setErrorPlanilla(mensajeError(err)));
    return () => {
      vigente = false;
    };
  }, [actual?.idAsignacion]); // eslint-disable-line react-hooks/exhaustive-deps

  return (
    <>
      <Encabezado titulo="Mis cursos" descripcion="Las materias que dictas en cada grupo. Elige un curso para ver cómo va." />
      <Alerta tipo="error">{error}</Alerta>

      {cargando ? (
        <p className="text-gris">Cargando tus cursos…</p>
      ) : cursos.length === 0 ? (
        <Alerta tipo="aviso">Todavía no tienes cursos asignados. Coordinación es quien asigna los profesores a cada grupo.</Alerta>
      ) : (
        <div className="grid gap-6 lg:grid-cols-[280px_1fr]">
          <ul className="space-y-2" aria-label="Cursos">
            {cursos.map((c) => {
              const activo = c.idAsignacion === actual?.idAsignacion;
              return (
                <li key={c.idAsignacion}>
                  <button
                    onClick={() => setElegida(c)}
                    aria-current={activo}
                    className={`w-full rounded-xl border p-4 text-left transition-colors ${
                      activo ? 'border-andino bg-andino text-white' : 'border-linea bg-white hover:border-andino'
                    }`}
                  >
                    <span className={`block font-titulo font-semibold ${activo ? 'text-white' : 'text-tinta'}`}>{c.nombreMateria}</span>
                    <span className={`text-sm ${activo ? 'text-white/80' : 'text-gris'}`}>{c.nombreGrupo}</span>
                  </button>
                </li>
              );
            })}
          </ul>

          {actual && (
            <Panel
              titulo={`${actual.nombreMateria} — ${actual.nombreGrupo}`}
              descripcion={`Planilla del año ${new Date().getFullYear()}`}
              acciones={
                <div className="flex flex-wrap gap-2">
                  <Link to={`/app/notas?materia=${actual.idMateria}`} className="rounded-lg bg-andino px-3 py-1.5 text-sm font-semibold text-white hover:bg-andino-oscuro">
                    Calificar
                  </Link>
                  <Link to={`/app/asistencias?grupo=${actual.idGrupo}&tomar=1`} className="rounded-lg border border-linea px-3 py-1.5 text-sm font-semibold text-pizarra hover:bg-niebla">
                    Tomar asistencia
                  </Link>
                </div>
              }
            >
              <Alerta tipo="error">{errorPlanilla}</Alerta>
              {planilla ? <PlanillaVista planilla={planilla} /> : !errorPlanilla && <p className="text-sm text-gris">Cargando planilla…</p>}
            </Panel>
          )}
        </div>
      )}
    </>
  );
}
