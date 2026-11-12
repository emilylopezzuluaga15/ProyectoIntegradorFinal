import { Link } from 'react-router-dom';

export default function SinPermiso() {
  return (
    <div className="mx-auto max-w-md py-20 text-center">
      <h1 className="text-2xl font-bold">Esta sección no está disponible para tu rol</h1>
      <p className="mt-3 text-gris">Si crees que deberías tener acceso, comunícate con la administración del instituto.</p>
      <Link to="/app" className="mt-6 inline-block rounded-lg bg-andino px-5 py-2.5 font-semibold text-white hover:bg-andino-oscuro">
        Volver al inicio
      </Link>
    </div>
  );
}
