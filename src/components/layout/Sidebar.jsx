import { NavLink } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { menuDelRol } from '../../config/roles';
import { INSTITUCION } from '../../config/institucion';
import { Marca } from '../landing/Icono';

// Menú lateral: solo muestra las opciones permitidas para el rol de quien inició sesión
export default function Sidebar({ abierto, onCerrar }) {
  const { usuario } = useAuth();
  const opciones = menuDelRol(usuario.rol);
  const grupos = [...new Set(opciones.map((o) => o.grupo))];

  const claseEnlace = ({ isActive }) =>
    `block rounded-lg px-3 py-2 text-sm transition-colors ${
      isActive ? 'bg-andino font-semibold text-white' : 'text-white/70 hover:bg-white/10 hover:text-white'
    }`;

  return (
    <>
      {abierto && <div className="fixed inset-0 z-30 bg-tinta/50 lg:hidden" onClick={onCerrar} aria-hidden="true" />}
      <aside
        className={`no-imprimir fixed inset-y-0 left-0 z-40 flex w-64 flex-col bg-tinta text-white transition-transform lg:static lg:translate-x-0 ${
          abierto ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        <div className="flex items-center gap-3 border-b border-white/10 px-5 py-5">
          <Marca />
          <span className="font-titulo text-sm font-semibold leading-tight text-white">
            {INSTITUCION.nombreCorto}
            <span className="block text-xs font-normal text-white/60">Plataforma académica</span>
          </span>
        </div>

        <nav aria-label="Menú principal" className="flex-1 overflow-y-auto px-3 py-4">
          {grupos.map((grupo) => (
            <div key={grupo} className="mb-5">
              <p className="mb-1 px-3 text-xs text-white/45">{grupo}</p>
              <ul className="space-y-0.5">
                {opciones
                  .filter((o) => o.grupo === grupo)
                  .map((o) => (
                    <li key={o.ruta}>
                      <NavLink to={o.ruta} end={o.ruta === '/app'} className={claseEnlace} onClick={onCerrar}>
                        {o.texto}
                      </NavLink>
                    </li>
                  ))}
              </ul>
            </div>
          ))}
        </nav>
      </aside>
    </>
  );
}
