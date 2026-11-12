import { useEffect } from 'react';

// Ventana emergente. Se cierra con la X, con la tecla Escape o haciendo clic afuera.
export default function Modal({ abierto, titulo, onCerrar, children, ancho = 'max-w-lg' }) {
  useEffect(() => {
    if (!abierto) return undefined;
    const alPresionar = (e) => e.key === 'Escape' && onCerrar();
    window.addEventListener('keydown', alPresionar);
    return () => window.removeEventListener('keydown', alPresionar);
  }, [abierto, onCerrar]);

  if (!abierto) return null;

  return (
    <div
      className="fixed inset-0 z-50 flex items-start sm:items-center justify-center bg-tinta/50 p-4 overflow-y-auto"
      onMouseDown={(e) => e.target === e.currentTarget && onCerrar()}
    >
      <div role="dialog" aria-modal="true" aria-label={titulo} className={`w-full ${ancho} rounded-2xl bg-white shadow-xl my-8`}>
        <div className="flex items-center justify-between border-b border-linea px-6 py-4">
          <h3 className="text-lg font-semibold">{titulo}</h3>
          <button
            onClick={onCerrar}
            aria-label="Cerrar"
            className="h-8 w-8 rounded-full text-xl leading-none text-gris hover:bg-niebla hover:text-pizarra"
          >
            &times;
          </button>
        </div>
        <div className="px-6 py-5">{children}</div>
      </div>
    </div>
  );
}
