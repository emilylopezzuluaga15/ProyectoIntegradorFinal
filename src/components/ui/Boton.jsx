const VARIANTES = {
  primario: 'bg-andino text-white hover:bg-andino-oscuro',
  secundario: 'bg-white text-pizarra border border-linea hover:bg-niebla',
  peligro: 'bg-red-700 text-white hover:bg-red-800',
  texto: 'text-andino hover:text-andino-oscuro hover:bg-andino-suave',
  textoPeligro: 'text-gray-500 hover:text-red-700 hover:bg-red-50',
};

export default function Boton({ variante = 'primario', tamano = 'normal', className = '', type = 'button', ...props }) {
  const medida = tamano === 'pequeno' ? 'px-2.5 py-1 text-xs' : 'px-4 py-2 text-sm';
  return (
    <button
      type={type}
      className={`${VARIANTES[variante]} ${medida} inline-flex items-center justify-center gap-2 rounded-lg font-semibold transition-colors disabled:opacity-50 disabled:cursor-not-allowed ${className}`}
      {...props}
    />
  );
}
