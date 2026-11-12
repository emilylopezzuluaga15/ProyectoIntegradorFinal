// Convierte cualquier error de axios en un mensaje claro para mostrar en pantalla.
// El backend responde { message } y, si hay campos inválidos, también { errores: { campo: mensaje } }.
export function mensajeError(err) {
  if (!err?.response) {
    return 'No hay conexión con el servidor. Revisa que el backend esté encendido.';
  }

  const { status, data } = err.response;

  if (data?.errores && Object.keys(data.errores).length > 0) {
    return Object.values(data.errores).join('. ');
  }

  if (status === 403) {
    return data?.message || 'Tu rol no tiene permiso para hacer esta acción.';
  }

  return data?.message || `El servidor respondió con un error (${status}).`;
}

// Fecha de hoy en formato yyyy-mm-dd, como la esperan los campos tipo date
export function hoy() {
  const fecha = new Date();
  const offset = fecha.getTimezoneOffset() * 60000;
  return new Date(fecha - offset).toISOString().slice(0, 10);
}

export function formatoNota(valor) {
  return valor === null || valor === undefined ? '—' : Number(valor).toFixed(1);
}
