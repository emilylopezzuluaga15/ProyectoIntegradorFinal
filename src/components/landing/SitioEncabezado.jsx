import { useState } from 'react';
import { Link } from 'react-router-dom';
import { INSTITUCION } from '../../config/institucion';
import { Marca } from './Icono';

const ENLACES = [
  { href: '#servicios', texto: 'Servicios' },
  { href: '#programas', texto: 'Programas' },
  { href: '#campus', texto: 'Campus' },
  { href: '#contacto', texto: 'Contacto' },
];

export default function SitioEncabezado() {
  const [menuAbierto, setMenuAbierto] = useState(false);

  return (
    <header className="absolute inset-x-0 top-0 z-30">
      <div className="mx-auto flex max-w-6xl items-center justify-between px-4 py-5 sm:px-6">
        <a href="#inicio" className="flex items-center gap-3 text-white">
          <Marca />
          <span className="font-titulo text-lg font-semibold leading-tight">
            {INSTITUCION.nombreCorto}
            <span className="block text-xs font-normal text-white/70">Instituto técnico</span>
          </span>
        </a>

        <nav aria-label="Secciones" className="hidden items-center gap-8 md:flex">
          {ENLACES.map((e) => (
            <a key={e.href} href={e.href} className="text-sm font-medium text-white/80 hover:text-white">
              {e.texto}
            </a>
          ))}
          <Link to="/login" className="rounded-full bg-maiz px-5 py-2 text-sm font-semibold text-tinta hover:bg-white">
            Ingresar
          </Link>
        </nav>

        <button
          className="rounded-lg p-2 text-white md:hidden"
          aria-expanded={menuAbierto}
          aria-label="Abrir menú"
          onClick={() => setMenuAbierto(!menuAbierto)}
        >
          <svg viewBox="0 0 24 24" className="h-6 w-6" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true">
            <path d={menuAbierto ? 'M6 6l12 12M18 6 6 18' : 'M4 7h16M4 12h16M4 17h16'} />
          </svg>
        </button>
      </div>

      {menuAbierto && (
        <nav aria-label="Secciones" className="mx-4 rounded-2xl bg-white p-4 shadow-xl md:hidden">
          {ENLACES.map((e) => (
            <a key={e.href} href={e.href} onClick={() => setMenuAbierto(false)} className="block rounded-lg px-3 py-2.5 font-medium text-tinta hover:bg-niebla">
              {e.texto}
            </a>
          ))}
          <Link to="/login" className="mt-2 block rounded-lg bg-andino px-3 py-2.5 text-center font-semibold text-white">
            Ingresar a la plataforma
          </Link>
        </nav>
      )}
    </header>
  );
}
