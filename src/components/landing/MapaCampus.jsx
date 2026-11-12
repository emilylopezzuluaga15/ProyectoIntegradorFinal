import { useState } from 'react';
import { CAMPUS } from '../../config/institucion';

// Infraestructura del instituto como un plano interactivo: al elegir un edificio se ven sus detalles.
export default function MapaCampus() {
  const [seleccionado, setSeleccionado] = useState(CAMPUS[0].id);
  const edificio = CAMPUS.find((e) => e.id === seleccionado);

  return (
    <section id="campus" className="bg-tinta py-20 text-white sm:py-28">
      <div className="mx-auto max-w-6xl px-4 sm:px-6">
        <h2 className="font-titulo text-3xl font-bold tracking-tight text-white sm:text-4xl">Recorre el campus</h2>
        <p className="mt-3 max-w-xl leading-relaxed text-white/70">
          Toca un edificio en el plano para ver qué encuentras ahí.
        </p>

        <div className="mt-10 grid gap-8 lg:grid-cols-[1.7fr_1fr]">
          <div className="rounded-3xl bg-tinta-claro p-3 sm:p-5">
            <svg viewBox="0 0 800 520" className="h-auto w-full" role="img" aria-label="Plano del campus">
              {/* Zonas verdes y senderos */}
              <rect x="0" y="0" width="800" height="520" rx="18" fill="#163a52" />
              <path d="M20 205h760M310 20v480M560 220v280" stroke="#2a5470" strokeWidth="22" strokeLinecap="round" />
              <circle cx="160" cy="440" r="48" fill="#136f63" opacity="0.55" />
              <circle cx="680" cy="440" r="40" fill="#136f63" opacity="0.55" />
              <circle cx="625" cy="470" r="22" fill="#136f63" opacity="0.55" />
              <circle cx="95" cy="450" r="24" fill="#136f63" opacity="0.55" />
              <text x="160" y="505" textAnchor="middle" fill="#8fb3c4" fontSize="13">Plazoleta central</text>

              {CAMPUS.map((e) => {
                const activo = e.id === seleccionado;
                return (
                  <g
                    key={e.id}
                    role="button"
                    tabIndex={0}
                    aria-label={e.nombre}
                    aria-pressed={activo}
                    onClick={() => setSeleccionado(e.id)}
                    onKeyDown={(ev) => (ev.key === 'Enter' || ev.key === ' ') && (ev.preventDefault(), setSeleccionado(e.id))}
                    className="cursor-pointer outline-none [&:focus-visible>rect]:stroke-[#e3b23c]"
                  >
                    <rect
                      x={e.x}
                      y={e.y}
                      width={e.ancho}
                      height={e.alto}
                      rx="12"
                      fill={activo ? '#e3b23c' : '#f3f6f5'}
                      stroke={activo ? '#ffffff' : 'transparent'}
                      strokeWidth="4"
                      style={{ transition: 'fill 200ms' }}
                    />
                    <text
                      x={e.x + e.ancho / 2}
                      y={e.y + e.alto / 2 + 10}
                      textAnchor="middle"
                      fontSize="30"
                      fontWeight="700"
                      fill="#0f2a3d"
                      style={{ fontFamily: 'var(--font-titulo)', pointerEvents: 'none' }}
                    >
                      {e.sigla}
                    </text>
                  </g>
                );
              })}
            </svg>
          </div>

          <div className="flex flex-col">
            <div aria-live="polite" className="rounded-3xl bg-white p-6 text-pizarra sm:p-8">
              <h3 className="font-titulo text-2xl font-bold">{edificio.nombre}</h3>
              <p className="mt-3 leading-relaxed text-gris">{edificio.descripcion}</p>
              <ul className="mt-6 space-y-3">
                {edificio.detalles.map((d) => (
                  <li key={d} className="flex gap-3">
                    <span className="mt-2 h-2 w-2 shrink-0 rounded-full bg-andino" aria-hidden="true" />
                    <span>{d}</span>
                  </li>
                ))}
              </ul>
            </div>

            <ul className="mt-5 flex flex-wrap gap-2" aria-label="Edificios">
              {CAMPUS.map((e) => (
                <li key={e.id}>
                  <button
                    onClick={() => setSeleccionado(e.id)}
                    className={`rounded-full px-3 py-1.5 text-sm transition-colors ${
                      e.id === seleccionado ? 'bg-maiz font-semibold text-tinta' : 'bg-white/10 text-white/80 hover:bg-white/20'
                    }`}
                  >
                    {e.nombre.replace(/^Bloque \w — /, '')}
                  </button>
                </li>
              ))}
            </ul>
          </div>
        </div>
      </div>
    </section>
  );
}
