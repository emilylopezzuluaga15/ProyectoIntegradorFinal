# Gestión Académica

API REST para administrar una institución académica: carreras, niveles, plan de estudios, grupos, profesores, estudiantes, inscripciones, asistencia y notas. Incluye reportes (boletín, estudiantes en riesgo, planilla de notas) y un inicio de sesión con JWT y permisos por rol.

**Stack:** Java 21 · Spring Boot 4.1 · Spring Data JPA · Spring Security (JWT HS256) · MySQL 8 · Lombok

## Arquitectura

```
domain/          entidades JPA y puertos de repositorio
application/     servicios con las reglas de negocio (@Transactional)
infrastructure/  adaptadores JPA y seguridad (JWT, reglas de acceso)
presentation/    controladores REST, DTOs y manejo de errores
```

Los controladores reciben y devuelven **DTOs** (`presentation/dto`), nunca entidades. Las relaciones se envían por id (`"idEstudiante": "EST001"`) y las respuestas vienen aplanadas, con el id y el nombre del registro relacionado.

## Cómo ejecutarlo

1. **Base de datos.** En MySQL Workbench ejecuta:
   - `db/schema.sql`: crea la BD `sistema_academico` y sus tablas. Usa `IF NOT EXISTS`, así que sobre una BD existente solo agrega lo que falte (por ejemplo, la tabla `usuarios`).
   - `db/migracion_bd_existente.sql` (solo si tu BD ya existía antes de `schema.sql`): cambia `anio_inscripcion` de `YEAR` a `INT` y agrega los índices únicos que falten. Se ejecuta una sola vez.
   - `db/data.sql` (opcional, solo con la BD vacía): datos y usuarios de demostración.
2. **Configuración local.** Copia `src/main/resources/application-local.properties.example` como `application-local.properties` en la misma carpeta y pon tus valores. Ese archivo está en `.gitignore`. También puedes usar las variables de entorno `DB_PASSWORD`, `JWT_SECRET` y `ADMIN_PASSWORD`.
3. **Arrancar:** `./mvnw spring-boot:run` (o ejecuta `GestionAcademicaApplication` desde IntelliJ).
4. **Pruebas:** `./mvnw test`. Usan H2 en memoria, no necesitan MySQL.

Al arrancar, si no existe ningún usuario `ADMINISTRATIVO`, se crea `admin` con la contraseña de `app.admin.password`.

## Inicio de sesión

```http
POST /api/auth/login
{ "username": "admin", "password": "Admin123*" }
```

La respuesta trae `token`. Envíalo en cada petición como `Authorization: Bearer <token>`. El token dura 8 horas (`app.jwt.expiracion-minutos`). `GET /api/auth/me` devuelve el usuario del token.

Usuarios de `db/data.sql` (contraseña `Demo1234*`): `coordinador`, `profesor1` (PROF001), `profesor2` (PROF002), `est001` (EST001) y `est002` (EST002).

Las cuentas se administran en `/api/usuarios` (solo ADMINISTRATIVO). Un usuario `ESTUDIANTE` debe tener `idEstudiante` y uno `PROFESOR` debe tener `idProfesor`; así el sistema sabe qué información es suya.

## Permisos por rol

| Recurso | ADMINISTRATIVO | COORDINADOR | PROFESOR | ESTUDIANTE |
|---|---|---|---|---|
| Usuarios | CRUD | — | — | — |
| Carreras, niveles, materias, plan de estudios, grupos, asignaciones | CRUD | CRUD | consulta | consulta |
| Profesores | CRUD | CRUD | consulta | — |
| Estudiantes | CRUD | CRUD | consulta | solo el suyo |
| Inscripciones | CRUD | CRUD | consulta | solo las suyas |
| Notas y calificaciones | CRUD | CRUD | consulta; registra solo en **las materias que tiene asignadas** | solo las suyas |
| Asistencia | CRUD | CRUD | consulta; registra solo en **los grupos donde dicta** | solo la suya |
| Boletín | sí | sí | sí | solo el suyo |
| Estudiantes en riesgo / planilla de notas | sí | sí | solo de sus grupos y materias | — |

Las reglas están en `@PreAuthorize` en cada controlador y en `infrastructure/security/AccesoService`.

## Códigos de respuesta

| Código | Cuándo |
|---|---|
| 400 | Campos inválidos o JSON mal formado |
| 401 | Sin token, token vencido o login fallido |
| 403 | El rol no tiene permiso |
| 404 | El registro (o una referencia) no existe |
| 409 | Regla de negocio violada (cupo lleno, duplicado, tiene dependencias…) |

## Postman

La colección está en `postman/collections/Gestion-Academica`. Ejecuta **Auth > Login**, copia el `token` en la variable `token` de la colección y las demás peticiones lo envían automáticamente.
