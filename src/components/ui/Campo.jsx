// Estilos compartidos para todos los campos de los formularios
export const claseInput =
  'w-full rounded-lg border border-linea bg-white px-3 py-2 text-sm text-pizarra placeholder:text-gris/70 focus:border-andino focus:outline-none focus:ring-2 focus:ring-andino/25 disabled:bg-niebla disabled:text-gris';

// Envuelve un input con su etiqueta y una ayuda opcional
export default function Campo({ etiqueta, id, ayuda, children }) {
  return (
    <div>
      <label htmlFor={id} className="mb-1 block text-sm font-medium text-pizarra">{etiqueta}</label>
      {children}
      {ayuda && <p className="mt-1 text-xs text-gris">{ayuda}</p>}
    </div>
  );
}

// Botones al final de cada formulario
export function AccionesFormulario({ onCancelar, textoGuardar = 'Guardar', guardando }) {
  return (
    <div className="flex justify-end gap-2 pt-2">
      <button
        type="button"
        onClick={onCancelar}
        className="rounded-lg border border-linea bg-white px-4 py-2 text-sm font-semibold text-pizarra hover:bg-niebla"
      >
        Cancelar
      </button>
      <button
        type="submit"
        disabled={guardando}
        className="rounded-lg bg-andino px-4 py-2 text-sm font-semibold text-white hover:bg-andino-oscuro disabled:opacity-60"
      >
        {guardando ? 'Guardando…' : textoGuardar}
      </button>
    </div>
  );
}
