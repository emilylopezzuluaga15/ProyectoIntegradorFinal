import { Link } from 'react-router-dom';

const PERFILES = [
  {
    rol: 'Estudiantes',
    texto: 'Revisa tus notas por materia, tu porcentaje de asistencia y descarga tu boletín.',
    color: 'border-andino',
  },
  {
    rol: 'Docentes',
    texto: 'Califica y toma asistencia en los grupos que tienes asignados y detecta a tiempo quién va en riesgo.',
    color: 'border-maiz',
  },
  {
    rol: 'Coordinación',
    texto: 'Organiza carreras, materias, grupos, inscripciones y asignaciones de docentes.',
    color: 'border-tinta',
  },
  {
    rol: 'Administración',
    texto: 'Además de lo anterior, crea y administra las cuentas de acceso de toda la comunidad.',
    color: 'border-gris',
  },
];

export default function AccesoPorRol() {
  return (
    <section className="bg-niebla py-20 sm:py-28">
      <div className="mx-auto max-w-6xl px-4 sm:px-6">
        <div className="max-w-2xl">
          <h2 className="font-titulo text-3xl font-bold tracking-tight sm:text-4xl">Una plataforma para toda la comunidad</h2>
          <p className="mt-3 leading-relaxed text-gris">
            Al ingresar, cada persona ve solo lo que necesita según su rol.
          </p>
        </div>

        <div className="mt-12 grid gap-x-8 gap-y-10 sm:grid-cols-2 lg:grid-cols-4">
          {PERFILES.map((p) => (
            <div key={p.rol} className={`border-t-4 ${p.color} pt-5`}>
              <h3 className="text-xl font-semibold">{p.rol}</h3>
              <p className="mt-2 leading-relaxed text-gris">{p.texto}</p>
            </div>
          ))}
        </div>

        <Link to="/login" className="mt-12 inline-block rounded-full bg-andino px-6 py-3 font-semibold text-white hover:bg-andino-oscuro">
          Ingresar a la plataforma
        </Link>
      </div>
    </section>
  );
}
