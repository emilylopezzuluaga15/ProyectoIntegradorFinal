import { useState } from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { INSTITUCION } from '../../config/institucion';
import Sidebar from './Sidebar';
import Navbar from './Navbar';

// Estructura de la plataforma (menú + barra superior). Sin sesión, envía al login.
export default function LayoutPrivado() {
  const { usuario } = useAuth();
  const [menuAbierto, setMenuAbierto] = useState(false);

  if (!usuario) {
    return <Navigate to="/login" replace />;
  }

  return (
    <div className="flex min-h-screen bg-niebla">
      <Sidebar abierto={menuAbierto} onCerrar={() => setMenuAbierto(false)} />
      <div className="flex min-w-0 flex-1 flex-col">
        <Navbar onAbrirMenu={() => setMenuAbierto(true)} />
        <main className="mx-auto w-full max-w-6xl flex-1 px-4 py-8 sm:px-8">
          <Outlet />
        </main>
        <footer className="no-imprimir border-t border-linea px-8 py-4 text-xs text-gris">
          {INSTITUCION.nombre} · {INSTITUCION.creditos}
        </footer>
      </div>
    </div>
  );
}
