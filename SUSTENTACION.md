# Guía de sustentación: Gestión Académica

Todo lo que necesitas para defender el proyecto: qué hace, cómo está construido, qué hace cada clase, cada anotación y cada palabra clave, cómo viaja una petición de principio a fin y las preguntas que te pueden hacer, con su respuesta.

> **Cómo estudiarla:** lee primero las secciones 1 a 4 (te dan la idea completa). Después la 9 (clase por clase) con el código abierto al lado. Termina con la 17 (preguntas) en voz alta, como si te las hiciera el profesor.

---

## Índice

1. [El proyecto en 30 segundos](#1-el-proyecto-en-30-segundos)
2. [Tecnologías y por qué se usan](#2-tecnologías-y-por-qué-se-usan)
3. [Cómo se ejecuta](#3-cómo-se-ejecuta)
4. [Arquitectura por capas](#4-arquitectura-por-capas)
5. [Recorrido completo de una petición](#5-recorrido-completo-de-una-petición)
6. [Modelo de datos (base de datos)](#6-modelo-de-datos-base-de-datos)
7. [Glosario de anotaciones](#7-glosario-de-anotaciones)
8. [Glosario de Java: palabras clave y conceptos](#8-glosario-de-java-palabras-clave-y-conceptos)
9. [Clase por clase](#9-clase-por-clase)
10. [Seguridad: login, JWT, roles y CORS](#10-seguridad-login-jwt-roles-y-cors)
11. [Reglas de negocio](#11-reglas-de-negocio)
12. [Manejo de errores](#12-manejo-de-errores)
13. [Todos los endpoints](#13-todos-los-endpoints)
14. [Configuración (application.properties)](#14-configuración-applicationproperties)
15. [Pruebas automáticas](#15-pruebas-automáticas)
16. [Scripts de base de datos](#16-scripts-de-base-de-datos)
17. [Preguntas probables y respuestas](#17-preguntas-probables-y-respuestas)
18. [Limitaciones conocidas (y cómo responder si te las señalan)](#18-limitaciones-conocidas-y-cómo-responder-si-te-las-señalan)

---

## 1. El proyecto en 30 segundos

**Qué es:** una **API REST** (un servidor que recibe peticiones HTTP y responde JSON) para administrar una institución académica: carreras, semestres (niveles), materias, plan de estudios, grupos, profesores, estudiantes, inscripciones, notas, calificaciones parciales y asistencia. Además genera reportes: boletín del estudiante, estudiantes en riesgo y planilla de notas.

**Quién la usa:** un front en React (otro proyecto, en `C:\Users\Usuario\FRONEND-II`) que llama a esta API. El back no tiene pantallas: solo datos.

**Lo más importante que resuelve:**
- **Seguridad real en el servidor:** inicio de sesión con **JWT** y permisos por **rol** (ADMINISTRATIVO, COORDINADOR, PROFESOR, ESTUDIANTE). Un profesor solo califica sus materias; un estudiante solo ve lo suyo. Aunque alguien se salte el front, el servidor lo bloquea.
- **Reglas de negocio:** cupo máximo por grupo, no inscribir dos veces, no registrar asistencia futura, la definitiva se calcula sola como promedio de las calificaciones, etc.
- **Errores claros y en español**, con un código estable (`CUPO_LLENO`, `SIN_PERMISO`…).

**Frase para abrir la sustentación:**
> "Es una API REST en Java 21 con Spring Boot, organizada por capas (dominio, aplicación, infraestructura y presentación), que persiste en MySQL con JPA, protege cada endpoint con JWT y permisos por rol, y aplica las reglas académicas en la capa de servicios."

---

## 2. Tecnologías y por qué se usan

| Tecnología | Versión | Para qué | Por qué esta y no otra |
|---|---|---|---|
| **Java** | 21 (LTS) | Lenguaje | Versión de soporte largo; permite `record`, `switch` con flechas y *pattern matching* (`instanceof Jwt jwt`). |
| **Spring Boot** | 4.1.1 | Framework | Arranca un servidor web (Tomcat) embebido, configura casi todo solo (*autoconfiguración*) e inyecta dependencias. |
| **Maven** | (wrapper `mvnw`) | Construcción y dependencias | Descarga librerías del `pom.xml`, compila, corre pruebas y empaqueta. `mvnw` permite usarlo sin instalar Maven. |
| **Spring Web MVC** | — | Controladores REST | `@RestController`, `@GetMapping`, conversión automática a JSON. |
| **Spring Data JPA + Hibernate** | Hibernate 7 | Acceso a la BD | Se escriben interfaces y Spring genera el SQL (`findByEstudianteIdEstudiante`). Hibernate es el **ORM**: traduce objetos Java ↔ filas SQL. |
| **MySQL** | 8 | Base de datos | Relacional, con llaves foráneas e índices únicos que refuerzan las reglas. |
| **Spring Security** | 7 | Autenticación y autorización | Filtros que verifican el token y `@PreAuthorize` para permisos por método. |
| **OAuth2 Resource Server (Nimbus JOSE)** | — | JWT | Firma y valida tokens JWT con HS256 sin escribir criptografía a mano. |
| **BCrypt** | — | Contraseñas | Guarda un *hash* lento con sal; nunca la contraseña en texto. |
| **Bean Validation (Hibernate Validator)** | — | Validar datos de entrada | `@NotBlank`, `@Size`, `@Email`… en los DTO. |
| **Jackson** | 3 | JSON | Convierte objetos Java a JSON y viceversa automáticamente. |
| **Lombok** | 1.18 | Menos código repetido | Genera getters, setters, constructores y *builder* en compilación. |
| **H2** | — | BD en memoria para pruebas | Las pruebas no necesitan MySQL. |
| **JUnit 5 + MockMvc** | — | Pruebas | Simulan peticiones HTTP contra la app sin levantar un servidor real. |

**Palabras que debes poder definir:**
- **API REST:** interfaz donde cada recurso tiene una URL (`/api/estudiantes`) y se opera con verbos HTTP: `GET` (consultar), `POST` (crear), `PUT` (actualizar), `DELETE` (borrar). Es *stateless*: cada petición trae todo lo necesario (el token).
- **JSON:** formato de texto para intercambiar datos: `{"idEstudiante": "EST001", "activo": true}`.
- **Framework:** código ya hecho que pone la estructura; tú llenas las partes específicas.
- **Inyección de dependencias:** Spring crea los objetos (*beans*) y se los pasa a quien los necesita por el constructor. Así ninguna clase hace `new` de sus dependencias.
- **ORM:** *Object-Relational Mapping*, mapear clases a tablas.
- **Endpoint:** una URL + un verbo HTTP que la API atiende (`POST /api/notas`).

---

## 3. Cómo se ejecuta

1. **MySQL:** ejecutar `db/schema.sql` (crea la BD `sistema_academico` y sus tablas).
2. **Configuración local:** `src/main/resources/application-local.properties` (no se sube a git) con la contraseña de MySQL, el secreto JWT y la contraseña del admin.
3. **Arrancar:** `./mvnw spring-boot:run` o el botón *Run* de `GestionAcademicaApplication` en IntelliJ. Queda en **http://localhost:8080**.
4. **Pruebas:** `./mvnw test` (usan H2, no tocan MySQL).
5. **Primer usuario:** si no existe ningún ADMINISTRATIVO, al arrancar se crea `admin` con la contraseña de `app.admin.password` (clase `AdminInicialRunner`).

**Datos de demostración cargados:** usuarios `demo_est0001…`, `demo_prof001…`, `demo_coord001…`, `demo_admin01…` con contraseña `Demo1234*` (ver sección 16).

---

## 4. Arquitectura por capas

El código está en `src/main/java/com/example/gestionacademica/` dividido en cuatro paquetes. Es una **arquitectura por capas con puertos y adaptadores** (inspirada en la arquitectura hexagonal / limpia):

```
presentation/      ← Habla HTTP: controladores, DTOs, manejo de errores, CORS
     │  llama a
application/       ← Reglas de negocio: servicios (@Service, @Transactional) y excepciones
     │  usa interfaces de
domain/            ← El corazón: entidades (tablas) y repositorios como INTERFACES (puertos)
     ▲  implementadas por
infrastructure/    ← Detalles técnicos: adaptadores JPA (MySQL) y seguridad (JWT, permisos)
```

### Qué va en cada capa

| Capa | Paquete | Contiene | Regla |
|---|---|---|---|
| **Dominio** | `domain/models`, `domain/repository` | Entidades (`Estudiante`, `Nota`…), enums (`Rol`, `Genero`…) e **interfaces** de repositorio (`NotaRepository`) | No sabe nada de HTTP ni de cómo se guarda. |
| **Aplicación** | `application/Services`, `application/Exceptions` | Interfaces de servicio (`NotaServices`), sus implementaciones (`NotaServicesImpl`) y excepciones de negocio | Aquí viven las reglas: cupo, duplicados, promedios. |
| **Infraestructura** | `infrastructure/persistence`, `infrastructure/security` | `XxxJpaRepository` (Spring Data), `XxxRepositoryAdapter` (implementa el puerto del dominio), configuración de seguridad y JWT | Es lo que se podría cambiar (otra BD, otro tipo de token) sin tocar las reglas. |
| **Presentación** | `presentation/controller`, `presentation/dto`, `presentation/mapper`, `presentation/config` | Controladores REST, DTOs (`record`), `Referencias`, `CorsConfig`, `ApiExceptionHandler` | Traduce HTTP/JSON ↔ objetos y llama a los servicios. |

### Por qué hay "puerto" y "adaptador" en los repositorios

Para cada tabla hay **tres piezas**:

1. `domain/repository/NotaRepository` → **interfaz** con lo que el negocio necesita (`findById`, `findByEstudianteIdEstudiante`…). Es el **puerto**.
2. `infrastructure/persistence/NotaJpaRepository` → interfaz que **extiende `JpaRepository<Nota, Long>`**; Spring Data genera su implementación y el SQL automáticamente.
3. `infrastructure/persistence/NotaRepositoryAdapter` → clase `@Repository` que **implementa el puerto** delegando en el `JpaRepository`. Es el **adaptador**.

Los servicios solo conocen el puerto (1). **Ventaja:** si mañana se cambia JPA por otra tecnología, solo se reescribe el adaptador; los servicios no se tocan. También facilita probar los servicios con repositorios falsos.

### Por qué hay interfaz + implementación en los servicios

`NotaServices` (interfaz) y `NotaServicesImpl` (implementación). El controlador depende de la interfaz → **bajo acoplamiento** (principio de inversión de dependencias, la "D" de SOLID). Spring inyecta la implementación.

### Por qué DTOs y no entidades en los controladores

- **Seguridad:** nunca se devuelve `passwordHash` ni datos internos.
- **Respuestas planas:** `NotaResponse` trae `idEstudiante` y `nombreEstudiante`, no un objeto `estudiante` anidado (que además causaría problemas con proxies *lazy* de Hibernate y ciclos infinitos al convertir a JSON).
- **Contrato estable:** se puede cambiar la entidad sin romper el JSON que consume el front.
- **Entrada controlada:** el cliente manda solo ids (`"idMateria": "MAT001"`); el campo `aprobada` ni siquiera se recibe porque lo calcula el servidor.

---

## 5. Recorrido completo de una petición

Ejemplo: un profesor registra una calificación.

```http
POST http://localhost:8080/api/movimiento-notas
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{ "fechaRegistro": "2026-03-10", "calificacion": 4.0, "idNota": 1 }
```

| # | Dónde | Qué pasa |
|---|---|---|
| 1 | **Tomcat** (servidor embebido) | Recibe la petición en el puerto 8080. |
| 2 | **CorsFilter** (Spring Security, `.cors()` + `CorsConfig`) | Si viene de otro origen (el front en `localhost:5173`), revisa que esté permitido y agrega cabeceras CORS. Si es un *preflight* `OPTIONS`, responde aquí mismo con 200. |
| 3 | **BearerTokenAuthenticationFilter** (OAuth2 Resource Server) | Lee `Authorization: Bearer …`, valida firma HS256 y vencimiento con el `JwtDecoder` de `SecurityConfig`. Si falla → `ErroresSeguridadHandler.commence` → **401 `TOKEN_INVALIDO`**. |
| 4 | **JwtAuthenticationConverter** (`SecurityConfig`) | Convierte el claim `rol: "PROFESOR"` en la autoridad `ROLE_PROFESOR` y guarda la autenticación en el `SecurityContext`. |
| 5 | **AuthorizationFilter** | Regla de URL: `anyRequest().authenticated()` → está autenticado, pasa. (Para `/api/usuarios/**` exigiría `ROLE_ADMINISTRATIVO`). |
| 6 | **DispatcherServlet** (Spring MVC) | Busca qué método atiende `POST /api/movimiento-notas` → `MovimientoNotaController.crear`. |
| 7 | **Jackson + Bean Validation** | Convierte el JSON en `MovimientoNotaRequest` (un `record`) y, por `@Valid`, revisa `@NotNull`, `@DecimalMin(0.0)`, `@DecimalMax(5.0)`. Si falla → **400 `VALIDACION`** con `errores: {campo: mensaje}`. |
| 8 | **@PreAuthorize** (seguridad por método) | Evalúa `@acceso.puedeModificarNota(authentication, #request.idNota())` → `AccesoService` busca la nota 1, ve que es de MAT001 y consulta si el profesor del token (`idProfesor`) tiene asignada MAT001. Si no → **403 `SIN_PERMISO`**. |
| 9 | **Controlador** | `request.toEntity(referencias)` convierte el DTO en entidad; `Referencias.nota(1)` carga la nota real (404 si no existe). |
| 10 | **Servicio** `MovimientoNotaServicesImpl.crear` (`@Transactional`) | Abre una transacción. Guarda el movimiento y llama a `notaServices.recalcularDefinitiva(1)`: promedio de todas las calificaciones de esa nota, redondeado a 2 decimales; `aprobada = definitiva >= 3.0`. |
| 11 | **Repositorio** (puerto → adaptador → `JpaRepository`) | `save(...)` → Hibernate genera `INSERT INTO movimiento_notas ...` y `UPDATE notas SET definitiva=..., aprobada=...`. |
| 12 | **Fin de la transacción** | *Commit*: los dos cambios quedan guardados juntos. Si algo hubiera fallado, *rollback* de ambos. |
| 13 | **Controlador** | `MovimientoNotaResponse.from(guardado)` arma el DTO de salida; `ResponseEntity.status(201)`. |
| 14 | **Jackson** | Convierte el `record` a JSON. Respuesta **201 Created**. |

Si en cualquier paso se lanza una excepción (por ejemplo `NotaNotFoundException`), la atrapa `ApiExceptionHandler` y devuelve el JSON de error en español (sección 12).

---

## 6. Modelo de datos (base de datos)

### Tablas y relaciones

```
carreras 1──* niveles 1──* grupos 1──* inscripciones *──1 estudiantes
                 │            │                               │  │
                 │            ├──* asistencia *───────────────┘  │
                 │            │                                  │
   niveles_materias (plan)    └──* asignaciones_materia_profesor *──1 profesores
     *        *                              │
   niveles  materias 1──────────────────────┘ (materia)
                 │
                 └──* notas *──1 estudiantes
                        │
                        └──* movimiento_notas (calificaciones parciales)

usuarios ──0..1 estudiantes   usuarios ──0..1 profesores
```

| Tabla | Llave primaria | Qué representa | Relaciones / restricciones |
|---|---|---|---|
| `carreras` | `ID_Carrera` (texto) | Programa académico | — |
| `niveles` | `ID_Nivel` | Semestre de una carrera | FK `ID_Carrera` |
| `materias` | `ID_Materia` | Asignatura | — |
| `niveles_materias` | `ID_Nivel_Materia` (auto) | **Plan de estudios**: qué materias tiene cada nivel | FK nivel y materia; **único (nivel, materia)** |
| `grupos` | `ID_Grupo` | Curso concreto de un nivel (ej. "1A Diurno") | FK nivel; `Cupo_Maximo` (NULL = sin límite) |
| `profesores` | `ID_Profesor` | Docente | **correo único** |
| `asignaciones_materia_profesor` | `ID_Asignacion` (auto) | Qué profesor dicta qué materia en qué grupo | FK profesor, grupo, materia; **único (grupo, materia)** → un solo profesor por materia en cada grupo |
| `estudiantes` | `ID_Estudiante` | Alumno | **correo único**; `Estado` (Activo, Retirado, Graduado, Suspendido) |
| `inscripciones` | `ID_Inscripcion` (auto) | Estudiante en un grupo un año | FK estudiante, grupo; **único (estudiante, grupo, año)** |
| `asistencia` | `ID_Asistencia` (auto) | Un día de clase de un estudiante en un grupo | FK estudiante, grupo; **único (estudiante, grupo, fecha)** |
| `notas` | `ID_Nota` (auto) | La nota de un estudiante en una materia | FK estudiante, materia; `Definitiva` NULL = en curso; `Aprobada` la calcula la app |
| `movimiento_notas` | `ID_Movimiento` (auto) | Cada calificación parcial (corte) | FK nota; la definitiva es su promedio |
| `usuarios` | `ID_Usuario` (auto) | Cuenta de acceso | `Username` único; `ID_Estudiante` único; `ID_Profesor` único; `Password_Hash` BCrypt |

**Preguntas típicas sobre el modelo:**
- *¿Por qué los ids de carrera, estudiante, etc. son texto y los de nota o inscripción son números autoincrementales?* Los primeros son códigos que la institución ya usa (documento, código de materia); los segundos son registros de movimiento que no tienen código natural, así que la BD los genera (`AUTO_INCREMENT` ↔ `@GeneratedValue(strategy = IDENTITY)`).
- *¿Por qué `notas` y `movimiento_notas` separadas?* Una nota es "Juan en Cálculo I"; los movimientos son sus cortes (4.0, 3.5, 4.2). La definitiva se deriva de ellos. Así se conserva el historial de cada corte.
- *¿Por qué `niveles_materias` es una tabla aparte?* Es una relación **muchos a muchos** (un nivel tiene varias materias y una materia puede estar en varios niveles). Se resuelve con una tabla intermedia.
- *¿Dónde se garantiza "no inscribir dos veces"?* En dos lugares: el servicio lo valida (mensaje claro, 409 `INSCRIPCION_DUPLICADA`) y la BD tiene el índice único como última barrera (si se colara, saldría 409 `INTEGRIDAD_DATOS`).

---

## 7. Glosario de anotaciones

Una **anotación** (`@Algo`) es una etiqueta que se pone sobre una clase, método, campo o parámetro. No ejecuta nada por sí misma: el compilador, Lombok o Spring la leen y actúan en consecuencia.

### Spring Boot / núcleo

| Anotación | Dónde está | Qué hace |
|---|---|---|
| `@SpringBootApplication` | `GestionAcademicaApplication` | Combina `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan`: marca la clase principal, activa la autoconfiguración y busca componentes en este paquete y subpaquetes. |
| `@Component` | `AccesoService` (`@Component("acceso")`), `ErroresSeguridadHandler`, `Referencias`, `AdminInicialRunner` | "Spring, crea un objeto de esta clase y adminístralo" (un *bean*). El nombre `"acceso"` permite usarlo en SpEL como `@acceso`. |
| `@Service` | Todos los `XxxServicesImpl`, `JwtTokenServices` | Igual que `@Component`, pero indica que es lógica de negocio. |
| `@Repository` | Todos los `XxxRepositoryAdapter` | `@Component` para acceso a datos; además traduce excepciones de BD a `DataAccessException` de Spring. |
| `@Configuration` | `SecurityConfig`, `CorsConfig` | Clase que declara *beans* con métodos `@Bean`. |
| `@Bean` | Métodos de `SecurityConfig` y `CorsConfig` | El objeto que devuelve el método queda registrado como bean (ej. `PasswordEncoder`, `JwtDecoder`, `CorsConfigurationSource`). |
| `@Value("${app.jwt.secret}")` | `SecurityConfig`, `JwtTokenServices`, `AdminInicialRunner` | Inyecta un valor de `application.properties`. |
| `@Override` | Métodos que implementan una interfaz | Indica al compilador que se sobrescribe un método; si no existe en la interfaz, da error. |

### Web (controladores)

| Anotación | Qué hace |
|---|---|
| `@RestController` | Controlador cuyos métodos devuelven datos (JSON), no vistas. Es `@Controller` + `@ResponseBody`. |
| `@RequestMapping("/api/notas")` | Prefijo de URL para todos los métodos de la clase. |
| `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` | Atiende ese verbo HTTP en esa ruta. `@GetMapping("/{id}")` → `/api/notas/5`. |
| `@PathVariable` | Toma un valor de la ruta: en `/api/notas/{id}`, `@PathVariable Long id` recibe 5. |
| `@RequestParam` | Toma un parámetro de la URL: `?anio=2026`. `required = false` lo hace opcional. |
| `@RequestBody` | Convierte el JSON del cuerpo en un objeto Java (con Jackson). |
| `@Valid` | Ejecuta las validaciones del objeto (Bean Validation). Si fallan → `MethodArgumentNotValidException` → 400. |
| `@Validated(AlCrear.class)` | Como `@Valid`, pero activando un **grupo de validación**: en el POST se exigen campos (como el id) que en el PUT no, porque en el PUT el id viene en la URL. |
| `@RestControllerAdvice` | `ApiExceptionHandler`: clase que atiende excepciones de todos los controladores. |
| `@ExceptionHandler(X.class)` | El método atiende las excepciones de tipo X y arma la respuesta. |

### Seguridad

| Anotación | Qué hace |
|---|---|
| `@EnableMethodSecurity` | En `SecurityConfig`: activa `@PreAuthorize` en los métodos. |
| `@PreAuthorize("...")` | Antes de ejecutar el método evalúa la expresión (SpEL). Si da `false` → `AccessDeniedException` → 403. Ejemplos: `hasRole('ADMINISTRATIVO')`, `@acceso.esGestor(authentication)`, `@acceso.puedeCalificarMateria(authentication, #request.idMateria())`. `#request` es el parámetro del método; `authentication` es el usuario actual. |

### Transacciones

| Anotación | Qué hace |
|---|---|
| `@Transactional` | Todo el método corre en una transacción: si termina bien → *commit*; si lanza una excepción no controlada (`RuntimeException`) → *rollback* (se deshace todo). Puesto sobre la clase aplica a todos sus métodos públicos. |
| `@Transactional(readOnly = true)` | Transacción de solo lectura (reportes, `AuthServicesImpl`, `AccesoService`): Hibernate no revisa cambios y la BD puede optimizar. |

### JPA (entidades)

| Anotación | Qué hace |
|---|---|
| `@Entity` | La clase es una tabla. |
| `@Table(name = "notas")` | Nombre exacto de la tabla. |
| `@Id` | Llave primaria. |
| `@GeneratedValue(strategy = GenerationType.IDENTITY)` | El id lo genera la BD (`AUTO_INCREMENT`). |
| `@Column(name = "Fecha_Registro", length = 50, nullable = false, unique = true)` | Columna real, longitud, si admite NULL, si es única. |
| `@ManyToOne(fetch = FetchType.LAZY)` | Relación muchos-a-uno (muchas notas → un estudiante). `LAZY`: el estudiante no se carga de la BD hasta que se use (`nota.getEstudiante().getNombre...`). Evita traer datos innecesarios. |
| `@JoinColumn(name = "ID_Estudiante")` | Columna que guarda la llave foránea. |
| `@Enumerated(EnumType.STRING)` | Guarda el enum como texto (`'ESTUDIANTE'`), no como número. Si se reordena el enum, los datos no se corrompen. |

### Lombok (genera código al compilar)

| Anotación | Genera |
|---|---|
| `@Getter` / `@Setter` | `getNombre()`, `setNombre(...)` para cada campo. |
| `@NoArgsConstructor` | Constructor vacío (JPA lo exige para crear objetos al leer de la BD). |
| `@AllArgsConstructor` | Constructor con todos los campos (lo necesita `@Builder`). |
| `@Builder` | Patrón *builder*: `Nota.builder().fechaRegistro(f).estudiante(e).build()`. Legible cuando hay muchos campos. |

### Validación (Bean Validation)

| Anotación | Regla |
|---|---|
| `@NotNull` | No puede ser `null`. |
| `@NotBlank` | Texto no nulo y con al menos un carácter que no sea espacio. |
| `@Size(min, max)` | Longitud del texto. |
| `@Email` | Formato de correo. |
| `@PastOrPresent` | Fecha de hoy o anterior (fecha de ingreso). |
| `@Min(1)` | Número ≥ 1 (cupo máximo). |
| `@DecimalMin("0.0")` / `@DecimalMax("5.0")` | Rango de la nota. |
| `message = "..."` | Mensaje en español que sale en `errores`. |
| `groups = AlCrear.class` | La regla solo se aplica en el grupo `AlCrear` (el POST). |

### Pruebas

| Anotación | Qué hace |
|---|---|
| `@SpringBootTest` | Levanta la aplicación completa para la prueba. |
| `@AutoConfigureMockMvc` | Prepara `MockMvc` para simular peticiones HTTP. |
| `@Sql(scripts = "/datos-prueba.sql", executionPhase = BEFORE_TEST_CLASS)` | Carga datos antes de las pruebas de la clase. |
| `@Autowired` | Inyecta un bean en la prueba (en el código de producción se usa inyección por constructor). |
| `@Test` | Marca un método como caso de prueba. |

---

## 8. Glosario de Java: palabras clave y conceptos

### Palabras clave

| Palabra | Significado | Ejemplo en el proyecto |
|---|---|---|
| `package` | Carpeta lógica a la que pertenece la clase | `package com.example.gestionacademica.domain.models;` |
| `import` | Usar una clase de otro paquete sin escribir su nombre completo | `import java.util.List;` |
| `public` | Visible desde cualquier lugar | `public class Nota` |
| `private` | Visible solo dentro de la clase | `private final NotaRepository notaRepository;` |
| `protected` | Visible en el paquete y en subclases | (no se usa) |
| `class` | Define una clase (molde de objetos) | `public class NotaServicesImpl` |
| `interface` | Contrato: métodos sin implementación que otra clase debe implementar | `public interface NotaServices` |
| `implements` | La clase cumple una interfaz | `NotaServicesImpl implements NotaServices` |
| `extends` | Herencia (clase) o extensión (interfaz) | `NotaJpaRepository extends JpaRepository<Nota, Long>`, `BusinessException extends RuntimeException` |
| `enum` | Tipo con valores fijos | `enum Rol { ESTUDIANTE, PROFESOR, COORDINADOR, ADMINISTRATIVO }` |
| `record` | Clase inmutable de solo datos: genera constructor, getters (`request.idMateria()`), `equals`, `hashCode`, `toString` | Todos los DTO: `record NotaRequest(...)` |
| `final` (campo) | Se asigna una sola vez (en el constructor) | `private final NotaRepository notaRepository;` |
| `final` (clase) | No se puede heredar | `public final class NotaDtos`, `CuerpoError` |
| `static` | Pertenece a la clase, no a cada objeto | `NotaResponse.from(n)`, `CuerpoError.crear(...)` |
| `static final` | Constante | `private static final float NOTA_MINIMA_APROBACION = 3.0f;` |
| `void` | El método no devuelve nada | `public void eliminar(Long id)` |
| `return` | Devuelve un valor y termina el método | `return notaRepository.save(nota);` |
| `new` | Crea un objeto | `new LinkedHashMap<>()` |
| `this` | El objeto actual; distingue campo de parámetro | `this.notaRepository = notaRepository;` |
| `throw` | Lanza una excepción | `throw new BusinessException("CUPO_LLENO", "...")` |
| `throws` | Declara que un método puede lanzar una excepción verificada | `throws IOException` en `ErroresSeguridadHandler` |
| `try / catch` | Atrapa excepciones | `AdminInicialRunner` atrapa `DataAccessException` si la tabla no existe |
| `if / else` | Condición | — |
| `for (X x : lista)` | Recorre una colección | `for (Nota nota : notas)` |
| `switch ... ->` | Elige según un valor (forma moderna con flecha) | `CuerpoError.descripcion` |
| `instanceof Jwt jwt` | Pregunta el tipo y, si coincide, crea la variable (*pattern matching*) | `AccesoService.claim` |
| `null` | Ausencia de valor | `definitiva == null` → en curso |
| `true / false` | Booleanos | — |
| `super(message)` | Llama al constructor de la clase padre | Excepciones |

### Tipos que aparecen

| Tipo | Qué es |
|---|---|
| `String` | Texto. |
| `Long`, `Integer`, `Float`, `Double`, `Boolean` | Versiones objeto de los primitivos; **pueden ser `null`** (por eso `Definitiva` es `Float` y no `float`: null = en curso). |
| `LocalDate` | Fecha sin hora (`2026-03-10`). |
| `LocalDateTime` | Fecha con hora (el `timestamp` de los errores). |
| `Instant` | Momento exacto en UTC (vencimiento del token). |
| `List<T>` | Lista ordenada. |
| `Set<T>` | Conjunto sin repetidos; búsqueda rápida con `contains` (en `AccesoService`). |
| `Map<K, V>` / `LinkedHashMap` | Pares clave-valor; `LinkedHashMap` **conserva el orden de inserción**, por eso el JSON sale con los campos en el orden escrito. |
| `Optional<T>` | Caja que puede tener un valor o estar vacía; evita `null`. `findById(id).orElseThrow(() -> new XNotFoundException(...))`. |
| `ResponseEntity<T>` | Respuesta HTTP con código de estado, cabeceras y cuerpo. |
| `Function<T, String>` | Una función que recibe T y devuelve String; se pasa como parámetro (`n -> n.getMateria().getIdMateria()`). |

### Conceptos

- **Clase vs objeto:** la clase es el molde (`Estudiante`); el objeto es una instancia concreta (Juan Pérez).
- **Encapsulamiento:** campos `private` y acceso por getters/setters.
- **Herencia:** `BusinessException extends RuntimeException`.
- **Polimorfismo:** el controlador usa `NotaServices` (interfaz) y en ejecución recibe `NotaServicesImpl`.
- **Abstracción:** las interfaces de repositorio esconden si se usa MySQL, H2 o lo que sea.
- **Genéricos `<T>`:** código que sirve para cualquier tipo. `JpaRepository<Nota, Long>` = repositorio de `Nota` con id `Long`. `soloMateriasAsignadas(Authentication, List<T>, Function<T,String>)` sirve para notas y movimientos.
- **Lambda:** función corta sin nombre. `() -> new NotaNotFoundException("...")`, `n -> n.getMateria().getIdMateria()`.
- **Referencia a método:** `NotaResponse::from` equivale a `n -> NotaResponse.from(n)`.
- **Streams:** `lista.stream().filter(...).map(...).toList()` procesa colecciones de forma declarativa.
- **Excepción verificada vs no verificada:** `RuntimeException` (no verificada) no obliga a `try/catch`; además, `@Transactional` hace *rollback* automático con ellas. Por eso todas nuestras excepciones extienden `RuntimeException`.
- **Inyección por constructor:** cada clase recibe sus dependencias en el constructor y las guarda en campos `final`. Ventajas: inmutables, obligatorias, fáciles de probar.

---

## 9. Clase por clase

### 9.1 Arranque

**`GestionAcademicaApplication`**: clase principal. `main` llama a `SpringApplication.run(...)`, que levanta Spring, Tomcat en el puerto 8080, la conexión a MySQL y todos los beans.

### 9.2 Dominio: `domain/models`

Cada entidad es una tabla (ver sección 6). Todas usan Lombok (`@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder`) y validaciones.

| Clase | Campos clave | Notas |
|---|---|---|
| `Carrera` | `idCarrera`, `nombreCarrera` | |
| `Nivel` | `idNivel`, `nombreNivel`, `carrera` (`@ManyToOne`) | Semestre. |
| `Materia` | `idMateria`, `nombreMateria` | |
| `NivelMateria` | `idNivelMateria`, `nivel`, `materia` | Plan de estudios. |
| `Grupo` | `idGrupo`, `nombreGrupo`, `nivel`, `cupoMaximo` | `cupoMaximo` null = sin límite. |
| `Profesor` | `idProfesor`, `nombreProfesor`, `fechaIngreso`, `genero`, `celular`, `correo`, `activo`, `especializacion` | |
| `AsignacionMateriaProfesor` | `profesor`, `grupo`, `materia` | Base de los permisos del profesor. |
| `Estudiante` | `idEstudiante`, `nombreEstudiante`, `fechaIngreso`, `genero`, `celular`, `correo`, `activo`, `estado` | |
| `Inscripcion` | `anioInscripcion`, `cohorte`, `estudiante`, `grupo` | |
| `Asistencia` | `fechaAsistencia`, `asistio`, `justificada`, `observacion`, `estudiante`, `grupo` | `justificada` solo aplica si no asistió. |
| `Nota` | `definitiva`, `fechaRegistro`, `aprobada`, `estudiante`, `materia` | `definitiva` y `aprobada` null = en curso. |
| `MovimientoNota` | `fechaRegistro`, `calificacion`, `nota` | Corte parcial. |
| `Usuario` | `username`, `passwordHash`, `rol`, `activo`, `estudiante`, `profesor` | Cuenta de acceso. |
| `Rol` (enum) | ESTUDIANTE, PROFESOR, COORDINADOR, ADMINISTRATIVO | |
| `Genero` (enum) | M, F, Otro | |
| `EstadoEstudiante` (enum) | Activo, Retirado, Graduado, Suspendido | |

### 9.3 Dominio: `domain/repository` (puertos)

Una interfaz por entidad con los métodos que el negocio necesita: `save`, `findById`, `findAll`, `existsById`, `deleteById` y consultas específicas como:
- `InscripcionRepository.countByGrupoIdGrupoAndAnioInscripcion` → cuántos inscritos tiene un grupo en un año (para el cupo).
- `AsignacionMateriaProfesorRepository.existsByProfesorIdProfesorAndMateriaIdMateria` → ¿este profesor dicta esta materia? (para permisos).
- `UsuarioRepository.countByRolAndActivoTrue` → cuántos ADMINISTRATIVOS activos hay.

### 9.4 Infraestructura: `infrastructure/persistence`

- **`XxxJpaRepository extends JpaRepository<Entidad, TipoId>`**: Spring Data genera la implementación. Los métodos con nombre especial (**consultas derivadas**) se convierten en SQL:
  - `findByEstudianteIdEstudiante(String id)` → `SELECT * FROM notas WHERE ID_Estudiante = ?` (navega `estudiante.idEstudiante`).
  - `existsByEstudianteIdEstudianteAndGrupoIdGrupoAndFechaAsistencia(...)` → `SELECT COUNT(*) > 0 ... WHERE ... AND ... AND ...`.
  - `countByRolAndActivoTrue(Rol rol)` → `SELECT COUNT(*) FROM usuarios WHERE Rol = ? AND Activo = true`.
  - `JpaRepository` ya trae `save`, `findById`, `findAll`, `deleteById`, `existsById`, `count`.
- **`XxxRepositoryAdapter implements XxxRepository`** (`@Repository`): delega cada método al `JpaRepository`. Es el adaptador del puerto.

### 9.5 Aplicación: excepciones (`application/Exceptions`)

| Excepción | HTTP | Cuándo |
|---|---|---|
| `XxxNotFoundException` (13 clases: Carrera, Nivel, Materia, NivelMateria, Profesor, Grupo, Asignacion…, Estudiante, Inscripcion, Asistencia, Nota, MovimientoNota, Usuario) | 404 | El registro no existe. |
| `BusinessException(codigo, mensaje)` | 409 | Se incumple una regla de negocio (cupo lleno, duplicado, tiene dependencias…). Trae un `codigo` estable. |
| `CredencialesInvalidasException` | 401 | Usuario o contraseña incorrectos. |
| `CuentaDesactivadaException` | 403 | La clave es correcta pero la cuenta está inactiva. |

Todas extienden `RuntimeException` → no obligan a `try/catch` y provocan *rollback* en `@Transactional`.

### 9.6 Aplicación: servicios (`application/Services` + `impl`)

Patrón común en cada servicio: `crear`, `obtenerPorId` (404 si no existe), `obtenerTodos`, `actualizar`, `eliminar` (404 si no existe, 409 si tiene dependencias).

| Servicio | Lo importante |
|---|---|
| `CarreraServicesImpl` | No crear id repetido (`YA_EXISTE`). No borrar si tiene niveles (`TIENE_DEPENDENCIAS`). |
| `NivelServicesImpl` | No borrar si tiene grupos o materias en el plan. |
| `MateriaServicesImpl` | No borrar si está en planes, asignaciones o notas. |
| `NivelMateriaServicesImpl` | No repetir la misma materia en el mismo nivel (`MATERIA_YA_ASOCIADA`). |
| `GrupoServicesImpl` | No borrar si tiene inscripciones, asignaciones o asistencias. |
| `ProfesorServicesImpl` | Correo único (`CORREO_DUPLICADO`); al actualizar solo revisa el correo si cambió. No borrar si tiene asignaciones. |
| `EstudianteServicesImpl` | Correo único. No borrar si tiene inscripciones, notas o asistencias. |
| `AsignacionMateriaProfesorServicesImpl` | `validar`: (1) profesor existe y está **activo**; (2) la materia pertenece al **plan del nivel** del grupo; (3) **un solo profesor por materia en cada grupo**. |
| `InscripcionServicesImpl` | `validar`: (1) estudiante activo; (2) no inscrito dos veces en el mismo grupo y año; (3) **cupo**: `inscritos >= cupoMaximo` → `CUPO_LLENO`. `consultarCupo` devuelve cupo, inscritos y disponibles. Al actualizar solo revalida si cambia estudiante, grupo o año. |
| `AsistenciaServicesImpl` | `validar`: (1) fecha no futura; (2) estudiante inscrito en el grupo; (3) un registro por estudiante-grupo-fecha. `validarJustificacion`: solo se justifica una falta. `calcularResumen`: total, asistidas, inasistencias, justificadas, porcentaje (2 decimales). |
| `NotaServicesImpl` | `validarDuplicado`: no dos notas **en curso o aprobadas** de la misma materia (si la reprobó puede repetirla). `recalcularDefinitiva`: promedio de los movimientos, redondeado a 2 decimales; sin movimientos → null. `calcularAprobada`: `definitiva >= 3.0` (null si no hay definitiva). `calcularPromedio`: promedio del estudiante, aprobadas y reprobadas. No borrar una nota con calificaciones. |
| `MovimientoNotaServicesImpl` | Cada vez que se crea, edita o borra una calificación llama a `recalcularDefinitiva`. Si se mueve una calificación a otra nota, recalcula ambas. |
| `ReporteServicesImpl` (`readOnly`) | `boletin`: datos del estudiante + grupos + materias con estado + promedio + asistencia. `estudiantesEnRiesgo`: inscritos del grupo con **promedio < 3.0** o **asistencia < 80 %**, con los motivos. `planillaNotas`: todos los inscritos del grupo con su nota en la materia (si repitió, la más reciente), aprobados, reprobados y promedio del grupo. |
| `AuthServicesImpl` | `login`: busca el usuario; si no existe o la clave no coincide → 401 con el **mismo mensaje** (no revela cuál falló); si la clave es correcta pero está inactivo → 403 "Tu cuenta está desactivada"; si todo va bien genera el token. |
| `UsuarioServicesImpl` | Username único; `validarVinculo` (ESTUDIANTE con `idEstudiante`, PROFESOR con `idProfesor`, los demás sin vínculo; un solo usuario por estudiante/profesor); no borrarse ni desactivarse a sí mismo (`CUENTA_PROPIA`); no dejar el sistema sin ADMINISTRATIVOS activos (`ULTIMO_ADMINISTRATIVO`); guarda la clave con BCrypt. |
| `Sesion` (record) | Resultado del login: `token`, `expiraEn`, `usuario`. |
| `TokenServices` (interfaz) | `generar(Usuario)`; la implementa `JwtTokenServices` en infraestructura. Así la capa de aplicación no depende de JWT. |

### 9.7 Infraestructura: seguridad (`infrastructure/security`)

| Clase | Qué hace |
|---|---|
| `SecurityConfig` | Define la **cadena de filtros**: `.cors()`, CSRF desactivado, sesión `STATELESS`, `POST /api/auth/login` público, `/api/usuarios/**` solo ADMINISTRATIVO, todo lo demás autenticado; valida JWT con `oauth2ResourceServer().jwt()`. Beans: `PasswordEncoder` (BCrypt), `SecretKey` (exige ≥ 32 caracteres), `JwtEncoder`, `JwtDecoder` (HS256) y el convertidor `rol` → `ROLE_<rol>`. |
| `JwtTokenServices` | Crea el token: `issuer`, `issuedAt`, `expiresAt` (+480 min), `subject` (username) y claims `idUsuario`, `rol`, `idEstudiante` o `idProfesor`. Firma HS256. |
| `AccesoService` (`@Component("acceso")`) | **Reglas de acceso que dependen de los datos.** Lee los claims del token. Métodos: `esPersonal` (todo menos ESTUDIANTE), `esGestor` (ADMINISTRATIVO o COORDINADOR), `puedeVerEstudiante`, `puedeVerNota`, `puedeCalificarMateria`, `puedeModificarNota`, `puedeModificarMovimiento`, `puedeGestionarGrupo`, `puedeModificarAsistencia`, `puedeVerPlanilla`. Y para listados: `soloMateriasAsignadas`, `soloGruposAsignados`, `soloEstudiantesDeSusGrupos` (gestor → todo; profesor → solo lo suyo). `idUsuario(auth)` lee el id del token. Si el registro consultado no existe devuelve `true` para que el servicio responda 404 en vez de 403. |
| `ErroresSeguridadHandler` | Responde los 401/403 que ocurren **en los filtros** (antes de llegar al controlador) con el mismo JSON que `ApiExceptionHandler`. Distingue `NO_AUTENTICADO` (sin token) de `TOKEN_INVALIDO`. |
| `AdminInicialRunner` (`ApplicationRunner`) | Al arrancar, si no hay ningún ADMINISTRATIVO crea `admin`. Sin él nadie podría crear cuentas. |

### 9.8 Presentación

| Clase | Qué hace |
|---|---|
| `XxxController` (16) | Reciben la petición, validan el DTO, verifican permisos con `@PreAuthorize`, llaman al servicio y devuelven el DTO de respuesta. No contienen reglas de negocio. Los `POST` devuelven **201 Created**; los `DELETE`, **204 No Content**. |
| `AuthController` | `POST /api/auth/login` (público) y `GET /api/auth/me` (datos del dueño del token). |
| `UsuarioController` | `@PreAuthorize("hasRole('ADMINISTRATIVO')")` a nivel de clase. Pasa a los servicios el `idUsuario` del token para impedir que alguien se borre a sí mismo. |
| `ReporteController` | Boletín, en riesgo y planilla. Si no se envía `anio`, usa el año actual. |
| `XxxDtos` (final, constructor privado) | Agrupan los `record` de cada recurso: `XxxRequest` (entrada, con validaciones y `toEntity(Referencias)`) y `XxxResponse` (salida, con `from(entidad)`). |
| `AlCrear` | Interfaz-marcador para el grupo de validación del POST. Extiende `Default` para que también corran las reglas normales. |
| `CuerpoError` | Arma el JSON de error: `timestamp, status, error, codigo, message`. `descripcion(status)` traduce el código HTTP al español. |
| `ApiExceptionHandler` | `@RestControllerAdvice`: convierte cada excepción en su respuesta (sección 12). Nunca expone el detalle interno de un 500 (queda en el log). |
| `Referencias` (`presentation/mapper`) | Convierte los ids que llegan en los DTO en entidades reales usando los servicios (404 si no existen). Así las respuestas salen con los nombres completos. |
| `CorsConfig` | Bean `CorsConfigurationSource`: orígenes `localhost:5173-5175`, métodos GET/POST/PUT/DELETE/OPTIONS, cabeceras `Authorization` y `Content-Type`, caché del preflight 1 hora. |

---

## 10. Seguridad: login, JWT, roles y CORS

### 10.1 Autenticación vs autorización

- **Autenticación:** *¿quién eres?* → login con usuario y contraseña → token.
- **Autorización:** *¿qué puedes hacer?* → roles y reglas de `AccesoService`.

### 10.2 Flujo de login

1. `POST /api/auth/login` con `{username, password}` (ruta pública).
2. `AuthServicesImpl.login` busca el usuario y compara con `passwordEncoder.matches(clave, hash)`.
3. `JwtTokenServices.generar` crea y firma el token.
4. Respuesta: `{ token, tipo: "Bearer", expiraEn, usuario: {...} }`.
5. El front guarda el token y lo envía en cada petición: `Authorization: Bearer <token>`.

### 10.3 Qué es un JWT

**JSON Web Token**: texto con tres partes separadas por punto: `header.payload.firma`.
- **Header:** `{"alg":"HS256"}`.
- **Payload (claims):** `{"iss":"gestion-academica","sub":"demo_prof001","rol":"PROFESOR","idProfesor":"DEMO-P001","idUsuario":12,"iat":...,"exp":...}`.
- **Firma:** HMAC-SHA256 de header+payload con el **secreto** del servidor (`app.jwt.secret`).

Están en Base64, **no cifrados**: cualquiera puede leer el payload, pero **nadie puede modificarlo** sin el secreto, porque la firma dejaría de coincidir. Por eso no se pone la contraseña en el token.

**HS256** es simétrico: la misma clave firma y verifica. Sirve porque el mismo servidor emite y valida los tokens.

**Ventaja del JWT (stateless):** el servidor no guarda sesiones; con verificar la firma sabe quién es y qué rol tiene. Escala fácil.

### 10.4 BCrypt

- Es una función *hash* **lenta a propósito** y con **sal** aleatoria: dos usuarios con la misma clave tienen hashes distintos, y probar claves por fuerza bruta es costoso.
- El hash empieza por `$2a$10$`: versión 2a, costo 10 (2¹⁰ rondas).
- No se puede "desencriptar": para verificar se vuelve a calcular y se compara (`matches`).

### 10.5 Roles y permisos

| Recurso | ADMINISTRATIVO | COORDINADOR | PROFESOR | ESTUDIANTE |
|---|---|---|---|---|
| Usuarios | CRUD | — | — | — |
| Carreras, niveles, materias, plan, grupos, asignaciones | CRUD | CRUD | consulta | consulta |
| Profesores | CRUD | CRUD | consulta | — |
| Estudiantes | CRUD | CRUD | solo los inscritos en sus grupos | solo él mismo |
| Inscripciones | CRUD | CRUD | listado: solo sus grupos | solo las suyas |
| Notas y calificaciones | CRUD | CRUD | listado: solo sus materias; registra/edita **solo en sus materias** | solo las suyas |
| Asistencia | CRUD | CRUD | listado: solo sus grupos; registra **solo en sus grupos** | solo la suya |
| Boletín | sí | sí | sí | solo el suyo |
| En riesgo / planilla | sí | sí | solo sus grupos y materias | — |

**Dos niveles de control:**
1. **Por URL** en `SecurityConfig` (`/api/usuarios/**` → ADMINISTRATIVO; resto → autenticado).
2. **Por método** con `@PreAuthorize` + `AccesoService` (reglas finas que dependen de los datos).

Los **listados generales** se recortan en el servidor (`acceso.soloMateriasAsignadas(...)` etc.): un profesor que llama `GET /api/notas` recibe solo las notas de sus materias.

### 10.6 Por qué CSRF desactivado

CSRF es un ataque que aprovecha que el navegador manda **cookies** solo. Esta API no usa cookies de sesión: el token va en la cabecera `Authorization`, que el navegador no agrega por sí solo. Por eso no aplica y se desactiva.

### 10.7 CORS

El navegador bloquea que una página en `http://localhost:5173` (el front) llame a `http://localhost:8080` (otro origen) salvo que el servidor lo permita con cabeceras `Access-Control-Allow-*`. Antes de un `POST` con `Authorization`, el navegador envía un **preflight** (`OPTIONS`). Se configuró `.cors(Customizer.withDefaults())` en `SecurityConfig` para que ese `OPTIONS` se responda **antes** de exigir token (si no, devolvía 401 y el front no podía llamar a la API).

---

## 11. Reglas de negocio

| # | Regla | Dónde | Código de error |
|---|---|---|---|
| 1 | No crear un registro con un id que ya existe | Servicios de carrera, nivel, materia, grupo, profesor, estudiante | 409 `YA_EXISTE` |
| 2 | Correo único de estudiantes y profesores | `EstudianteServicesImpl`, `ProfesorServicesImpl` | 409 `CORREO_DUPLICADO` |
| 3 | No borrar si tiene registros que dependen de él | Todos los `eliminar` | 409 `TIENE_DEPENDENCIAS` |
| 4 | Una materia solo una vez por nivel en el plan | `NivelMateriaServicesImpl` | 409 `MATERIA_YA_ASOCIADA` |
| 5 | Solo profesores activos reciben asignaciones | `AsignacionMateriaProfesorServicesImpl` | 409 `PROFESOR_INACTIVO` |
| 6 | La materia asignada debe estar en el plan del nivel del grupo | Asignaciones y planilla | 409 `MATERIA_FUERA_DEL_NIVEL` |
| 7 | Un solo profesor por materia en cada grupo | Asignaciones (+ índice único) | 409 `MATERIA_YA_ASIGNADA` |
| 8 | Solo estudiantes activos se inscriben | `InscripcionServicesImpl` | 409 `ESTUDIANTE_INACTIVO` |
| 9 | No inscribir dos veces en el mismo grupo y año | Inscripciones (+ índice único) | 409 `INSCRIPCION_DUPLICADA` |
| 10 | No superar el cupo máximo del grupo | Inscripciones | 409 `CUPO_LLENO` |
| 11 | No registrar asistencia en fechas futuras | `AsistenciaServicesImpl` | 409 `FECHA_FUTURA` |
| 12 | Solo hay asistencia si el estudiante está inscrito en el grupo | Asistencia | 409 `ESTUDIANTE_NO_INSCRITO` |
| 13 | Un registro de asistencia por estudiante, grupo y día | Asistencia (+ índice único) | 409 `ASISTENCIA_DUPLICADA` |
| 14 | Solo se justifica una inasistencia | Asistencia | 409 `JUSTIFICACION_INVALIDA` |
| 15 | No dos notas en curso o aprobadas de la misma materia (sí se puede repetir si la perdió) | `NotaServicesImpl` | 409 `NOTA_DUPLICADA` |
| 16 | La definitiva = promedio de las calificaciones (2 decimales); aprueba con ≥ 3.0 | `NotaServicesImpl.recalcularDefinitiva` | — |
| 17 | Las calificaciones van de 0.0 a 5.0 | Validaciones del DTO | 400 `VALIDACION` |
| 18 | Estudiante en riesgo: promedio < 3.0 o asistencia < 80 % | `ReporteServicesImpl` | — |
| 19 | Usuario ESTUDIANTE debe tener `idEstudiante` existente; PROFESOR, `idProfesor` | `UsuarioServicesImpl` | 409 `VINCULO_INVALIDO` / 404 |
| 20 | Un solo usuario por estudiante o profesor | `UsuarioServicesImpl` (+ índice único) | 409 `VINCULO_DUPLICADO` |
| 21 | Username único | `UsuarioServicesImpl` | 409 `USERNAME_DUPLICADO` |
| 22 | Un admin no puede borrarse, desactivarse ni quitarse el rol | `UsuarioServicesImpl` | 409 `CUENTA_PROPIA` |
| 23 | Siempre debe quedar al menos un ADMINISTRATIVO activo | `UsuarioServicesImpl` | 409 `ULTIMO_ADMINISTRATIVO` |
| 24 | Cuenta desactivada no inicia sesión | `AuthServicesImpl` | 403 `CUENTA_DESACTIVADA` |

---

## 12. Manejo de errores

Todos los errores tienen el mismo formato:

```json
{
  "timestamp": "2026-10-24T11:54:31.73",
  "status": 404,
  "error": "No encontrado",
  "codigo": "ESTUDIANTE_NO_ENCONTRADO",
  "message": "Estudiante no encontrado con id: NO-EXISTE"
}
```

Las validaciones agregan `"errores": {"username": "username es obligatorio"}`.

| HTTP | `error` | Cuándo | Ejemplos de `codigo` |
|---|---|---|---|
| 400 | Solicitud inválida | Datos mal formados o inválidos | `VALIDACION`, `CUERPO_INVALIDO`, `PARAMETRO_FALTANTE`, `PARAMETRO_INVALIDO` |
| 401 | No autenticado | Sin token, token inválido/vencido, clave errada | `NO_AUTENTICADO`, `TOKEN_INVALIDO`, `CREDENCIALES_INVALIDAS` |
| 403 | Acceso denegado | El rol no alcanza o la cuenta está inactiva | `SIN_PERMISO`, `CUENTA_DESACTIVADA` |
| 404 | No encontrado | Registro o ruta inexistente | `ESTUDIANTE_NO_ENCONTRADO`, `NOTA_NO_ENCONTRADA`, `RUTA_NO_ENCONTRADA`… |
| 405 | Método no permitido | Verbo no soportado en esa ruta (ej. `DELETE /api/notas`) | `METODO_NO_PERMITIDO` |
| 409 | Conflicto | Regla de negocio o integridad | `CUPO_LLENO`, `YA_EXISTE`, `INTEGRIDAD_DATOS`… |
| 415 | Tipo de contenido no soportado | El cuerpo no es JSON | `TIPO_CONTENIDO_NO_SOPORTADO` |
| 500 | Error interno del servidor | Cualquier error no previsto (el detalle va al log, no al cliente) | `ERROR_INTERNO` |

**Dos puntos donde se generan errores:**
1. **Dentro de los controladores** → `ApiExceptionHandler` (`@RestControllerAdvice`).
2. **En los filtros de seguridad** (antes del controlador) → `ErroresSeguridadHandler`.

Ambos usan `CuerpoError.crear(...)`, así el formato es idéntico.

**Por qué 409 para reglas de negocio:** la petición está bien formada (no es 400), pero choca con el estado actual de los datos (cupo lleno, ya existe). Eso es exactamente un *conflicto*.

**Por qué 401 vs 403:** 401 = no sé quién eres; 403 = sé quién eres pero no tienes permiso.

---

## 13. Todos los endpoints

Leyenda de permisos: **Todos** = cualquier usuario autenticado · **Personal** = ADMINISTRATIVO, COORDINADOR o PROFESOR · **Gestor** = ADMINISTRATIVO o COORDINADOR.

### Autenticación y usuarios

| Verbo | Ruta | Quién | Qué hace |
|---|---|---|---|
| POST | `/api/auth/login` | Público | Devuelve el token. |
| GET | `/api/auth/me` | Todos | Datos del usuario del token. |
| GET / POST | `/api/usuarios` | ADMINISTRATIVO | Listar / crear cuentas. |
| GET / PUT / DELETE | `/api/usuarios/{id}` | ADMINISTRATIVO | Ver / editar / borrar una cuenta. |

### Estructura académica

| Verbo | Ruta | Quién |
|---|---|---|
| GET | `/api/carreras`, `/api/carreras/{id}` | Todos |
| POST / PUT / DELETE | `/api/carreras[/{id}]` | Gestor |
| GET | `/api/niveles`, `/api/niveles/{id}` | Todos |
| POST / PUT / DELETE | `/api/niveles[/{id}]` | Gestor |
| GET | `/api/materias`, `/api/materias/{id}` | Todos |
| POST / PUT / DELETE | `/api/materias[/{id}]` | Gestor |
| GET | `/api/niveles-materias`, `/api/niveles-materias/{id}` | Todos |
| POST / PUT / DELETE | `/api/niveles-materias[/{id}]` | Gestor |
| GET | `/api/grupos`, `/api/grupos/{id}` | Todos |
| GET | `/api/grupos/{id}/cupo?anio=2026` | Todos |
| POST / PUT / DELETE | `/api/grupos[/{id}]` | Gestor |
| GET | `/api/asignaciones-materia-profesor`, `/{id}`, `/profesor/{idProfesor}` | Todos |
| POST / PUT / DELETE | `/api/asignaciones-materia-profesor[/{id}]` | Gestor |

### Personas

| Verbo | Ruta | Quién |
|---|---|---|
| GET | `/api/profesores`, `/api/profesores/{id}` | Personal |
| POST / PUT / DELETE | `/api/profesores[/{id}]` | Gestor |
| GET | `/api/estudiantes` | Personal (el profesor solo ve los inscritos en sus grupos) |
| GET | `/api/estudiantes/{id}` | Personal o el propio estudiante |
| POST / PUT / DELETE | `/api/estudiantes[/{id}]` | Gestor |

### Inscripciones

| Verbo | Ruta | Quién |
|---|---|---|
| GET | `/api/inscripciones` | Personal (profesor: solo sus grupos) |
| GET | `/api/inscripciones/estudiante/{idEstudiante}` | Personal o el propio estudiante |
| GET | `/api/inscripciones/grupo/{idGrupo}`, `/api/inscripciones/{id}` | Personal |
| POST / PUT / DELETE | `/api/inscripciones[/{id}]` | Gestor |

### Notas y calificaciones

| Verbo | Ruta | Quién |
|---|---|---|
| GET | `/api/notas` | Personal (profesor: solo sus materias) |
| GET | `/api/notas/estudiante/{id}`, `/api/notas/estudiante/{id}/promedio` | Personal o el propio estudiante |
| GET | `/api/notas/{id}` | Personal o el dueño de la nota |
| POST | `/api/notas` | Gestor o el profesor de esa materia |
| PUT / DELETE | `/api/notas/{id}` | Gestor o el profesor de esa materia |
| GET | `/api/movimiento-notas` | Personal (profesor: solo sus materias) |
| GET | `/api/movimiento-notas/nota/{idNota}` | Personal o el dueño de la nota |
| GET | `/api/movimiento-notas/{id}` | Personal |
| POST / PUT / DELETE | `/api/movimiento-notas[/{id}]` | Gestor o el profesor de la materia de la nota |

### Asistencia

| Verbo | Ruta | Quién |
|---|---|---|
| GET | `/api/asistencia` | Personal (profesor: solo sus grupos) |
| GET | `/api/asistencia/estudiante/{id}`, `/resumen` | Personal o el propio estudiante |
| GET | `/api/asistencia/{id}` | Personal |
| POST / PUT / DELETE | `/api/asistencia[/{id}]` | Gestor o el profesor que dicta en ese grupo |

### Reportes

| Verbo | Ruta | Quién |
|---|---|---|
| GET | `/api/reportes/boletin/{idEstudiante}` | Personal o el propio estudiante |
| GET | `/api/reportes/grupo/{idGrupo}/en-riesgo?anio=` | Gestor o profesor del grupo |
| GET | `/api/reportes/grupo/{idGrupo}/planilla-notas?idMateria=&anio=` | Gestor o el profesor de esa materia en ese grupo |

---

## 14. Configuración (application.properties)

| Propiedad | Valor | Qué significa |
|---|---|---|
| `spring.application.name` | gestionAcademica | Nombre de la app. |
| `spring.config.import` | `optional:classpath:application-local.properties` | Carga un archivo local con secretos (no está en git). `optional` = si no existe, no falla. |
| `spring.datasource.url` | `${DB_URL:jdbc:mysql://localhost:3306/sistema_academico?...}` | Conexión a MySQL. `${VAR:valor}` = usa la variable de entorno o, si no hay, el valor por defecto. `allowPublicKeyRetrieval=true` permite el login de MySQL 8 sin SSL en local. |
| `spring.datasource.username` / `password` | root / (local) | Credenciales de MySQL. |
| `spring.jpa.hibernate.ddl-auto` | `none` | Hibernate **no crea ni altera tablas**: el esquema lo controla `db/schema.sql`. (En pruebas es `create-drop`.) |
| `spring.jpa.show-sql` / `format_sql` | true | Muestra en consola el SQL que genera Hibernate. |
| `spring.jpa.database-platform` | `MySQLDialect` | Dialecto SQL de MySQL. |
| `spring.jpa.open-in-view` | true | Mantiene la sesión de Hibernate abierta hasta que se arma la respuesta, para que los DTO puedan leer relaciones `LAZY` fuera del servicio. |
| `app.jwt.secret` | (local, ≥ 32 caracteres) | Secreto para firmar los tokens. |
| `app.jwt.expiracion-minutos` | 480 | El token dura 8 horas. |
| `app.admin.username` / `password` | admin / (local) | Administrador inicial. |
| `server.port` | (no definido) | Por defecto **8080**. |

---

## 15. Pruebas automáticas

- **Archivo:** `src/test/java/.../SeguridadYDtosIntegrationTest.java` (15 pruebas) + `GestionAcademicaApplicationTests` (el contexto arranca).
- **Tipo:** pruebas de **integración**: levantan la app completa con `@SpringBootTest` y simulan peticiones con `MockMvc`.
- **BD:** H2 en memoria (`src/test/resources/application.properties`) con datos de `datos-prueba.sql`.
- **Qué prueban:** login correcto/incorrecto; 401 sin token; estudiante solo ve lo suyo; profesor solo califica sus materias; listados recortados por rol; coordinador no administra usuarios; admin crea usuarios vinculados; DTOs planos; 404 por referencia inexistente; formato de errores (400, 401, 403, 404, 405, 415); preflight CORS; reglas de usuarios (cuenta propia, último admin, cuenta desactivada).
- **Ejecutar:** `./mvnw test` → `Tests run: 16, Failures: 0`.

**Pregunta probable:** *¿Por qué H2 y no MySQL en las pruebas?* Para que se puedan correr en cualquier máquina sin instalar MySQL, más rápido y sin ensuciar la BD real.

---

## 16. Scripts de base de datos

| Archivo | Para qué |
|---|---|
| `db/schema.sql` | Crea la BD y todas las tablas con llaves foráneas e índices únicos (`IF NOT EXISTS`). |
| `db/migracion_bd_existente.sql` | Ajusta una BD creada antes del `schema.sql` (tipos e índices). |
| `db/data.sql` | Datos de demostración pequeños (para BD vacía). |
| `db/usuarios_masivos.sql` | 3000 usuarios (2000 estudiantes, 700 profesores, 250 coordinadores, 50 administrativos) con sus estudiantes y profesores `DEMO-*`. Contraseña `Demo1234*`. |
| `db/datos_academicos_demo.sql` | Llena todas las tablas para esos usuarios: 5 carreras, 20 niveles, 100 materias, 40 grupos, 200 asignaciones, 2000 inscripciones, 10 000 notas, 30 000 calificaciones, 20 000 asistencias. Usa CTE recursivas (`WITH RECURSIVE`) para generar filas. |
| `db/*_borrar.sql` | Deshacen los dos anteriores (primero `datos_academicos_demo_borrar.sql`). |

---

## 17. Preguntas probables y respuestas

### Generales

**¿Qué hace el proyecto?**
Es el back de un sistema académico: una API REST que gestiona la estructura académica, las personas, inscripciones, notas y asistencia, con reportes y seguridad por rol.

**¿Por qué Spring Boot?**
Porque trae servidor embebido, autoconfiguración, inyección de dependencias, integración con JPA y Spring Security. Permite concentrarse en las reglas del negocio y no en infraestructura.

**¿Qué es una API REST y por qué es *stateless*?**
Recursos identificados por URL y manipulados con verbos HTTP. *Stateless* porque el servidor no guarda sesión: cada petición trae su token, y con él el servidor sabe quién es.

**¿Qué patrón de arquitectura usaron?**
Arquitectura por capas con puertos y adaptadores: dominio, aplicación, infraestructura y presentación. Los servicios dependen de interfaces de repositorio (puertos) que implementan adaptadores JPA.

**¿Qué es la inyección de dependencias y dónde se ve?**
Spring crea los objetos y los entrega por el constructor. Ej.: `NotaServicesImpl(NotaRepository, MovimientoNotaRepository, EstudianteRepository)`. Nadie hace `new NotaRepositoryAdapter()`.

**¿Qué es un bean?**
Un objeto creado y administrado por Spring (por `@Component`, `@Service`, `@Repository`, `@Configuration`/`@Bean`). Por defecto es *singleton*: una sola instancia para toda la app.

**¿Qué principios SOLID se aplican?**
- **S** (responsabilidad única): controlador solo HTTP, servicio solo reglas, repositorio solo datos.
- **O** (abierto/cerrado): se agregan reglas de acceso nuevas en `AccesoService` sin tocar los servicios.
- **L** (sustitución de Liskov): cualquier implementación de `NotaRepository` sirve.
- **I** (segregación de interfaces): cada repositorio expone solo lo que su servicio necesita.
- **D** (inversión de dependencias): servicios y controladores dependen de interfaces (`NotaServices`, `NotaRepository`, `TokenServices`).

### Base de datos y JPA

**¿Qué es JPA y qué es Hibernate?**
JPA es la especificación (las reglas/anotaciones); Hibernate es la implementación que genera el SQL.

**¿Cómo se genera la consulta `findByEstudianteIdEstudiante`?**
Spring Data lee el nombre del método: `findBy` + propiedad `estudiante` + su campo `idEstudiante` → `WHERE ID_Estudiante = ?`. No se escribe SQL.

**¿Qué es LAZY y por qué se usa?**
La relación no se carga hasta que se usa. Evita traer el estudiante, su grupo, su nivel… cada vez que se lee una nota.

**¿Qué es el problema N+1?**
Leer N registros y luego hacer una consulta extra por cada uno para traer su relación (1 + N consultas). Ocurre con listados grandes y relaciones LAZY. Se resuelve con `JOIN FETCH` o `@EntityGraph`. (Ver limitaciones.)

**¿Qué es una transacción y por qué `@Transactional`?**
Un conjunto de operaciones que se guardan todas o ninguna. Al crear una calificación se inserta el movimiento y se actualiza la definitiva: si una falla, se deshacen ambas.

**¿Por qué `ddl-auto=none`?**
Para que Hibernate no modifique la BD real; el esquema está versionado en `db/schema.sql`.

**¿Por qué validar en el servicio si la BD ya tiene índices únicos?**
El servicio da un mensaje claro y un código de negocio (`INSCRIPCION_DUPLICADA`); el índice es la última defensa ante condiciones de carrera (dos peticiones simultáneas).

### Seguridad

**¿Cómo funciona el login?**
Se compara la clave con el hash BCrypt; si coincide se genera un JWT firmado con HS256 que trae el rol y el estudiante/profesor vinculado. El cliente lo envía en `Authorization: Bearer`.

**¿Se puede falsificar el token cambiando el rol?**
No. El payload se puede leer, pero si se modifica, la firma HMAC no coincide y el servidor responde 401 `TOKEN_INVALIDO`. Para firmar se necesita el secreto, que solo tiene el servidor.

**¿Dónde se guarda la contraseña?**
Solo el hash BCrypt en `Password_Hash`. Nunca se devuelve (el `UsuarioResponse` no tiene ese campo).

**¿Por qué el mismo mensaje para "usuario no existe" y "clave incorrecta"?**
Para no revelar qué usuarios existen (enumeración de cuentas).

**¿Cómo sabe el sistema que un profesor puede calificar una materia?**
El token trae `idProfesor`. `AccesoService.puedeCalificarMateria` consulta `asignaciones_materia_profesor` para ver si ese profesor tiene esa materia asignada.

**¿Qué pasa si el front oculta un botón pero alguien llama la API directamente?**
El servidor igual lo bloquea con `@PreAuthorize` → 403. La seguridad real está en el back; el front solo mejora la experiencia.

**¿Qué es `hasRole('ADMINISTRATIVO')`?**
Verifica que el usuario tenga la autoridad `ROLE_ADMINISTRATIVO`, que sale del claim `rol` del token (`JwtAuthenticationConverter` le pone el prefijo `ROLE_`).

**¿Por qué `AccesoService` devuelve `true` si el registro no existe?**
Para que el servicio responda 404 (no existe) en vez de 403 (prohibido), que sería un mensaje engañoso.

**¿Qué es CORS y por qué se configuró?**
Mecanismo del navegador que bloquea llamadas entre orígenes distintos. El front (5173) y el back (8080) son orígenes distintos; se permitieron explícitamente y se habilitó `.cors()` en Spring Security para que el preflight `OPTIONS` no exija token.

**¿Cuánto dura el token y qué pasa al vencer?**
480 minutos (8 h). Al vencer, cualquier petición responde 401 `TOKEN_INVALIDO` y el usuario debe iniciar sesión de nuevo.

### Código Java

**¿Qué es un `record` y por qué los DTO lo son?**
Clase inmutable de solo datos; Java genera constructor y accesores. Los DTO solo transportan datos, así que es ideal y ahorra código.

**¿Qué es `Optional` y qué hace `orElseThrow`?**
Una caja que puede estar vacía. `findById(id).orElseThrow(() -> new NotaNotFoundException(...))` devuelve la nota o lanza la excepción si no existe.

**¿Qué hace `toEntity(Referencias ref)` en un Request?**
Convierte el DTO en una entidad, transformando los ids en objetos reales (`ref.estudiante(idEstudiante)`), y lanza 404 si alguno no existe.

**¿Qué hace `from(entidad)` en un Response?**
Método estático que arma el DTO de salida a partir de la entidad, aplanando las relaciones (id + nombre).

**¿Qué es Lombok y qué pasa si lo quito?**
Genera getters, setters, constructores y builder al compilar. Sin él habría que escribir todo eso a mano.

**¿Por qué los campos inyectados son `private final`?**
`private` para encapsular; `final` para que se asignen una sola vez en el constructor y no se cambien después.

**¿Qué significa `List<NotaResponse> listar(Authentication authentication)`?**
El método devuelve una lista de DTO; Spring inyecta el parámetro `Authentication` con el usuario del token.

**Explica esta línea:**
```java
return acceso.soloMateriasAsignadas(authentication, notaServices.obtenerTodos(), n -> n.getMateria().getIdMateria())
        .stream().map(NotaResponse::from).toList();
```
Trae todas las notas, `AccesoService` deja solo las de las materias asignadas al profesor (o todas si es gestor) usando la lambda para saber la materia de cada nota, y luego cada nota se convierte a `NotaResponse`.

**¿Qué es `LinkedHashMap` y por qué no `HashMap`?**
Conserva el orden en que se insertan las claves; así el JSON de reportes y errores sale con los campos en un orden lógico.

**¿Por qué `Float definitiva` y no `float`?**
Porque necesita poder ser `null` (materia en curso). Un primitivo `float` no admite `null`.

**¿Qué es `@Validated(AlCrear.class)`?**
Activa el grupo de validación `AlCrear` en el POST, que exige campos como el id; en el PUT se usa `@Valid` y el id viene en la URL.

### Reglas

**¿Cómo se calcula la definitiva?**
Promedio de las calificaciones (`movimiento_notas`) de la nota, redondeado a 2 decimales (`Math.round(x * 100) / 100`). Aprueba si es ≥ 3.0. Se recalcula cada vez que se crea, edita o borra una calificación.

**¿Cómo se controla el cupo?**
Al inscribir se cuentan los inscritos del grupo ese año (`countByGrupoIdGrupoAndAnioInscripcion`); si es ≥ `cupoMaximo` → 409 `CUPO_LLENO`. `GET /api/grupos/{id}/cupo` muestra los disponibles.

**¿Cuándo un estudiante está en riesgo?**
Promedio menor a 3.0 o asistencia (en ese grupo) menor al 80 %.

**¿Puede un estudiante repetir una materia?**
Sí, si la reprobó. No puede tener dos notas en curso o aprobadas de la misma materia. En la planilla se toma la nota más reciente.

**¿Qué pasa si intento borrar una carrera que tiene niveles?**
409 `TIENE_DEPENDENCIAS` con el mensaje de cuántos registros dependen de ella. Así no se rompen las llaves foráneas.

---

## 18. Limitaciones conocidas (y cómo responder si te las señalan)

Reconocer las limitaciones con una solución en mente da mejor impresión que negarlas.

| Limitación | Explicación | Cómo se mejoraría |
|---|---|---|
| **Listados lentos con muchos datos** (`GET /api/movimiento-notas` ≈ 9 s con 30 000 filas) | Se traen todos los registros y las relaciones LAZY se cargan una por una (N+1); el filtro por profesor se hace en memoria. | Paginación (`Pageable`), consultas con `JOIN FETCH`/`@EntityGraph` y filtrar por profesor directamente en la consulta SQL. |
| **`open-in-view = true`** | Mantiene la conexión abierta mientras se arma el JSON; cómodo pero no ideal en producción. | Armar los DTO dentro del servicio con consultas que traigan lo necesario. |
| **El token no se puede revocar** | Si se desactiva una cuenta, su token sigue sirviendo hasta que vence (8 h). Es el precio del modelo *stateless*. | Tokens más cortos + *refresh token*, o una lista de tokens revocados. |
| **Consultas de detalle del profesor** | Un profesor puede abrir el detalle de una nota o inscripción de otra materia (los listados sí están recortados). | Aplicar en esos endpoints los mismos métodos de `AccesoService`. |
| **Secreto HS256 compartido** | Si el back se dividiera en varios servicios, todos necesitarían el secreto. | Usar RS256 (llave privada para firmar, pública para verificar). |
| **No hay auditoría** | No se guarda quién cambió una nota y cuándo. | Tabla de auditoría o Hibernate Envers. |

---

### Últimos consejos para la sustentación

- Ten la app corriendo y Postman (carpeta `postman/`) o el front abierto para **demostrar**: login de cada rol, un 403 de estudiante, un profesor que solo ve sus notas, una inscripción con cupo lleno, una calificación que recalcula la definitiva.
- Cuando te pregunten por una clase, responde en este orden: **capa → responsabilidad → método principal → una regla que aplica**.
- Si no sabes algo, ubícalo: "eso está en `AccesoService`, que es la clase de reglas de acceso; déjeme mostrarle" y ábrelo.
