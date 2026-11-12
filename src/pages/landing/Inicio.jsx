import SitioEncabezado from '../../components/landing/SitioEncabezado';
import Portada from '../../components/landing/Portada';
import Servicios from '../../components/landing/Servicios';
import Programas from '../../components/landing/Programas';
import MapaCampus from '../../components/landing/MapaCampus';
import AccesoPorRol from '../../components/landing/AccesoPorRol';
import Contacto from '../../components/landing/Contacto';

// Página pública: lo primero que ve cualquier persona antes de iniciar sesión
export default function Inicio() {
  return (
    <div className="min-h-screen bg-white">
      <SitioEncabezado />
      <main>
        <Portada />
        <Servicios />
        <Programas />
        <MapaCampus />
        <AccesoPorRol />
      </main>
      <Contacto />
    </div>
  );
}
