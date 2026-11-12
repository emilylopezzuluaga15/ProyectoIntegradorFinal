// Íconos simples dibujados con SVG, para no depender de librerías externas
const TRAZOS = {
  birrete: <><path d="M3 9.5 12 5l9 4.5-9 4.5L3 9.5Z" /><path d="M7 11.5V16c0 1.2 2.2 2.5 5 2.5s5-1.3 5-2.5v-4.5" /><path d="M21 9.5V15" /></>,
  maletin: <><rect x="3" y="7.5" width="18" height="12" rx="2" /><path d="M8.5 7.5V6a2 2 0 0 1 2-2h3a2 2 0 0 1 2 2v1.5" /><path d="M3 13h18" /></>,
  libro: <><path d="M4 5.5A1.5 1.5 0 0 1 5.5 4H11v15H5.5A1.5 1.5 0 0 0 4 20.5v-15Z" /><path d="M20 5.5A1.5 1.5 0 0 0 18.5 4H13v15h5.5a1.5 1.5 0 0 1 1.5 1.5v-15Z" /></>,
  corazon: <path d="M12 20s-7-4.4-7-10a4 4 0 0 1 7-2.6A4 4 0 0 1 19 10c0 5.6-7 10-7 10Z" />,
  certificado: <><rect x="4" y="4" width="16" height="12" rx="1.5" /><path d="M8 8h8M8 11h5" /><circle cx="15.5" cy="17" r="2.5" /><path d="m14 19-1 2.5M17 19l1 2.5" /></>,
  pantalla: <><rect x="3" y="4" width="18" height="12" rx="2" /><path d="M8 20h8M12 16v4" /><path d="m8 11 2 2 4-4" /></>,
};

export default function Icono({ nombre, className = 'h-6 w-6' }) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" className={className} aria-hidden="true">
      {TRAZOS[nombre]}
    </svg>
  );
}

// Marca del instituto: un triángulo de montaña con el sol, en los colores corporativos
export function Marca({ className = 'h-9 w-9' }) {
  return (
    <svg viewBox="0 0 40 40" className={className} aria-hidden="true">
      <rect width="40" height="40" rx="10" fill="#0f2a3d" />
      <circle cx="27" cy="14" r="5" fill="#e3b23c" />
      <path d="M5 31 16 15l6 8 4-5 9 13H5Z" fill="#136f63" />
      <path d="M16 15l3.2 4.3L16 22l-2.6-3.3L16 15Z" fill="#f3f6f5" />
    </svg>
  );
}
