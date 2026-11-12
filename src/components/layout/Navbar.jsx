import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { NOMBRE_ROL } from '../../config/roles';

export default function Navbar({ onAbrirMenu }) {
  const { usuario, logout } = useAuth();
  const navigate = useNavigate();

  const cerrarSesion = () => {
    logout();
    navigate('/login', { replace: true });
  };

  return (
    <header className="no-imprimir sticky top-0 z-20 flex items-center justify-between border-b border-linea bg-white/95 px-4 py-3 backdrop-blur sm:px-8">
      <button onClick={onAbrirMenu} className="rounded-lg p-2 text-tinta hover:bg-niebla lg:hidden" aria-label="Abrir menú">
        <svg viewBox="0 0 24 24" className="h-6 w-6" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true">
          <path d="M4 7h16M4 12h16M4 17h16" />
        </svg>
      </button>

      <div className="ml-auto flex items-center gap-4">
        <div className="text-right leading-tight">
          <p className="text-sm font-semibold text-tinta">{usuario.username}</p>
          <p className="text-xs text-gris">{NOMBRE_ROL[usuario.rol] ?? usuario.rol}</p>
        </div>
        <span className="flex h-9 w-9 items-center justify-center rounded-full bg-maiz font-titulo font-bold text-tinta" aria-hidden="true">
          {usuario.username.charAt(0).toUpperCase()}
        </span>
        <button onClick={cerrarSesion} className="rounded-lg border border-linea px-3 py-1.5 text-sm font-medium text-pizarra hover:border-red-200 hover:bg-red-50 hover:text-red-800">
          Cerrar sesión
        </button>
      </div>
    </header>
  );
}
