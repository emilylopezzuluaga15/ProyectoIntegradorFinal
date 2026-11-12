import { INSTITUCION } from '../../config/institucion';
import { Marca } from './Icono';

export default function Contacto() {
  const { contacto } = INSTITUCION;
  const datos = [
    { titulo: 'Dirección', valor: contacto.direccion },
    { titulo: 'Teléfono', valor: contacto.telefono },
    { titulo: 'Correo', valor: contacto.correo, enlace: `mailto:${contacto.correo}` },
    { titulo: 'Horario de atención', valor: contacto.horario },
  ];

  return (
    <footer id="contacto" className="bg-white">
      <div className="mx-auto grid max-w-6xl gap-12 px-4 py-20 sm:px-6 lg:grid-cols-[1fr_1.5fr]">
        <div>
          <h2 className="font-titulo text-3xl font-bold tracking-tight sm:text-4xl">Hablemos</h2>
          <p className="mt-3 max-w-sm leading-relaxed text-gris">
            Te ayudamos a elegir tu programa y te contamos cómo inscribirte.
          </p>
        </div>

        <dl className="grid gap-8 sm:grid-cols-2">
          {datos.map((d) => (
            <div key={d.titulo}>
              <dt className="text-sm text-gris">{d.titulo}</dt>
              <dd className="mt-1 text-lg font-medium text-tinta">
                {d.enlace ? <a href={d.enlace} className="underline decoration-maiz decoration-2 underline-offset-4 hover:text-andino">{d.valor}</a> : d.valor}
              </dd>
            </div>
          ))}
        </dl>
      </div>

      <div className="border-t border-linea">
        <div className="mx-auto flex max-w-6xl flex-col gap-3 px-4 py-6 text-sm text-gris sm:flex-row sm:items-center sm:justify-between sm:px-6">
          <span className="flex items-center gap-3">
            <Marca className="h-7 w-7" />
            {INSTITUCION.nombre}
          </span>
          <span>{INSTITUCION.creditos}</span>
        </div>
      </div>
    </footer>
  );
}
