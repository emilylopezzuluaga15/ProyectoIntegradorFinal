// Qué puede ver cada rol. Debe coincidir con los permisos del backend (AccesoService.java):
//  - ADMINISTRATIVO y COORDINADOR ("gestores") administran todo lo académico.
//  - Solo ADMINISTRATIVO administra cuentas de usuario.
//  - PROFESOR trabaja con los grupos y materias que tiene asignados.
//  - ESTUDIANTE solo consulta su propia información.

export const ROLES = {
  ADMINISTRATIVO: 'ADMINISTRATIVO',
  COORDINADOR: 'COORDINADOR',
  PROFESOR: 'PROFESOR',
  ESTUDIANTE: 'ESTUDIANTE',
};

export const NOMBRE_ROL = {
  ADMINISTRATIVO: 'Administrativo',
  COORDINADOR: 'Coordinador',
  PROFESOR: 'Profesor',
  ESTUDIANTE: 'Estudiante',
};

const GESTORES = [ROLES.ADMINISTRATIVO, ROLES.COORDINADOR];
const PERSONAL = [...GESTORES, ROLES.PROFESOR];
const TODOS = [...PERSONAL, ROLES.ESTUDIANTE];

export const esGestor = (usuario) => GESTORES.includes(usuario?.rol);

// Cada entrada es una opción del menú lateral y una ruta protegida.
// "grupo" ordena el menú en secciones.
export const MENU = [
  { ruta: '/app', texto: 'Inicio', roles: TODOS, grupo: 'General' },

  { ruta: '/app/mis-cursos', texto: 'Mis cursos', roles: [ROLES.PROFESOR], grupo: 'Mi trabajo' },
  { ruta: '/app/mis-notas', texto: 'Mis notas', roles: [ROLES.ESTUDIANTE], grupo: 'Mi información' },
  { ruta: '/app/mi-asistencia', texto: 'Mi asistencia', roles: [ROLES.ESTUDIANTE], grupo: 'Mi información' },
  { ruta: '/app/mi-boletin', texto: 'Mi boletín', roles: [ROLES.ESTUDIANTE], grupo: 'Mi información' },

  { ruta: '/app/carreras', texto: 'Carreras', roles: GESTORES, grupo: 'Oferta académica' },
  { ruta: '/app/niveles', texto: 'Niveles', roles: GESTORES, grupo: 'Oferta académica' },
  { ruta: '/app/materias', texto: 'Materias', roles: GESTORES, grupo: 'Oferta académica' },
  { ruta: '/app/plan-estudios', texto: 'Plan de estudios', roles: GESTORES, grupo: 'Oferta académica' },

  { ruta: '/app/estudiantes', texto: 'Estudiantes', roles: PERSONAL, grupo: 'Comunidad' },
  { ruta: '/app/profesores', texto: 'Profesores', roles: GESTORES, grupo: 'Comunidad' },
  { ruta: '/app/grupos', texto: 'Grupos', roles: GESTORES, grupo: 'Comunidad' },
  { ruta: '/app/asignaciones', texto: 'Asignaciones', roles: GESTORES, grupo: 'Comunidad' },
  { ruta: '/app/inscripciones', texto: 'Inscripciones', roles: GESTORES, grupo: 'Comunidad' },

  { ruta: '/app/notas', texto: 'Calificaciones', roles: PERSONAL, grupo: 'Seguimiento' },
  { ruta: '/app/asistencias', texto: 'Asistencia', roles: PERSONAL, grupo: 'Seguimiento' },
  { ruta: '/app/reportes', texto: 'Reportes', roles: PERSONAL, grupo: 'Seguimiento' },

  { ruta: '/app/usuarios', texto: 'Usuarios', roles: [ROLES.ADMINISTRATIVO], grupo: 'Administración' },
];

export const menuDelRol = (rol) => MENU.filter((item) => item.roles.includes(rol));

export const puedeEntrar = (rol, ruta) => {
  const item = MENU.find((m) => m.ruta === ruta);
  return Boolean(item && item.roles.includes(rol));
};
