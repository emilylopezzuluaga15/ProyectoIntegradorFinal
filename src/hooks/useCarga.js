import { useCallback, useEffect, useState } from 'react';
import { mensajeError } from '../utils/errores';

// Carga datos del backend al abrir una pantalla y permite recargarlos después de guardar o eliminar.
// Uso: const { datos, cargando, error, recargar } = useCarga(() => getCarreras(), []);
export default function useCarga(funcionCarga, dependencias = [], valorInicial = []) {
  const [datos, setDatos] = useState(valorInicial);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState(null);

  // eslint-disable-next-line react-hooks/exhaustive-deps
  const cargar = useCallback(funcionCarga, dependencias);

  const recargar = useCallback(async () => {
    setCargando(true);
    try {
      setDatos(await cargar());
      setError(null);
    } catch (err) {
      setError(mensajeError(err));
    } finally {
      setCargando(false);
    }
  }, [cargar]);

  useEffect(() => {
    recargar();
  }, [recargar]);

  return { datos, setDatos, cargando, error, setError, recargar };
}
