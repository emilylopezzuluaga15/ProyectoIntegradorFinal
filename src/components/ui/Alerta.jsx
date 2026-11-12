const TIPOS = {
  error: 'bg-red-50 text-red-800 border-red-200',
  aviso: 'bg-maiz-suave text-amber-900 border-maiz/40',
  exito: 'bg-andino-suave text-andino-oscuro border-andino/30',
};

export default function Alerta({ tipo = 'error', children, onCerrar }) {
  if (!children) return null;
  return (
    <div role={tipo === 'error' ? 'alert' : 'status'} className={`mb-4 flex items-start justify-between gap-4 rounded-lg border px-4 py-3 text-sm ${TIPOS[tipo]}`}>
      <span>{children}</span>
      {onCerrar && (
        <button onClick={onCerrar} aria-label="Cerrar mensaje" className="text-lg leading-none opacity-60 hover:opacity-100">
          &times;
        </button>
      )}
    </div>
  );
}
