import { claseInput } from './Campo';

export default function Buscador({ valor, onCambiar, placeholder = 'Buscar…' }) {
  return (
    <div className="mb-4 max-w-md">
      <label htmlFor="buscador" className="sr-only">{placeholder}</label>
      <input
        id="buscador"
        type="search"
        value={valor}
        onChange={(e) => onCambiar(e.target.value)}
        placeholder={placeholder}
        className={claseInput}
      />
    </div>
  );
}
