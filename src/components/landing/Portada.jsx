import { Link } from 'react-router-dom';
import { INSTITUCION } from '../../config/institucion';

// Primera sección: el lema sobre un paisaje de montañas en capas (el "valle andino")
export default function Portada() {
  return (
    <section id="inicio" className="relative overflow-hidden bg-tinta pt-36 pb-48 sm:pt-44 sm:pb-64">
      <Montanas />

      <div className="relative z-10 mx-auto max-w-6xl px-4 sm:px-6">
        <p className="mb-5 inline-block rounded-full border border-white/20 px-4 py-1.5 text-sm text-white/80">
          Inscripciones abiertas para el próximo semestre
        </p>
        <h1 className="max-w-3xl font-titulo text-4xl font-bold leading-[1.05] tracking-tight text-white sm:text-6xl">
          {INSTITUCION.lema}
        </h1>
        <p className="mt-6 max-w-xl text-lg leading-relaxed text-white/75">{INSTITUCION.descripcion}</p>
        <div className="mt-9 flex flex-wrap gap-3">
          <a href="#programas" className="rounded-full bg-maiz px-6 py-3 font-semibold text-tinta hover:bg-white">
            Ver programas
          </a>
          <Link to="/login" className="rounded-full border border-white/40 px-6 py-3 font-semibold text-white hover:bg-white/10">
            Ingresar a la plataforma
          </Link>
        </div>
      </div>
    </section>
  );
}

function Montanas() {
  return (
    <svg
      className="absolute inset-x-0 bottom-0 h-[70%] w-full"
      viewBox="0 0 1440 520"
      preserveAspectRatio="xMidYMax slice"
      aria-hidden="true"
    >
      <circle cx="1120" cy="170" r="70" fill="#e3b23c" opacity="0.9" />
      <path d="M0 330 220 170l170 120 210-190 230 200 180-110 220 150 210-110v290H0Z" fill="#1b3f59" />
      <path d="M0 400 180 290l200 110 240-170 200 150 220-120 210 150 190-90v300H0Z" fill="#136f63" opacity="0.85" />
      <path d="M0 460 260 360l220 80 260-120 230 110 240-70 230 90v90H0Z" fill="#0e564d" />
      <path d="M0 500c240-40 480-50 720-30s480 20 720-10v60H0Z" fill="#f3f6f5" />
    </svg>
  );
}
