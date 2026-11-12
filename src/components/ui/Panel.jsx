// Bloque blanco con título, para agrupar contenido dentro de una pantalla
export default function Panel({ titulo, descripcion, acciones, children, className = '' }) {
  return (
    <section className={`rounded-xl border border-linea bg-white p-5 sm:p-6 ${className}`}>
      {(titulo || acciones) && (
        <div className="mb-4 flex flex-wrap items-start justify-between gap-3">
          <div>
            {titulo && <h2 className="text-lg font-semibold">{titulo}</h2>}
            {descripcion && <p className="mt-0.5 text-sm text-gris">{descripcion}</p>}
          </div>
          {acciones}
        </div>
      )}
      {children}
    </section>
  );
}

// Un número importante con su explicación (usado en los inicios de cada rol)
export function Dato({ valor, etiqueta, detalle, tono = 'tinta' }) {
  const color = { tinta: 'text-tinta', andino: 'text-andino', maiz: 'text-amber-700', rojo: 'text-red-700' }[tono];
  return (
    <div className="rounded-xl border border-linea bg-white p-5">
      <p className="text-sm text-gris">{etiqueta}</p>
      <p className={`mt-1 font-titulo text-3xl font-bold ${color}`}>{valor}</p>
      {detalle && <p className="mt-1 text-xs text-gris">{detalle}</p>}
    </div>
  );
}
