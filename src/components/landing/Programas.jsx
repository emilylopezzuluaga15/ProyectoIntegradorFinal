import { useState } from 'react';
import { PROGRAMAS } from '../../config/institucion';

export default function Programas() {
  const areas = ['Todas', ...new Set(PROGRAMAS.map((p) => p.area))];
  const [area, setArea] = useState('Todas');
  const visibles = area === 'Todas' ? PROGRAMAS : PROGRAMAS.filter((p) => p.area === area);

  return (
    <section id="programas" className="bg-white py-20 sm:py-28">
      <div className="mx-auto max-w-6xl px-4 sm:px-6">
        <div className="flex flex-col gap-6 md:flex-row md:items-end md:justify-between">
          <div>
            <h2 className="font-titulo text-3xl font-bold tracking-tight sm:text-4xl">Programas técnicos</h2>
            <p className="mt-3 max-w-xl leading-relaxed text-gris">
              Todos duran cuatro semestres y el último incluye práctica en una empresa.
            </p>
          </div>

          <div role="group" aria-label="Filtrar por área" className="flex flex-wrap gap-2">
            {areas.map((a) => (
              <button
                key={a}
                onClick={() => setArea(a)}
                aria-pressed={area === a}
                className={`rounded-full px-4 py-1.5 text-sm font-medium transition-colors ${
                  area === a ? 'bg-tinta text-white' : 'bg-niebla text-pizarra hover:bg-linea'
                }`}
              >
                {a}
              </button>
            ))}
          </div>
        </div>

        <div className="mt-10 overflow-x-auto">
          <table className="w-full min-w-[620px] border-collapse text-left">
            <thead>
              <tr className="border-b-2 border-tinta text-sm text-gris">
                <th scope="col" className="py-3 pr-4 font-medium">Programa</th>
                <th scope="col" className="py-3 pr-4 font-medium">Área</th>
                <th scope="col" className="py-3 pr-4 font-medium">Duración</th>
                <th scope="col" className="py-3 font-medium">Jornadas</th>
              </tr>
            </thead>
            <tbody>
              {visibles.map((p) => (
                <tr key={p.nombre} className="border-b border-linea">
                  <td className="py-5 pr-4 font-titulo text-lg font-semibold text-tinta">{p.nombre}</td>
                  <td className="py-5 pr-4">
                    <span className="rounded-full bg-maiz-suave px-3 py-1 text-sm text-amber-900">{p.area}</span>
                  </td>
                  <td className="py-5 pr-4 text-gris">{p.duracion}</td>
                  <td className="py-5 text-gris">{p.jornadas}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </section>
  );
}
