import { useEffect, useState } from 'react';
import Campo, { claseInput, AccionesFormulario } from '../ui/Campo';
import { getInscripcionesByGrupo } from '../../services/inscripcionmysqlServices';
import { hoy } from '../../utils/errores';

// Abre el registro de nota de un estudiante en una materia.
// Primero se elige el grupo, y así solo aparecen los estudiantes inscritos y las materias que se pueden calificar.
export default function NotaForm({ grupos, materiasDelGrupo, onGuardar, onCancelar, guardando }) {
  const [idGrupo, setIdGrupo] = useState(grupos[0]?.idGrupo ?? '');
  const [inscritos, setInscritos] = useState([]);
  const [idEstudiante, setIdEstudiante] = useState('');
  const [idMateria, setIdMateria] = useState(materiasDelGrupo(grupos[0]?.idGrupo)[0]?.idMateria ?? '');
  const [fechaRegistro, setFechaRegistro] = useState(hoy());

  const materias = materiasDelGrupo(idGrupo);

  useEffect(() => {
    if (!idGrupo) return;
    let vigente = true;
    getInscripcionesByGrupo(idGrupo)
      .then((lista) => {
        if (!vigente) return;
        const unicos = [...new Map(lista.map((i) => [i.idEstudiante, i])).values()];
        setInscritos(unicos);
        setIdEstudiante(unicos[0]?.idEstudiante ?? '');
      })
      .catch(() => vigente && setInscritos([]));
    return () => {
      vigente = false;
    };
  }, [idGrupo]);

  const cambiarGrupo = (valor) => {
    setIdGrupo(valor);
    setIdMateria(materiasDelGrupo(valor)[0]?.idMateria ?? '');
  };

  const enviar = (e) => {
    e.preventDefault();
    onGuardar({ idEstudiante, idMateria, fechaRegistro, definitiva: null });
  };

  return (
    <form onSubmit={enviar} className="space-y-4">
      <p className="text-sm text-gris">
        La nota queda "en curso". La definitiva se calcula sola con el promedio de las calificaciones parciales que agregues.
      </p>

      <Campo etiqueta="Grupo" id="nota-grupo">
        <select id="nota-grupo" value={idGrupo} onChange={(e) => cambiarGrupo(e.target.value)} required className={claseInput}>
          {grupos.map((g) => <option key={g.idGrupo} value={g.idGrupo}>{g.nombreGrupo}</option>)}
        </select>
      </Campo>

      <Campo etiqueta="Estudiante" id="nota-estudiante" ayuda={inscritos.length === 0 ? 'Este grupo todavía no tiene estudiantes inscritos.' : null}>
        <select id="nota-estudiante" value={idEstudiante} onChange={(e) => setIdEstudiante(e.target.value)} required className={claseInput}>
          {inscritos.map((i) => <option key={i.idEstudiante} value={i.idEstudiante}>{i.nombreEstudiante}</option>)}
        </select>
      </Campo>

      <Campo etiqueta="Materia" id="nota-materia">
        <select id="nota-materia" value={idMateria} onChange={(e) => setIdMateria(e.target.value)} required className={claseInput}>
          {materias.map((m) => <option key={m.idMateria} value={m.idMateria}>{m.nombreMateria}</option>)}
        </select>
      </Campo>

      <Campo etiqueta="Fecha" id="nota-fecha">
        <input id="nota-fecha" type="date" value={fechaRegistro} max={hoy()} onChange={(e) => setFechaRegistro(e.target.value)} required className={claseInput} />
      </Campo>

      <AccionesFormulario onCancelar={onCancelar} guardando={guardando} textoGuardar="Abrir nota" />
    </form>
  );
}
