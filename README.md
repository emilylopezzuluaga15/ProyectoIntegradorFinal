# Sistema de Gestión Académica — Frontend

Aplicación React (Vite + Tailwind CSS v4) conectada al backend Spring Boot `gestionAcademica` mediante JWT.

Tiene dos partes:

- **Página institucional pública** (`/`): servicios, programas, mapa interactivo del campus y acceso a la plataforma.
- **Plataforma académica** (`/app`): cada persona ve un menú distinto según su rol.

## Cómo arrancarlo

1. Enciende la base de datos y el backend (`.\mvnw.cmd spring-boot:run`). Debe quedar en `http://localhost:8080`.
2. En esta carpeta ejecuta:

```bash
npm install
npm run dev
```

3. Abre `http://localhost:5173`.

La URL del backend se configura en el archivo `.env` con `VITE_API_URL`.

## Usuarios de prueba (los crea `db/data.sql` del backend)

| Usuario | Contraseña | Rol |
|---|---|---|
| admin | Admin123* | Administrativo |
| coordinador | Demo1234* | Coordinador |
| profesor1 / profesor2 | Demo1234* | Profesor |
| est001 / est002 | Demo1234* | Estudiante |

## Qué puede hacer cada rol

| Rol | Menú |
|---|---|
| Administrativo | Todo lo del coordinador + **Usuarios** (crear cuentas y vincularlas a estudiantes o profesores) |
| Coordinador | Carreras, niveles, materias, plan de estudios, estudiantes, profesores, grupos, asignaciones, inscripciones, calificaciones, asistencia y reportes |
| Profesor | Mis cursos, estudiantes (solo consulta), calificaciones y asistencia **solo de sus materias y grupos**, reportes de sus grupos |
| Estudiante | Mis notas, mi asistencia y mi boletín (imprimible) |

Los permisos del menú están en `src/config/roles.js` y coinciden con los del backend (`AccesoService.java`).
Aunque alguien escriba una dirección a mano, la app y el backend bloquean lo que su rol no puede ver.

## Estructura

```
src/
├── config/
│   ├── institucion.js   ← nombre, textos, programas y edificios de la página pública
│   ├── roles.js         ← menú y permisos por rol
│   └── opciones.js      ← opciones de listas (género, estado)
├── context/AuthContext.jsx   ← sesión: login, logout y usuario actual
├── hooks/
│   ├── useCarga.js      ← carga datos del backend con manejo de errores
│   └── useAlcance.js    ← grupos y materias que puede usar cada rol
├── services/            ← llamadas al backend, una por entidad (apiClient agrega el token)
├── components/
│   ├── landing/         ← secciones de la página pública
│   ├── layout/          ← menú lateral, barra superior y protección de rutas
│   ├── ui/              ← piezas reutilizables: Boton, Modal, Tabla, Campo, Alerta…
│   ├── forms/           ← formularios (uno genérico + los especiales)
│   └── PaginaCrud.jsx   ← pantalla genérica de listar, crear, editar y eliminar
├── pages/               ← una página por ruta (profesor/ y estudiante/ para las vistas propias)
├── routes/AppRoutes.jsx
└── utils/               ← mensajes de error, fechas y estados de notas
```

## Personalizar la institución

- **Nombre, lema, contacto, servicios, programas y edificios:** `src/config/institucion.js`
- **Colores corporativos:** bloque `@theme` en `src/index.css` (`tinta`, `andino`, `maiz`…)
- **Tipografías:** paquetes `@fontsource-variable` importados en `src/main.jsx`

## Scripts

- `npm run dev` — servidor de desarrollo.
- `npm run build` — build de producción en `dist/`.
- `npm run lint` — revisa el código con ESLint.

---

Proyecto académico — CESDE. Autores: Daniel, Emily y Simón.
