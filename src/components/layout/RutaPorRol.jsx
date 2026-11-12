import { useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { puedeEntrar } from '../../config/roles';
import SinPermiso from '../../pages/SinPermiso';

// Si alguien escribe a mano una dirección que su rol no puede ver, se le muestra un aviso
export default function RutaPorRol({ children }) {
  const { usuario } = useAuth();
  const { pathname } = useLocation();

  if (!puedeEntrar(usuario.rol, pathname)) {
    return <SinPermiso />;
  }
  return children;
}
