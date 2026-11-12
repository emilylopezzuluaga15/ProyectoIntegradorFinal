import { useState } from 'react';
import Campo, { claseInput, AccionesFormulario } from '../ui/Campo';

// Asigna un profesor a una materia dentro de un grupo.
// Solo muestra las materias del plan de estudios del nivel al que pertenece el grupo.
export default function AsignacionForm({ inicial, profesores, grupos, planEstudios, onGuardar, onCancelar, guardando }) {
  const activos = profesores.filter((p) => p.activo);
  const [idProfesor, setIdProfesor] = useState(inicial?.idProfesor ?? activos[0]?.idProfesor ?? '');
  const [idGrupo, setIdGrupo] = useState(inicial?.idGrupo ?? grupos[0]?.idGrupo ?? '');

  const nivelDelGrupo = grupos.find((g) => g.idGrupo === idGrupo)?.idNivel;
  const materias = planEstudios.filter((pm) => pm.idNivel === nivelDelGrupo);
  const [idMateria, setIdMateria] = useState(inicial?.idMateria ?? materias[0]?.idMateria ?? '');

  const cambiarGrupo = (valor) => {
    setIdGrupo(valor);
    const nivel = grupos.find((g) => g.idGrupo === valor)?.idNivel;
    setIdMateria(planEstudios.find((pm) => pm.idNivel === nivel)?.idMateria ?? '');
  };

  const enviar = (e) => {
    e.preventDefault();
    onGuardar({ idProfesor, idGrupo, idMateria });
  };

  return (
    <form onSubmit={enviar} className="space-y-4">
      <Campo etiqueta="Profesor" id="asig-profesor" ayuda="Solo aparecen los profesores activos.">
        <select id="asig-profesor" value={idProfesor} onChange={(e) => setIdProfesor(e.target.value)} required className={claseInput}>
          {activos.map((p) => <option key={p.idProfesor} value={p.idProfesor}>{p.nombreProfesor}</option>)}
        </select>
      </Campo>

      <Campo etiqueta="Grupo" id="asig-grupo">
        <select id="asig-grupo" value={idGrupo} onChange={(e) => cambiarGrupo(e.target.value)} required className={claseInput}>
          {grupos.map((g) => <option key={g.idGrupo} value={g.idGrupo}>{g.nombreGrupo} — {g.nombreNivel}</option>)}
        </select>
      </Campo>

      <Campo
        etiqueta="Materia"
        id="asig-materia"
        ayuda={materias.length === 0 ? 'El nivel de este grupo no tiene materias en el plan de estudios.' : 'Materias del plan de estudios del nivel del grupo.'}
      >
        <select id="asig-materia" value={idMateria} onChange={(e) => setIdMateria(e.target.value)} required className={claseInput}>
          {materias.map((m) => <option key={m.idMateria} value={m.idMateria}>{m.nombreMateria}</option>)}
        </select>
      </Campo>

      <AccionesFormulario onCancelar={onCancelar} guardando={guardando} />
    </form>
  );
}
