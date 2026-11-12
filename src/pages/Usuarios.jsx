import PaginaCrud from '../components/PaginaCrud';
import UsuarioForm from '../components/forms/UsuarioForm';
import Etiqueta from '../components/ui/Etiqueta';
import useCarga from '../hooks/useCarga';
import { useAuth } from '../context/AuthContext';
import { NOMBRE_ROL } from '../config/roles';
import { getUsuarios, createUsuario, updateUsuario, deleteUsuario } from '../services/usuarioServices';
import { getEstudiantes } from '../services/estudiantemysqlServices';
import { getProfesores } from '../services/profesormysqlServices';

const TONO_ROL = { ADMINISTRATIVO: 'azul', COORDINADOR: 'dorado', PROFESOR: 'verde', ESTUDIANTE: 'gris' };

// Solo el rol ADMINISTRATIVO entra aquí
export default function Usuarios() {
  const { usuario: sesion } = useAuth();
  const { datos: estudiantes } = useCarga(getEstudiantes);
  const { datos: profesores } = useCarga(getProfesores);
  const { datos: usuarios } = useCarga(getUsuarios);

  const nombreVinculado = (u) =>
    estudiantes.find((e) => e.idEstudiante === u.idEstudiante)?.nombreEstudiante ??
    profesores.find((p) => p.idProfesor === u.idProfesor)?.nombreProfesor ??
    '—';

  const Formulario = (props) => (
    <UsuarioForm {...props} estudiantes={estudiantes} profesores={profesores} usuarios={usuarios} />
  );

  return (
    <PaginaCrud
      titulo="Usuarios"
      descripcion="Cuentas para ingresar a la plataforma. Cada estudiante o profesor necesita su propia cuenta."
      entidad="Usuario"
      cargar={getUsuarios}
      crear={createUsuario}
      actualizar={updateUsuario}
      eliminar={deleteUsuario}
      idDe={(u) => u.idUsuario}
      nombreDe={(u) => u.username}
      textoBusqueda={(u) => `${u.username} ${NOMBRE_ROL[u.rol]} ${nombreVinculado(u)}`}
      columnas={[
        {
          titulo: 'Usuario',
          render: (u) => (
            <span className="font-medium text-tinta">
              {u.username}
              {u.idUsuario === sesion.idUsuario && <span className="ml-2 text-xs font-normal text-gris">(tú)</span>}
            </span>
          ),
        },
        { titulo: 'Rol', render: (u) => <Etiqueta tono={TONO_ROL[u.rol]}>{NOMBRE_ROL[u.rol]}</Etiqueta> },
        { titulo: 'Persona vinculada', render: nombreVinculado },
        { titulo: 'Estado', render: (u) => <Etiqueta tono={u.activo ? 'verde' : 'rojo'}>{u.activo ? 'Activa' : 'Bloqueada'}</Etiqueta> },
      ]}
      Formulario={Formulario}
    />
  );
}
