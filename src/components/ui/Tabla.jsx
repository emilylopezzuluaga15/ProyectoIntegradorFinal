// Tabla reutilizable.
// columnas: [{ titulo: 'Nombre', campo: 'nombreCarrera' }] o [{ titulo: 'Estado', render: (fila) => <Etiqueta /> }]
// acciones: función opcional que recibe la fila y devuelve los botones de esa fila.
export default function Tabla({ columnas, filas, claveFila, acciones, cargando, vacio = 'No hay registros todavía.' }) {
  return (
    <div className="overflow-x-auto rounded-xl border border-linea bg-white">
      <table className="w-full min-w-[640px] border-collapse text-left text-sm">
        <thead>
          <tr className="border-b border-linea bg-niebla/70 text-xs font-semibold text-gris">
            {columnas.map((c) => (
              <th key={c.titulo} scope="col" className="px-4 py-3">{c.titulo}</th>
            ))}
            {acciones && <th scope="col" className="px-4 py-3 text-right">Acciones</th>}
          </tr>
        </thead>
        <tbody>
          {cargando ? (
            <tr>
              <td colSpan={columnas.length + 1} className="px-4 py-10 text-center text-gris">Cargando información…</td>
            </tr>
          ) : filas.length === 0 ? (
            <tr>
              <td colSpan={columnas.length + 1} className="px-4 py-10 text-center text-gris">{vacio}</td>
            </tr>
          ) : (
            filas.map((fila) => (
              <tr key={claveFila(fila)} className="border-b border-linea/70 last:border-0 hover:bg-niebla/50">
                {columnas.map((c) => (
                  <td key={c.titulo} className="px-4 py-3 align-middle">
                    {c.render ? c.render(fila) : (fila[c.campo] ?? '—')}
                  </td>
                ))}
                {acciones && (
                  <td className="px-4 py-2 text-right whitespace-nowrap">{acciones(fila)}</td>
                )}
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}
