const TONOS = {
  verde: 'bg-andino-suave text-andino-oscuro',
  rojo: 'bg-red-50 text-red-800',
  dorado: 'bg-maiz-suave text-amber-900',
  azul: 'bg-sky-50 text-sky-900',
  gris: 'bg-slate-100 text-slate-700',
};

export default function Etiqueta({ tono = 'gris', children }) {
  return (
    <span className={`inline-block whitespace-nowrap rounded-full px-2.5 py-0.5 text-xs font-semibold ${TONOS[tono]}`}>
      {children}
    </span>
  );
}
