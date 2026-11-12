// Color según el estado de una nota: Aprobada, Reprobada, En curso, Sin nota
export function tonoEstadoNota(estado) {
  if (estado === 'Aprobada') return 'verde';
  if (estado === 'Reprobada') return 'rojo';
  if (estado === 'En curso') return 'dorado';
  return 'gris';
}

export function estadoDeNota(aprobada) {
  if (aprobada === null || aprobada === undefined) return 'En curso';
  return aprobada ? 'Aprobada' : 'Reprobada';
}
