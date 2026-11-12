import { Link } from 'react-router-dom';
import { SERVICIOS, PLATAFORMA } from '../../config/institucion';
import Icono from './Icono';

export default function Servicios() {
  return (
    <section id="servicios" className="bg-niebla py-20 sm:py-28">
      <div className="mx-auto grid max-w-6xl gap-12 px-4 sm:px-6 lg:grid-cols-[1fr_1.6fr]">
        <div className="lg:sticky lg:top-10 lg:self-start">
          <h2 className="font-titulo text-3xl font-bold tracking-tight sm:text-4xl">
            Lo que encuentras al estudiar con nosotros
          </h2>
          <p className="mt-4 max-w-md leading-relaxed text-gris">
            Además de las clases, el instituto te acompaña con servicios pensados para que termines tu carrera y
            consigas empleo.
          </p>
        </div>

        <div>
          <ul className="divide-y divide-linea border-y border-linea">
            {SERVICIOS.map((s) => (
              <li key={s.titulo} className="flex gap-5 py-6">
                <span className="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl bg-andino-suave text-andino">
                  <Icono nombre={s.icono} />
                </span>
                <div>
                  <h3 className="text-lg font-semibold">{s.titulo}</h3>
                  <p className="mt-1 leading-relaxed text-gris">{s.texto}</p>
                </div>
              </li>
            ))}
          </ul>

          <div className="mt-8 flex flex-col gap-5 rounded-2xl bg-tinta p-6 text-white sm:flex-row sm:items-center sm:p-8">
            <span className="flex h-14 w-14 shrink-0 items-center justify-center rounded-xl bg-maiz text-tinta">
              <Icono nombre="pantalla" className="h-7 w-7" />
            </span>
            <div className="flex-1">
              <h3 className="text-xl font-semibold text-white">{PLATAFORMA.titulo}</h3>
              <p className="mt-1 leading-relaxed text-white/75">{PLATAFORMA.texto}</p>
            </div>
            <Link to="/login" className="shrink-0 rounded-full bg-white px-5 py-2.5 text-center font-semibold text-tinta hover:bg-maiz">
              Ingresar
            </Link>
          </div>
        </div>
      </div>
    </section>
  );
}
