import { useAuth } from '../context/AuthContext';
import { ROLES } from '../config/roles';
import useCarga from './useCarga';
import { getAsignacionesByProfesor } from '../services/asignacionmysqlServices';
import { getGrupos } from '../services/grupomysqlServices';
import { getMaterias } from '../services/materiamysqlServices';

const unicos = (lista, clave) => [...new Map(lista.map((x) => [x[clave], x])).values()];

// Devuelve los grupos y materias con los que puede trabajar quien inició sesión:
//  - Coordinación y administración: todos.
//  - Profesor: solo los de sus asignaciones (el backend aplica la misma regla).
export default function useAlcance() {
  const { usuario } = useAuth();
  const esProfesor = usuario.rol === ROLES.PROFESOR;

  const { datos, cargando, error } = useCarga(
    async () => {
      if (esProfesor) {
        const asignaciones = await getAsignacionesByProfesor(usuario.idProfesor);
        return {
          asignaciones,
          grupos: unicos(asignaciones, 'idGrupo').map((a) => ({ idGrupo: a.idGrupo, nombreGrupo: a.nombreGrupo })),
          materias: unicos(asignaciones, 'idMateria').map((a) => ({ idMateria: a.idMateria, nombreMateria: a.nombreMateria })),
        };
      }
      const [grupos, materias] = await Promise.all([getGrupos(), getMaterias()]);
      return { asignaciones: null, grupos, materias };
    },
    [esProfesor, usuario.idProfesor],
    { asignaciones: null, grupos: [], materias: [] }
  );

  // Materias que se pueden calificar en un grupo
  const materiasDelGrupo = (idGrupo) =>
    esProfesor
      ? (datos.asignaciones ?? []).filter((a) => a.idGrupo === idGrupo).map((a) => ({ idMateria: a.idMateria, nombreMateria: a.nombreMateria }))
      : datos.materias;

  return { ...datos, esProfesor, cargando, error, materiasDelGrupo };
}
