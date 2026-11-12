import { useState } from 'react';
import Campo, { claseInput, AccionesFormulario } from '../ui/Campo';
import { NOMBRE_ROL, ROLES } from '../../config/roles';

// Crea o edita una cuenta de acceso.
// Un ESTUDIANTE se vincula a su registro de estudiante y un PROFESOR a su registro de profesor.
export default function UsuarioForm({ inicial, editando, estudiantes, profesores, usuarios, onGuardar, onCancelar, guardando }) {
  const [form, setForm] = useState({
    username: inicial?.username ?? '',
    password: '',
    rol: inicial?.rol ?? ROLES.ESTUDIANTE,
    activo: inicial?.activo ?? true,
    idEstudiante: inicial?.idEstudiante ?? '',
    idProfesor: inicial?.idProfesor ?? '',
  });

  // Personas que todavía no tienen cuenta (o la que ya tiene este usuario)
  const estudiantesLibres = estudiantes.filter(
    (e) => e.idEstudiante === inicial?.idEstudiante || !usuarios.some((u) => u.idEstudiante === e.idEstudiante)
  );
  const profesoresLibres = profesores.filter(
    (p) => p.idProfesor === inicial?.idProfesor || !usuarios.some((u) => u.idProfesor === p.idProfesor)
  );

  const cambiar = (campo, valor) => setForm((prev) => ({ ...prev, [campo]: valor }));

  const cambiarRol = (rol) => {
    setForm((prev) => ({
      ...prev,
      rol,
      idEstudiante: rol === ROLES.ESTUDIANTE ? prev.idEstudiante || estudiantesLibres[0]?.idEstudiante || '' : '',
      idProfesor: rol === ROLES.PROFESOR ? prev.idProfesor || profesoresLibres[0]?.idProfesor || '' : '',
    }));
  };

  const enviar = (e) => {
    e.preventDefault();
    onGuardar({
      username: form.username.trim(),
      password: form.password === '' ? null : form.password,
      rol: form.rol,
      activo: form.activo,
      idEstudiante: form.rol === ROLES.ESTUDIANTE ? form.idEstudiante || null : null,
      idProfesor: form.rol === ROLES.PROFESOR ? form.idProfesor || null : null,
    });
  };

  return (
    <form onSubmit={enviar} className="space-y-4">
      <div className="grid gap-4 sm:grid-cols-2">
        <Campo etiqueta="Usuario" id="u-username">
          <input id="u-username" value={form.username} onChange={(e) => cambiar('username', e.target.value)} required maxLength={50} autoComplete="off" className={claseInput} />
        </Campo>
        <Campo etiqueta={editando ? 'Nueva contraseña (opcional)' : 'Contraseña'} id="u-password" ayuda={editando ? 'Déjala vacía para no cambiarla.' : 'Mínimo 8 caracteres.'}>
          <input id="u-password" type="password" value={form.password} onChange={(e) => cambiar('password', e.target.value)} required={!editando} minLength={8} maxLength={72} autoComplete="new-password" className={claseInput} />
        </Campo>
      </div>

      <Campo etiqueta="Rol" id="u-rol">
        <select id="u-rol" value={form.rol} onChange={(e) => cambiarRol(e.target.value)} className={claseInput}>
          {Object.values(ROLES).map((r) => <option key={r} value={r}>{NOMBRE_ROL[r]}</option>)}
        </select>
      </Campo>

      {form.rol === ROLES.ESTUDIANTE && (
        <Campo etiqueta="Estudiante vinculado" id="u-estudiante" ayuda={estudiantesLibres.length === 0 ? 'Todos los estudiantes ya tienen cuenta.' : 'Solo aparecen estudiantes sin cuenta.'}>
          <select id="u-estudiante" value={form.idEstudiante} onChange={(e) => cambiar('idEstudiante', e.target.value)} required className={claseInput}>
            <option value="" disabled>Elige un estudiante</option>
            {estudiantesLibres.map((e) => <option key={e.idEstudiante} value={e.idEstudiante}>{e.nombreEstudiante} ({e.idEstudiante})</option>)}
          </select>
        </Campo>
      )}

      {form.rol === ROLES.PROFESOR && (
        <Campo etiqueta="Profesor vinculado" id="u-profesor" ayuda={profesoresLibres.length === 0 ? 'Todos los profesores ya tienen cuenta.' : 'Solo aparecen profesores sin cuenta.'}>
          <select id="u-profesor" value={form.idProfesor} onChange={(e) => cambiar('idProfesor', e.target.value)} required className={claseInput}>
            <option value="" disabled>Elige un profesor</option>
            {profesoresLibres.map((p) => <option key={p.idProfesor} value={p.idProfesor}>{p.nombreProfesor} ({p.idProfesor})</option>)}
          </select>
        </Campo>
      )}

      <label className="flex items-center gap-2 text-sm font-medium">
        <input type="checkbox" checked={form.activo} onChange={(e) => cambiar('activo', e.target.checked)} className="h-4 w-4 accent-andino" />
        Cuenta activa (puede iniciar sesión)
      </label>

      <AccionesFormulario onCancelar={onCancelar} guardando={guardando} />
    </form>
  );
}
