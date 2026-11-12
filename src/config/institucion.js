// Contenido de la página pública de la institución.
// Para cambiar el nombre, los textos, los programas o los edificios del campus, solo se edita este archivo.

export const INSTITUCION = {
  nombre: 'Instituto Técnico Valle Andino',
  nombreCorto: 'Valle Andino',
  lema: 'Formación técnica para trabajar desde el primer semestre',
  descripcion:
    'Programas técnicos de dos años con prácticas en empresas de la región, laboratorios equipados y docentes que vienen de la industria.',
  contacto: {
    direccion: 'Sede principal, Medellín, Antioquia',
    telefono: '(604) 000 0000',
    correo: 'admisiones@valleandino.edu.co',
    horario: 'Lunes a viernes de 7:00 a.m. a 9:00 p.m. Sábados de 8:00 a.m. a 1:00 p.m.',
  },
  creditos: 'Proyecto académico desarrollado por Daniel, Emily y Simón — CESDE',
};

// El campo "icono" usa los nombres definidos en components/landing/Icono.jsx
export const SERVICIOS = [
  {
    icono: 'birrete',
    titulo: 'Programas técnicos',
    texto: 'Seis carreras técnicas de cuatro semestres, en jornada diurna, nocturna o sabatina.',
  },
  {
    icono: 'maletin',
    titulo: 'Prácticas empresariales',
    texto: 'El último semestre lo haces dentro de una empresa aliada, con contrato de aprendizaje.',
  },
  {
    icono: 'libro',
    titulo: 'Biblioteca y recursos digitales',
    texto: 'Préstamo de libros, bases de datos académicas y salas de estudio que se reservan en línea.',
  },
  {
    icono: 'corazon',
    titulo: 'Bienestar estudiantil',
    texto: 'Asesoría psicológica, enfermería, deportes y apoyo económico para quien lo necesite.',
  },
  {
    icono: 'certificado',
    titulo: 'Educación continua',
    texto: 'Cursos cortos y diplomados para actualizarte sin dejar de trabajar.',
  },
];

// Servicio destacado: la plataforma académica (este mismo sistema)
export const PLATAFORMA = {
  titulo: 'Plataforma académica',
  texto:
    'Consulta tus notas y tu asistencia, descarga tu boletín y, si eres docente, califica a tus grupos desde cualquier lugar.',
};

export const PROGRAMAS = [
  { nombre: 'Técnico en Desarrollo de Software', area: 'Tecnología', duracion: '4 semestres', jornadas: 'Diurna y nocturna' },
  { nombre: 'Técnico en Análisis de Datos', area: 'Tecnología', duracion: '4 semestres', jornadas: 'Nocturna' },
  { nombre: 'Técnico en Diseño Gráfico Digital', area: 'Creatividad', duracion: '4 semestres', jornadas: 'Diurna y sabatina' },
  { nombre: 'Técnico en Gestión Administrativa', area: 'Negocios', duracion: '4 semestres', jornadas: 'Diurna, nocturna y sabatina' },
  { nombre: 'Técnico en Mantenimiento Automotriz', area: 'Industria', duracion: '4 semestres', jornadas: 'Diurna' },
  { nombre: 'Auxiliar en Enfermería', area: 'Salud', duracion: '4 semestres', jornadas: 'Diurna' },
];

// Edificios del mapa del campus. x, y, ancho y alto son posiciones dentro del plano (800 x 520).
export const CAMPUS = [
  {
    id: 'aulas',
    nombre: 'Bloque A — Aulas',
    sigla: 'A',
    x: 60, y: 60, ancho: 230, alto: 110,
    descripcion: 'El edificio principal de clases teóricas, con cinco pisos y ascensor.',
    detalles: ['32 aulas con video beam y aire acondicionado', 'Salas de estudio en cada piso', 'Oficinas de coordinación académica'],
  },
  {
    id: 'computo',
    nombre: 'Bloque B — Salas de cómputo',
    sigla: 'B',
    x: 330, y: 60, ancho: 170, alto: 110,
    descripcion: 'Laboratorios para los programas de tecnología y diseño.',
    detalles: ['8 salas con 240 equipos', 'Laboratorio de redes y servidores', 'Sala Mac para diseño gráfico'],
  },
  {
    id: 'biblioteca',
    nombre: 'Biblioteca',
    sigla: 'Bi',
    x: 540, y: 60, ancho: 200, alto: 140,
    descripcion: 'Dos pisos de colecciones, cubículos de estudio y préstamo de equipos.',
    detalles: ['12.000 títulos físicos', '20 cubículos de estudio en grupo', 'Préstamo de portátiles y calculadoras'],
  },
  {
    id: 'talleres',
    nombre: 'Bloque C — Talleres',
    sigla: 'C',
    x: 60, y: 230, ancho: 190, alto: 130,
    descripcion: 'Espacios prácticos con equipos reales de la industria.',
    detalles: ['Taller automotriz con 6 elevadores', 'Laboratorio de electrónica', 'Laboratorio de simulación clínica'],
  },
  {
    id: 'auditorio',
    nombre: 'Auditorio',
    sigla: 'Au',
    x: 290, y: 240, ancho: 150, alto: 110,
    descripcion: 'Para grados, conferencias y eventos culturales.',
    detalles: ['320 sillas', 'Sonido e iluminación profesional', 'Transmisión en vivo'],
  },
  {
    id: 'bienestar',
    nombre: 'Bienestar',
    sigla: 'Bs',
    x: 480, y: 250, ancho: 130, alto: 100,
    descripcion: 'Atención en salud, acompañamiento psicológico y actividades deportivas.',
    detalles: ['Enfermería', 'Consultorios de psicología', 'Gimnasio'],
  },
  {
    id: 'cafeteria',
    nombre: 'Cafetería',
    sigla: 'Ca',
    x: 640, y: 250, ancho: 100, alto: 100,
    descripcion: 'Comida a precios estudiantiles y zona de mesas al aire libre.',
    detalles: ['Menú del día', 'Microondas de uso libre', 'Terraza con 80 puestos'],
  },
  {
    id: 'cancha',
    nombre: 'Cancha múltiple',
    sigla: 'Cm',
    x: 290, y: 400, ancho: 250, alto: 90,
    descripcion: 'Cancha cubierta para microfútbol, baloncesto y voleibol.',
    detalles: ['Torneos internos cada semestre', 'Préstamo de implementos', 'Graderías'],
  },
];
