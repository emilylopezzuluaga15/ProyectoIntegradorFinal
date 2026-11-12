import { Routes, Route, Navigate } from 'react-router-dom';
import Inicio from '../pages/landing/Inicio';
import Login from '../pages/Login';
import LayoutPrivado from '../components/layout/LayoutPrivado';
import RutaPorRol from '../components/layout/RutaPorRol';
import Dashboard from '../pages/Dashboard';
import Carreras from '../pages/Carreras';
import Niveles from '../pages/Niveles';
import Materias from '../pages/Materias';
import PlanEstudios from '../pages/PlanEstudios';
import Estudiantes from '../pages/Estudiantes';
import Profesores from '../pages/Profesores';
import Grupos from '../pages/Grupos';
import Asignaciones from '../pages/Asignaciones';
import Inscripciones from '../pages/Inscripciones';
import Notas from '../pages/Notas';
import Asistencias from '../pages/Asistencias';
import Reportes from '../pages/Reportes';
import Usuarios from '../pages/Usuarios';
import MisCursos from '../pages/profesor/MisCursos';
import MisNotas from '../pages/estudiante/MisNotas';
import MiAsistencia from '../pages/estudiante/MiAsistencia';
import MiBoletin from '../pages/estudiante/MiBoletin';

// Páginas internas. Cada ruta debe existir también en config/roles.js con los roles que la pueden ver.
const PAGINAS = [
  { ruta: '', elemento: <Dashboard /> },
  { ruta: 'carreras', elemento: <Carreras /> },
  { ruta: 'niveles', elemento: <Niveles /> },
  { ruta: 'materias', elemento: <Materias /> },
  { ruta: 'plan-estudios', elemento: <PlanEstudios /> },
  { ruta: 'estudiantes', elemento: <Estudiantes /> },
  { ruta: 'profesores', elemento: <Profesores /> },
  { ruta: 'grupos', elemento: <Grupos /> },
  { ruta: 'asignaciones', elemento: <Asignaciones /> },
  { ruta: 'inscripciones', elemento: <Inscripciones /> },
  { ruta: 'notas', elemento: <Notas /> },
  { ruta: 'asistencias', elemento: <Asistencias /> },
  { ruta: 'reportes', elemento: <Reportes /> },
  { ruta: 'usuarios', elemento: <Usuarios /> },
  { ruta: 'mis-cursos', elemento: <MisCursos /> },
  { ruta: 'mis-notas', elemento: <MisNotas /> },
  { ruta: 'mi-asistencia', elemento: <MiAsistencia /> },
  { ruta: 'mi-boletin', elemento: <MiBoletin /> },
];

export default function AppRoutes() {
  return (
    <Routes>
      {/* Públicas */}
      <Route path="/" element={<Inicio />} />
      <Route path="/login" element={<Login />} />

      {/* Privadas: requieren sesión y el rol adecuado */}
      <Route path="/app" element={<LayoutPrivado />}>
        {PAGINAS.map((p) =>
          p.ruta === '' ? (
            <Route key="inicio" index element={<RutaPorRol>{p.elemento}</RutaPorRol>} />
          ) : (
            <Route key={p.ruta} path={p.ruta} element={<RutaPorRol>{p.elemento}</RutaPorRol>} />
          )
        )}
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
