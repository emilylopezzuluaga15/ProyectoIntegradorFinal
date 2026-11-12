import { useState } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { INSTITUCION } from '../config/institucion';
import { Marca } from '../components/landing/Icono';
import { claseInput } from '../components/ui/Campo';
import { mensajeError } from '../utils/errores';

// Cuentas que crea db/data.sql. Útiles para probar cada rol en clase.
const CUENTAS_DEMO = [
  { usuario: 'admin', clave: 'Admin123*', rol: 'Administrativo' },
  { usuario: 'coordinador', clave: 'Demo1234*', rol: 'Coordinador' },
  { usuario: 'profesor1', clave: 'Demo1234*', rol: 'Profesor' },
  { usuario: 'est001', clave: 'Demo1234*', rol: 'Estudiante' },
];

export default function Login() {
  const { usuario, login } = useAuth();
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [cargando, setCargando] = useState(false);

  if (usuario) {
    return <Navigate to="/app" replace />;
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setCargando(true);
    try {
      await login(username.trim(), password);
      navigate('/app', { replace: true });
    } catch (err) {
      setError(err.response?.status === 401 ? 'El usuario o la contraseña no son correctos.' : mensajeError(err));
    } finally {
      setCargando(false);
    }
  };

  const usarCuenta = (c) => {
    setUsername(c.usuario);
    setPassword(c.clave);
  };

  return (
    <div className="grid min-h-screen lg:grid-cols-2">
      <aside className="relative hidden overflow-hidden bg-tinta p-12 text-white lg:flex lg:flex-col lg:justify-between">
        <Link to="/" className="flex items-center gap-3">
          <Marca />
          <span className="font-titulo text-lg font-semibold">{INSTITUCION.nombre}</span>
        </Link>
        <div className="relative z-10 max-w-md">
          <h2 className="font-titulo text-4xl font-bold leading-tight text-white">Tu semestre, en un solo lugar.</h2>
          <p className="mt-4 text-lg text-white/70">
            Notas, asistencia, grupos y reportes, según tu rol en el instituto.
          </p>
        </div>
        <svg className="absolute inset-x-0 bottom-0 h-56 w-full" viewBox="0 0 720 220" preserveAspectRatio="none" aria-hidden="true">
          <path d="M0 140 160 50l120 70 150-100 140 110 150-60v150H0Z" fill="#1b3f59" />
          <path d="M0 190 200 110l170 60 190-80 160 90v40H0Z" fill="#136f63" />
        </svg>
      </aside>

      <main className="flex items-center justify-center bg-niebla px-4 py-12">
        <div className="w-full max-w-sm">
          <Link to="/" className="mb-8 flex items-center gap-3 lg:hidden">
            <Marca />
            <span className="font-titulo font-semibold text-tinta">{INSTITUCION.nombre}</span>
          </Link>

          <h1 className="font-titulo text-3xl font-bold">Ingresar</h1>
          <p className="mt-1 text-gris">Usa el usuario y la contraseña que te entregó el instituto.</p>

          <form onSubmit={handleSubmit} className="mt-8 space-y-5">
            {error && (
              <p role="alert" className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800">{error}</p>
            )}

            <div>
              <label htmlFor="username" className="mb-1 block text-sm font-medium">Usuario</label>
              <input id="username" value={username} onChange={(e) => setUsername(e.target.value)} required autoFocus autoComplete="username" className={claseInput} />
            </div>

            <div>
              <label htmlFor="password" className="mb-1 block text-sm font-medium">Contraseña</label>
              <input id="password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} required autoComplete="current-password" className={claseInput} />
            </div>

            <button type="submit" disabled={cargando} className="w-full rounded-lg bg-andino py-2.5 font-semibold text-white hover:bg-andino-oscuro disabled:opacity-60">
              {cargando ? 'Ingresando…' : 'Ingresar'}
            </button>
          </form>

          <details className="mt-8 rounded-lg border border-linea bg-white p-4 text-sm">
            <summary className="cursor-pointer font-medium text-pizarra">Cuentas de prueba</summary>
            <p className="mt-2 text-gris">Toca una cuenta para llenar el formulario.</p>
            <ul className="mt-3 space-y-1">
              {CUENTAS_DEMO.map((c) => (
                <li key={c.usuario}>
                  <button type="button" onClick={() => usarCuenta(c)} className="flex w-full justify-between rounded-md px-2 py-1.5 text-left hover:bg-niebla">
                    <span className="font-medium text-tinta">{c.usuario}</span>
                    <span className="text-gris">{c.rol}</span>
                  </button>
                </li>
              ))}
            </ul>
          </details>

          <Link to="/" className="mt-6 inline-block text-sm text-andino hover:underline">Volver a la página del instituto</Link>
        </div>
      </main>
    </div>
  );
}
