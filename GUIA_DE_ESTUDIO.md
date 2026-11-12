# Guía de estudio: Gestión Académica

Guía completa para sustentar el proyecto, con **énfasis especial en la seguridad y en cómo está implementada cada funcionalidad**. Las salidas de ejemplo (tokens, respuestas de error, ataques) son **reales**: se obtuvieron ejecutando la aplicación contra la base de datos del proyecto.

> **Plan de estudio sugerido**
> 1. Parte I completa (30 min): la idea general y la arquitectura.
> 2. Parte II, seguridad (2 h): léela con el código abierto al lado. Es lo que más te van a preguntar.
> 3. Parte III, implementaciones (1 h).
> 4. Parte V, preguntas: respóndelas en voz alta sin mirar.
> 5. Ensaya la demostración de la sección 2.22.

---

## Índice

**Parte I. Visión general**
- [1.1 Qué es el proyecto](#11-qué-es-el-proyecto)
- [1.2 Tecnologías](#12-tecnologías)
- [1.3 Arquitectura por capas](#13-arquitectura-por-capas)
- [1.4 Modelo de datos](#14-modelo-de-datos)
- [1.5 Recorrido de una petición](#15-recorrido-de-una-petición)

**Parte II. Seguridad (énfasis)**
- [2.1 Conceptos que debes dominar](#21-conceptos-que-debes-dominar)
- [2.2 Mapa de las piezas de seguridad](#22-mapa-de-las-piezas-de-seguridad)
- [2.3 SecurityConfig línea por línea](#23-securityconfig-línea-por-línea)
- [2.4 La cadena de filtros](#24-la-cadena-de-filtros)
- [2.5 Inicio de sesión paso a paso](#25-inicio-de-sesión-paso-a-paso)
- [2.6 Contraseñas con BCrypt](#26-contraseñas-con-bcrypt)
- [2.7 JWT a fondo (con un token real)](#27-jwt-a-fondo-con-un-token-real)
- [2.8 Del token al rol](#28-del-token-al-rol)
- [2.9 Autorización en dos niveles](#29-autorización-en-dos-niveles)
- [2.10 AccesoService método por método](#210-accesoservice-método-por-método)
- [2.11 Recorte de listados por rol](#211-recorte-de-listados-por-rol)
- [2.12 Matriz de permisos completa](#212-matriz-de-permisos-completa)
- [2.13 Errores de seguridad: 401 vs 403](#213-errores-de-seguridad-401-vs-403)
- [2.14 CORS a fondo](#214-cors-a-fondo)
- [2.15 CSRF y sesiones sin estado](#215-csrf-y-sesiones-sin-estado)
- [2.16 Gestión segura de usuarios](#216-gestión-segura-de-usuarios)
- [2.17 Manejo de secretos y configuración](#217-manejo-de-secretos-y-configuración)
- [2.18 Defensa en profundidad](#218-defensa-en-profundidad)
- [2.19 Ataques probados contra la API (resultados reales)](#219-ataques-probados-contra-la-api-resultados-reales)
- [2.20 OWASP Top 10 aplicado al proyecto](#220-owasp-top-10-aplicado-al-proyecto)
- [2.21 Pruebas automáticas de seguridad](#221-pruebas-automáticas-de-seguridad)
- [2.22 Guion de demostración en vivo](#222-guion-de-demostración-en-vivo)

**Parte III. Implementaciones**
- [3.1 Patrón puerto–adaptador (repositorios)](#31-patrón-puertoadaptador-repositorios)
- [3.2 DTOs, Referencias y grupos de validación](#32-dtos-referencias-y-grupos-de-validación)
- [3.3 Transacciones](#33-transacciones)
- [3.4 Definitiva automática de las notas](#34-definitiva-automática-de-las-notas)
- [3.5 Control de cupo](#35-control-de-cupo)
- [3.6 Reglas de asistencia](#36-reglas-de-asistencia)
- [3.7 Notas duplicadas y materias repetidas](#37-notas-duplicadas-y-materias-repetidas)
- [3.8 Asignación de profesores](#38-asignación-de-profesores)
- [3.9 Reportes](#39-reportes)
- [3.10 Manejo de errores unificado](#310-manejo-de-errores-unificado)
- [3.11 Borrado protegido](#311-borrado-protegido)
- [3.12 Datos de demostración](#312-datos-de-demostración)
- [3.13 Todas las reglas de negocio](#313-todas-las-reglas-de-negocio)

**Parte IV. Glosarios**
- [4.1 Anotaciones](#41-anotaciones)
- [4.2 Java: palabras clave y conceptos](#42-java-palabras-clave-y-conceptos)

**Parte V. Banco de preguntas**
- [5.1 Seguridad](#51-seguridad)
- [5.2 Implementación y arquitectura](#52-implementación-y-arquitectura)
- [5.3 Java y Spring](#53-java-y-spring)
- [5.4 Base de datos](#54-base-de-datos)

**Parte VI. Limitaciones y mejoras**
- [6. Limitaciones conocidas](#6-limitaciones-conocidas)

---

# Parte I. Visión general

## 1.1 Qué es el proyecto

Una **API REST** en Java que administra una institución académica: carreras, semestres (niveles), materias, plan de estudios, grupos, profesores, estudiantes, inscripciones, notas con calificaciones parciales y asistencia. Genera reportes (boletín, estudiantes en riesgo, planilla de notas). No tiene pantallas: el front es un proyecto React aparte que consume esta API.

**Frase de apertura:**
> "Es una API REST en Java 21 con Spring Boot 4, organizada por capas, que persiste en MySQL con JPA. Toda petición se autentica con un JWT firmado con HS256, y cada operación se autoriza por rol y por los datos: un profesor solo califica las materias que tiene asignadas y un estudiante solo ve su información. Las reglas académicas viven en la capa de servicios y los errores salen en español con un código estable."

## 1.2 Tecnologías

| Tecnología | Para qué |
|---|---|
| **Java 21** | Lenguaje. Se usan `record`, `switch` con flechas y *pattern matching* (`instanceof Jwt jwt`). |
| **Spring Boot 4.1.1** | Framework: servidor Tomcat embebido (puerto 8080), autoconfiguración, inyección de dependencias. |
| **Maven** (`mvnw`) | Dependencias, compilación, pruebas. |
| **Spring Web MVC** | Controladores REST y conversión a JSON (Jackson 3). |
| **Spring Data JPA + Hibernate 7** | Acceso a datos: ORM que traduce objetos ↔ tablas y genera SQL. |
| **MySQL 8** | Base de datos relacional. |
| **Spring Security 7** | Filtros de autenticación y autorización por método (`@PreAuthorize`). |
| **OAuth2 Resource Server + Nimbus JOSE** | Firmar y validar JWT. |
| **BCrypt** | Hash de contraseñas. |
| **Bean Validation** | Validación de datos de entrada (`@NotBlank`, `@Size`…). |
| **Lombok** | Genera getters, setters, constructores y builder. |
| **H2 + JUnit 5 + MockMvc** | Pruebas automáticas sin MySQL. |

## 1.3 Arquitectura por capas

```
presentation/     Controladores REST, DTOs, CORS, manejo de errores    ← habla HTTP
     │ llama a
application/      Servicios (reglas de negocio) y excepciones          ← decide
     │ usa las interfaces de
domain/           Entidades y repositorios como INTERFACES (puertos)    ← el modelo
     ▲ implementadas por
infrastructure/   Adaptadores JPA (MySQL) y seguridad (JWT, permisos)   ← detalles técnicos
```

- **Dominio** (`domain/models`, `domain/repository`): entidades JPA, enums (`Rol`, `Genero`, `EstadoEstudiante`) e interfaces de repositorio. No sabe nada de HTTP.
- **Aplicación** (`application/Services`, `application/Exceptions`): interfaces de servicio, sus implementaciones `XxxServicesImpl` con las reglas, y las excepciones de negocio.
- **Infraestructura** (`infrastructure/persistence`, `infrastructure/security`): `XxxJpaRepository` (Spring Data), `XxxRepositoryAdapter` (implementa el puerto) y todo lo de seguridad.
- **Presentación** (`presentation/controller`, `dto`, `mapper`, `config`): controladores, `record` de entrada/salida, `Referencias`, `CorsConfig`, `ApiExceptionHandler`, `CuerpoError`.

**Por qué así:** cada capa tiene una sola responsabilidad, las dependencias van hacia interfaces (bajo acoplamiento) y se puede cambiar la tecnología de persistencia o de tokens sin tocar las reglas.

## 1.4 Modelo de datos

```
carreras 1─* niveles 1─* grupos 1─* inscripciones *─1 estudiantes
               │           ├─* asistencia *──────────────┘ │
               │           └─* asignaciones *─1 profesores │
   niveles_materias (plan)       │ (materia)               │
   *            *                │                         │
 niveles     materias 1──────────┘                         │
               └─* notas *─1 estudiantes ──────────────────┘
                     └─* movimiento_notas (calificaciones parciales)

usuarios ─0..1 estudiantes      usuarios ─0..1 profesores
```

| Tabla | Restricciones importantes |
|---|---|
| `niveles_materias` | Único (nivel, materia) |
| `asignaciones_materia_profesor` | Único (grupo, materia): un profesor por materia en cada grupo |
| `inscripciones` | Único (estudiante, grupo, año) |
| `asistencia` | Único (estudiante, grupo, fecha) |
| `estudiantes`, `profesores` | Correo único |
| `usuarios` | `Username`, `ID_Estudiante` e `ID_Profesor` únicos; `Password_Hash` (BCrypt); `Rol` ENUM |

## 1.5 Recorrido de una petición

`POST /api/movimiento-notas` (un profesor registra una calificación):

1. **Tomcat** recibe la petición en el puerto 8080.
2. **CorsFilter** revisa el origen y agrega cabeceras CORS.
3. **BearerTokenAuthenticationFilter** valida el JWT (firma y vencimiento). Si falla, responde 401.
4. **JwtAuthenticationConverter** convierte `rol: PROFESOR` en `ROLE_PROFESOR`.
5. **AuthorizationFilter** aplica las reglas por URL (`authenticated()`).
6. **DispatcherServlet** encuentra `MovimientoNotaController.crear`.
7. **Jackson + `@Valid`** convierten y validan el JSON. Si falla, responde 400.
8. **`@PreAuthorize`** llama a `AccesoService.puedeModificarNota`. Si da `false`, responde 403.
9. El **controlador** convierte el DTO en entidad (`Referencias`).
10. El **servicio** (`@Transactional`) guarda la calificación y recalcula la definitiva.
11. **Hibernate** ejecuta `INSERT` y `UPDATE`; la transacción hace *commit*.
12. Se devuelve el DTO como JSON con **201 Created**.

---

# Parte II. Seguridad

## 2.1 Conceptos que debes dominar

| Concepto | Definición corta | En el proyecto |
|---|---|---|
| **Autenticación** | Comprobar *quién eres*. | Login con usuario/contraseña → JWT. |
| **Autorización** | Decidir *qué puedes hacer*. | Roles + reglas de `AccesoService`. |
| **Stateless** | El servidor no guarda sesiones; cada petición trae su credencial. | `SessionCreationPolicy.STATELESS` + token en cada petición. |
| **Token** | Credencial que el cliente presenta en cada petición. | JWT en `Authorization: Bearer <token>`. |
| **Hash** | Función de un solo sentido: de la clave sale el hash, pero no al revés. | BCrypt para contraseñas. |
| **Sal (salt)** | Valor aleatorio que se mezcla con la clave antes del hash. | BCrypt la genera y la guarda dentro del hash. |
| **Firma digital / HMAC** | Código calculado con un secreto que prueba que el contenido no fue alterado. | HMAC-SHA256 (HS256) firma el JWT. |
| **Simétrico vs asimétrico** | Simétrico: la misma clave firma y verifica. Asimétrico: llave privada firma, pública verifica. | HS256 es simétrico (un solo secreto en el servidor). |
| **Base64URL** | Codificación de texto (no es cifrado). | Las tres partes del JWT. |
| **Claim** | Un dato dentro del JWT. | `rol`, `idProfesor`, `idEstudiante`, `idUsuario`, `sub`, `exp`… |
| **Autoridad (*authority*)** | Permiso que Spring Security asigna al usuario. | `ROLE_PROFESOR`. |
| **CORS** | Regla del navegador que bloquea llamadas a otro origen salvo que el servidor las permita. | `CorsConfig` + `.cors()`. |
| **Preflight** | Petición `OPTIONS` que el navegador envía antes de la real para preguntar si está permitida. | Se responde 200 sin exigir token. |
| **CSRF** | Ataque que usa las cookies que el navegador envía solo. | Desactivado: no se usan cookies. |
| **IDOR** | Acceder a un recurso ajeno cambiando el id en la URL. | `AccesoService` verifica que el recurso sea tuyo. |
| **Principio de mínimo privilegio** | Cada rol tiene solo los permisos que necesita. | Matriz de permisos (2.12). |
| **Defensa en profundidad** | Varias capas de protección independientes. | Front + filtros + `@PreAuthorize` + servicio + índices de BD. |

## 2.2 Mapa de las piezas de seguridad

| Archivo | Capa | Responsabilidad |
|---|---|---|
| [SecurityConfig.java](src/main/java/com/example/gestionacademica/infrastructure/security/SecurityConfig.java) | Infraestructura | Cadena de filtros, reglas por URL, BCrypt, llave y codificador/decodificador JWT, conversión rol → autoridad. |
| [JwtTokenServices.java](src/main/java/com/example/gestionacademica/infrastructure/security/JwtTokenServices.java) | Infraestructura | Crea y firma el token con sus claims. |
| [AccesoService.java](src/main/java/com/example/gestionacademica/infrastructure/security/AccesoService.java) | Infraestructura | Reglas de acceso que dependen de los datos y recorte de listados. Bean `acceso`. |
| [ErroresSeguridadHandler.java](src/main/java/com/example/gestionacademica/infrastructure/security/ErroresSeguridadHandler.java) | Infraestructura | Respuesta JSON de los 401/403 que ocurren en los filtros. |
| [AdminInicialRunner.java](src/main/java/com/example/gestionacademica/infrastructure/security/AdminInicialRunner.java) | Infraestructura | Crea el primer ADMINISTRATIVO al arrancar si no hay ninguno. |
| [AuthServicesImpl.java](src/main/java/com/example/gestionacademica/application/Services/impl/AuthServicesImpl.java) | Aplicación | Lógica del login (verificar clave, cuenta activa). |
| [UsuarioServicesImpl.java](src/main/java/com/example/gestionacademica/application/Services/impl/UsuarioServicesImpl.java) | Aplicación | Reglas de cuentas: vínculos, cuenta propia, último administrativo, hash de la clave. |
| `TokenServices`, `Sesion` | Aplicación | Interfaz para generar tokens y resultado del login (la aplicación no depende de JWT). |
| `CredencialesInvalidasException`, `CuentaDesactivadaException` | Aplicación | Errores del login (401 y 403). |
| [AuthController.java](src/main/java/com/example/gestionacademica/presentation/controller/AuthController.java) | Presentación | `POST /api/auth/login` y `GET /api/auth/me`. |
| [UsuarioController.java](src/main/java/com/example/gestionacademica/presentation/controller/UsuarioController.java) | Presentación | CRUD de cuentas, solo ADMINISTRATIVO. |
| [CorsConfig.java](src/main/java/com/example/gestionacademica/presentation/config/CorsConfig.java) | Presentación | Orígenes, métodos y cabeceras permitidos. |
| [ApiExceptionHandler.java](src/main/java/com/example/gestionacademica/presentation/controller/ApiExceptionHandler.java) | Presentación | 403 de `@PreAuthorize`, 401/403 del login y demás errores. |
| [UsuarioDtos.java](src/main/java/com/example/gestionacademica/presentation/dto/UsuarioDtos.java) | Presentación | `LoginRequest`, `LoginResponse`, `UsuarioRequest`, `UsuarioResponse` (sin hash). |
| [Usuario.java](src/main/java/com/example/gestionacademica/domain/models/Usuario.java), [Rol.java](src/main/java/com/example/gestionacademica/domain/models/Rol.java) | Dominio | La cuenta y los cuatro roles. |
| `@PreAuthorize` en cada controlador | Presentación | La regla de cada endpoint. |

## 2.3 SecurityConfig línea por línea

```java
@Configuration                 // clase que declara beans
@EnableMethodSecurity          // activa @PreAuthorize en los métodos
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ErroresSeguridadHandler errores) throws Exception {
        http
            .cors(Customizer.withDefaults())                       // (1)
            .csrf(csrf -> csrf.disable())                          // (2)
            .sessionManagement(sesion -> sesion
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))   // (3)
            .authorizeHttpRequests(auth -> auth                    // (4)
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/api/usuarios/**").hasRole("ADMINISTRATIVO")
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth -> oauth                   // (5)
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                .authenticationEntryPoint(errores))
            .exceptionHandling(ex -> ex                            // (6)
                .authenticationEntryPoint(errores)
                .accessDeniedHandler(errores));
        return http.build();
    }
```

| # | Línea | Qué hace | Por qué |
|---|---|---|---|
| 1 | `.cors(Customizer.withDefaults())` | Activa el `CorsFilter` de Spring Security usando el bean `CorsConfigurationSource` de `CorsConfig`. | El preflight `OPTIONS` del navegador no trae token; sin esto respondía 401 y el front no podía llamar a la API. |
| 2 | `.csrf(csrf -> csrf.disable())` | Desactiva la protección CSRF. | CSRF protege de cookies que el navegador envía solo; aquí el token va en una cabecera que el navegador no agrega por su cuenta. |
| 3 | `STATELESS` | Spring no crea ni usa sesión HTTP (`JSESSIONID`). | Cada petición se autentica con su JWT; el servidor no guarda estado y escala fácil. |
| 4 | `authorizeHttpRequests` | Reglas **por URL**, evaluadas en orden; gana la primera que coincide. | Primer nivel de autorización. |
| | `POST /api/auth/login` → `permitAll()` | El login es público. | Sin token todavía. |
| | `/error` → `permitAll()` | La ruta interna de errores de Spring Boot. | Para que un error no se convierta en 401. |
| | `/api/usuarios/**` → `hasRole("ADMINISTRATIVO")` | Solo ADMINISTRATIVO administra cuentas. | Se repite en `@PreAuthorize` del controlador (dos barreras). |
| | `anyRequest().authenticated()` | Todo lo demás exige token válido. | Por defecto se niega: lo que no está explícito, requiere sesión. |
| 5 | `oauth2ResourceServer().jwt(...)` | Agrega el `BearerTokenAuthenticationFilter`: lee `Authorization: Bearer`, valida con el `JwtDecoder` y convierte el token con `jwtAuthenticationConverter()`. | Así la API es un *resource server* que acepta JWT. |
| | `.authenticationEntryPoint(errores)` | Si el token es inválido, responde con nuestro JSON. | Formato de error unificado. |
| 6 | `exceptionHandling(...)` | Qué hacer cuando un anónimo intenta entrar (401) o cuando un autenticado no tiene permiso por URL (403). | Ambos con `ErroresSeguridadHandler`. |

```java
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();                      // (7) costo 10 por defecto
    }

    @Bean
    public SecretKey jwtSecretKey(@Value("${app.jwt.secret}") String secret) {
        if (secret == null || secret.length() < 32) {            // (8) falla al arrancar
            throw new IllegalStateException("app.jwt.secret debe tener al menos 32 caracteres ...");
        }
        return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {       // (9) firma
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey));
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey jwtSecretKey) {       // (10) valida
        return NimbusJwtDecoder.withSecretKey(jwtSecretKey).macAlgorithm(MacAlgorithm.HS256).build();
    }

    // El claim "rol" del token se convierte en la autoridad ROLE_<rol>
    private JwtAuthenticationConverter jwtAuthenticationConverter() {   // (11)
        JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("rol");
        roles.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(roles);
        return converter;
    }
}
```

| # | Qué hace | Por qué |
|---|---|---|
| 7 | Bean de BCrypt para hashear y verificar contraseñas. | Lo usan `UsuarioServicesImpl` (al guardar), `AuthServicesImpl` (al verificar) y `AdminInicialRunner`. |
| 8 | Exige un secreto de al menos 32 caracteres (256 bits) o **la aplicación no arranca** (*fail fast*). | HS256 necesita una llave de al menos 256 bits; un secreto corto se adivinaría por fuerza bruta. Es mejor no arrancar que arrancar inseguro. |
| 9 | `JwtEncoder` con el secreto: lo usa `JwtTokenServices` para firmar. | Nimbus hace la criptografía. |
| 10 | `JwtDecoder` que **solo acepta HS256** con ese secreto. Valida firma y fechas (`exp`, con un margen de 60 s). | Rechaza tokens alterados, vencidos o con otro algoritmo (incluido `alg: none`). |
| 11 | Lee el claim `rol` y crea la autoridad `ROLE_<rol>`. | Por defecto Spring buscaría el claim `scope`; aquí se adapta a nuestro token. El prefijo `ROLE_` es lo que espera `hasRole(...)`. |

## 2.4 La cadena de filtros

Spring Security es una **cadena de filtros** (`SecurityFilterChain`) que se ejecuta **antes** de llegar al controlador. Los más importantes, en orden:

```
Petición
  │
  ├─ SecurityContextHolderFilter      prepara el contexto de seguridad (vacío, porque es STATELESS)
  ├─ HeaderWriterFilter               agrega cabeceras de seguridad (X-Content-Type-Options: nosniff,
  │                                   X-Frame-Options: DENY, Cache-Control: no-store...)
  ├─ CorsFilter                       (1) CORS; responde el preflight OPTIONS aquí mismo
  ├─ BearerTokenAuthenticationFilter  (5) valida el JWT → crea la Authentication
  │       └─ token inválido → ErroresSeguridadHandler.commence → 401 TOKEN_INVALIDO
  ├─ AnonymousAuthenticationFilter    sin token → usuario "anónimo"
  ├─ ExceptionTranslationFilter       convierte AccessDenied/Authentication exceptions en 401/403
  └─ AuthorizationFilter              (4) reglas por URL
          ├─ anónimo en ruta protegida → ErroresSeguridadHandler.commence → 401 NO_AUTENTICADO
          └─ rol insuficiente (ej. /api/usuarios) → ErroresSeguridadHandler.handle → 403 SIN_PERMISO
  │
  ▼
DispatcherServlet → validación @Valid → @PreAuthorize → controlador → servicio
```

**Detalles que impresionan al profesor:**
- Si mandas un token **inválido** incluso a una ruta pública, el `BearerTokenAuthenticationFilter` lo rechaza con 401: un token malo nunca se ignora en silencio.
- La validación `@Valid` ocurre **antes** de `@PreAuthorize`: Spring MVC primero arma y valida los argumentos y después invoca el método protegido. Por eso un JSON inválido puede dar 400 antes que 403.
- El `SecurityContext` se llena por petición y se descarta al terminar (no hay sesión).

## 2.5 Inicio de sesión paso a paso

**Petición:**
```http
POST /api/auth/login
Content-Type: application/json

{ "username": "demo_prof001", "password": "Demo1234*" }
```

**Código** ([AuthServicesImpl.java:33](src/main/java/com/example/gestionacademica/application/Services/impl/AuthServicesImpl.java:33)):
```java
// Mismo mensaje para usuario inexistente o clave errada: no se revela cuál falló.
// Que la cuenta está desactivada solo se informa si la clave es correcta
@Override
public Sesion login(String username, String password) {
    Usuario usuario = usuarioRepository.findByUsername(username)                       // (1)
            .orElseThrow(() -> new CredencialesInvalidasException("Usuario o contraseña incorrectos"));
    if (!passwordEncoder.matches(password, usuario.getPasswordHash())) {              // (2)
        throw new CredencialesInvalidasException("Usuario o contraseña incorrectos");
    }
    if (!usuario.getActivo()) {                                                       // (3)
        throw new CuentaDesactivadaException("Tu cuenta está desactivada");
    }
    return tokenServices.generar(usuario);                                            // (4)
}
```

1. Busca el usuario. Si no existe → 401 `CREDENCIALES_INVALIDAS`.
2. Compara la clave con el hash BCrypt. Si no coincide → **el mismo** 401. Así un atacante no puede averiguar qué usuarios existen (**enumeración de usuarios**).
3. Solo si la clave es correcta se revela que la cuenta está desactivada → 403 `CUENTA_DESACTIVADA`. Quien no conoce la clave no se entera de nada.
4. Genera el token.

**Respuesta real:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkZW1vX3Byb2YwMDEi...",
  "tipo": "Bearer",
  "expiraEn": "2026-11-12T01:39:28.203384100Z",
  "usuario": { "idUsuario": 2058, "username": "demo_prof001", "rol": "PROFESOR", "activo": true,
               "idEstudiante": null, "idProfesor": "DEMO-P001" }
}
```
Fíjate que `usuario` **no trae `passwordHash`**: `UsuarioResponse` no tiene ese campo.

**`GET /api/auth/me`:** devuelve el usuario dueño del token (`authentication.getName()` = el claim `sub`). El front lo usa para saber quién está conectado.

**Validación del login** (`LoginRequest`): `@NotBlank` en `username` y `password` → si faltan, 400 `VALIDACION` antes de tocar la BD.

## 2.6 Contraseñas con BCrypt

Hash real guardado en la BD para `Demo1234*`:
```
$2a$10$BBvTuUAAk0WmAG7LpzGUfOUVSKpXwl4cUg40AaXvfWZRJ.cEiUo6K
│  │  │                      │
│  │  └ sal (22 caracteres)  └ hash (31 caracteres)
│  └ costo: 10 → 2^10 = 1024 rondas
└ versión del algoritmo (2a)
```

| Pregunta | Respuesta |
|---|---|
| ¿Por qué no guardar la clave tal cual? | Si roban la BD, tendrían todas las claves (y muchos usuarios las repiten en otros sitios). |
| ¿Por qué no MD5 o SHA-256? | Son **rápidos**: una GPU prueba miles de millones por segundo. BCrypt es **lento a propósito** y el costo se puede subir. |
| ¿Para qué la sal? | Dos usuarios con la misma clave tienen hashes distintos, y las tablas precalculadas (*rainbow tables*) no sirven. |
| ¿Se puede "desencriptar"? | No: es un hash, no un cifrado. Para verificar, `matches(clave, hash)` vuelve a calcular con la misma sal y compara. |
| ¿Dónde se aplica? | `UsuarioServicesImpl.crear` y `actualizar` (`passwordEncoder.encode`), `AdminInicialRunner`, y en el login `passwordEncoder.matches`. |
| ¿Por qué la clave admite entre 8 y 72 caracteres? | 8 como mínimo de seguridad. 72 porque **BCrypt solo usa los primeros 72 bytes**: más caracteres no aportarían nada. |
| ¿Qué pasa con la clave al editar un usuario? | Si no se envía, se conserva la actual (en el PUT no es obligatoria); si se envía, se vuelve a hashear. |

## 2.7 JWT a fondo (con un token real)

**Token real** emitido para `demo_prof001`:
```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkZW1vX3Byb2YwMDEiLCJpZFByb2Zlc29yIjoiREVNTy1QMDAxIiwiaWRVc3VhcmlvIjoyMDU4LCJpc3MiOiJnZXN0aW9uLWFjYWRlbWljYSIsImV4cCI6MTc5NDQ0NzU2OCwiaWF0IjoxNzk0NDE4NzY4LCJyb2wiOiJQUk9GRVNPUiJ9.lQSTX5IDG7gRwbeQj8pk8QUS0AK5WyjnKWskdAJ4gVE
└──────── header ─────┘ └──────────────────────────────────── payload ──────────────────────────────────────────────────┘ └──────────── firma ────────────┘
```

**Decodificado** (Base64URL, cualquiera puede leerlo):
```json
// header
{ "alg": "HS256" }

// payload (claims)
{
  "sub": "demo_prof001",          // subject: el username
  "idProfesor": "DEMO-P001",      // profesor vinculado (lo usa AccesoService)
  "idUsuario": 2058,              // id de la cuenta (para "no te borres a ti mismo")
  "iss": "gestion-academica",     // issuer: quién lo emitió
  "exp": 1794447568,              // expiration: vence (segundos desde 1970) = 8 h después
  "iat": 1794418768,              // issued at: cuándo se emitió
  "rol": "PROFESOR"               // rol → ROLE_PROFESOR
}
```

**Cómo se firma:**
```
firma = Base64URL( HMAC-SHA256( secreto, Base64URL(header) + "." + Base64URL(payload) ) )
```
El servidor recalcula la firma al recibir el token. Si alguien cambia **un solo carácter** del payload (por ejemplo `"rol":"ADMINISTRATIVO"`), la firma ya no coincide y la petición se rechaza. Para fabricar una firma válida hace falta el secreto, que solo tiene el servidor.

**Código que lo genera** ([JwtTokenServices.java:31](src/main/java/com/example/gestionacademica/infrastructure/security/JwtTokenServices.java:31)):
```java
Instant ahora = Instant.now();
Instant expiraEn = ahora.plus(expiracionMinutos, ChronoUnit.MINUTES);      // 480 min

JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
        .issuer("gestion-academica")
        .issuedAt(ahora)
        .expiresAt(expiraEn)
        .subject(usuario.getUsername())
        .claim(AccesoService.CLAIM_USUARIO, usuario.getIdUsuario())
        .claim(AccesoService.CLAIM_ROL, usuario.getRol().name());
if (usuario.getEstudiante() != null) {                                       // solo si es ESTUDIANTE
    claims.claim(AccesoService.CLAIM_ESTUDIANTE, usuario.getEstudiante().getIdEstudiante());
}
if (usuario.getProfesor() != null) {                                         // solo si es PROFESOR
    claims.claim(AccesoService.CLAIM_PROFESOR, usuario.getProfesor().getIdProfesor());
}
JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims.build())).getTokenValue();
return new Sesion(token, expiraEn, usuario);
```

**Qué valida el `JwtDecoder` en cada petición:**
1. Que tenga tres partes y sea JSON válido.
2. Que el algoritmo sea **HS256** (un token con `alg: none` se rechaza).
3. Que la **firma** coincida con el secreto.
4. Que no esté **vencido** (`exp`), con 60 s de tolerancia por diferencias de reloj.

**Por qué meter `idProfesor` / `idEstudiante` en el token:** así `AccesoService` sabe a qué registro académico está vinculado el usuario sin consultar la tabla `usuarios` en cada petición.

**Qué NO va en el token:** la contraseña ni ningún dato sensible, porque el payload se puede leer.

**Por qué los nombres de claims son constantes** (`AccesoService.CLAIM_ROL`, etc.): quien escribe el token (`JwtTokenServices`) y quien lo lee (`AccesoService`) usan la misma constante; si alguien cambia el nombre, cambia en los dos lados.

## 2.8 Del token al rol

```
JWT { rol: "PROFESOR", idProfesor: "DEMO-P001", sub: "demo_prof001", ... }
        │  NimbusJwtDecoder valida
        ▼
     Jwt (objeto con los claims)
        │  JwtAuthenticationConverter + JwtGrantedAuthoritiesConverter
        ▼
     JwtAuthenticationToken
        ├─ principal   = el Jwt (de ahí AccesoService lee los claims)
        ├─ name        = "demo_prof001"  (claim sub)
        └─ authorities = [ROLE_PROFESOR]
        │  se guarda en el SecurityContext de esta petición
        ▼
     authentication  (disponible en @PreAuthorize y como parámetro Authentication)
```

- `hasRole('ADMINISTRATIVO')` pregunta si existe la autoridad `ROLE_ADMINISTRATIVO`.
- `AccesoService` lee los claims así ([AccesoService.java:198](src/main/java/com/example/gestionacademica/infrastructure/security/AccesoService.java:198)):
```java
private String claim(Authentication auth, String nombre) {
    if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {   // pattern matching de Java 21
        return jwt.getClaimAsString(nombre);
    }
    return null;
}
```

## 2.9 Autorización en dos niveles

### Nivel 1: por URL (en los filtros)
En `SecurityConfig`: login público, `/api/usuarios/**` solo ADMINISTRATIVO, todo lo demás autenticado. Es grueso y rápido: se decide antes de llegar al controlador.

### Nivel 2: por método (`@PreAuthorize`)
Activado con `@EnableMethodSecurity`. Cada método del controlador declara su regla con una expresión **SpEL** (*Spring Expression Language*):

```java
@PostMapping
@PreAuthorize("@acceso.puedeCalificarMateria(authentication, #request.idMateria())")
public ResponseEntity<NotaResponse> crear(@Valid @RequestBody NotaRequest request) { ... }
```

| Parte de la expresión | Significado |
|---|---|
| `@acceso` | El bean llamado `acceso` → `AccesoService` (`@Component("acceso")`). |
| `.puedeCalificarMateria(...)` | Método que devuelve `true` o `false`. |
| `authentication` | El usuario autenticado de la petición. |
| `#request` | El parámetro del método llamado `request`. |
| `.idMateria()` | Accesor del `record` `NotaRequest`. |
| `and` | Se pueden combinar condiciones: `puedeModificarNota(...) and puedeCalificarMateria(...)`. |

**Cómo funciona por dentro:** Spring crea un **proxy** (AOP) alrededor del controlador. Antes de ejecutar el método, el proxy evalúa la expresión; si da `false` lanza `AccessDeniedException`, que `ApiExceptionHandler` convierte en **403 `SIN_PERMISO`**.

**Por qué dos niveles:** el primero es una red gruesa (nadie sin token pasa); el segundo es fino y depende de los datos (*¿esta nota es de una materia tuya?*). En `/api/usuarios` la regla está en ambos: si alguien borrara una por error, la otra sigue protegiendo.

**Ejemplo de regla compuesta** (editar una nota):
```java
@PreAuthorize("@acceso.puedeModificarNota(authentication, #id) and @acceso.puedeCalificarMateria(authentication, #request.idMateria())")
```
La primera condición verifica la nota **actual** (que sea de tu materia); la segunda, la materia **nueva** (que no la pases a una materia que no dictas).

## 2.10 AccesoService método por método

Archivo: [AccesoService.java](src/main/java/com/example/gestionacademica/infrastructure/security/AccesoService.java). Es `@Component("acceso")` y `@Transactional(readOnly = true)`.

**Vocabulario del archivo:**
- **Gestor** = ADMINISTRATIVO o COORDINADOR (pueden todo lo académico).
- **Personal** = gestor o PROFESOR (todos menos ESTUDIANTE).

| Método | Devuelve `true` si… | Consulta que hace | Usado en |
|---|---|---|---|
| `esPersonal(auth)` (l. 58) | El rol no es ESTUDIANTE. | Ninguna (solo el claim). | Listados generales, profesores, detalle de asistencia/inscripción. |
| `esGestor(auth)` (l. 64) | Rol ADMINISTRATIVO o COORDINADOR. | Ninguna. | Crear/editar/borrar estructura, personas e inscripciones. |
| `puedeVerEstudiante(auth, id)` (l. 69) | Es personal, **o** es ESTUDIANTE y `id` coincide con su claim `idEstudiante`. | Ninguna. | Notas/asistencia/inscripciones por estudiante, boletín, detalle de estudiante. |
| `puedeVerNota(auth, idNota)` (l. 73) | Es personal, o la nota es del estudiante del token. | `notaRepository.findById`. | `GET /api/notas/{id}`, calificaciones de una nota. |
| `puedeCalificarMateria(auth, idMateria)` (l. 81) | Es gestor, o es PROFESOR con esa materia asignada. | `existsByProfesorIdProfesorAndMateriaIdMateria`. | Crear/editar notas. |
| `puedeModificarNota(auth, idNota)` (l. 89) | Es gestor, o la nota es de una materia del profesor. | `findById` + la anterior. | Editar/borrar notas; crear calificaciones. |
| `puedeModificarMovimiento(auth, idMov)` (l. 100) | Es gestor, o la calificación pertenece a una nota de su materia. | `findById` del movimiento + la anterior. | Editar/borrar calificaciones. |
| `puedeGestionarGrupo(auth, idGrupo)` (l. 111) | Es gestor, o el profesor dicta en ese grupo. | `existsByProfesorIdProfesorAndGrupoIdGrupo`. | Registrar asistencia, reporte en riesgo. |
| `puedeModificarAsistencia(auth, idAsist)` (l. 119) | Es gestor, o la asistencia es de un grupo del profesor. | `findById` + la anterior. | Editar/borrar asistencia. |
| `puedeVerPlanilla(auth, idGrupo, idMateria)` (l. 130) | Es gestor, o el profesor dicta **esa materia en ese grupo**. | `existsBy...GrupoIdGrupoAndMateriaIdMateria`. | Planilla de notas. |
| `idUsuario(auth)` (l. 170) | Devuelve el id de la cuenta del token. | Ninguna. | Reglas de cuenta propia en usuarios. |

**Métodos privados de apoyo:**
- `rol(auth)`: lee el claim `rol` y lo convierte al enum `Rol`.
- `idProfesor(auth)`: devuelve el claim `idProfesor` **solo si el rol es PROFESOR** (aunque un token trajera el claim con otro rol, no se usa).
- `esElEstudiante(auth, id)`: rol ESTUDIANTE y `id` igual a su claim.
- `asignaciones(auth, campo)`: el conjunto de materias o grupos asignados al profesor.

**Decisión de diseño clave: "si no existe, deja pasar"**
```java
Optional<Nota> nota = notaRepository.findById(idNota);
return nota.isEmpty() || puedeCalificarMateria(auth, nota.get().getMateria().getIdMateria());
```
Si la nota no existe, devuelve `true` para que el servicio responda **404 No encontrado**. Si devolviera `false`, el usuario vería un 403 engañoso ("no tienes permiso") sobre algo que no existe.

**Caso del ESTUDIANTE que intenta ver datos ajenos (IDOR):**
```java
public boolean puedeVerEstudiante(Authentication auth, String idEstudiante) {
    return esPersonal(auth) || esElEstudiante(auth, idEstudiante);
}
```
`demo_est0001` pide `/api/notas/estudiante/DEMO-E0002` → no es personal y `DEMO-E0002 ≠ DEMO-E0001` → `false` → **403**. Probado en 2.19.

**Por qué las reglas están en una clase aparte y no en los controladores:** se reutilizan en muchos endpoints, se prueban en un solo lugar y los controladores quedan legibles.

## 2.11 Recorte de listados por rol

Un PROFESOR que llama `GET /api/notas` no recibe todas las notas: solo las de sus materias. Implementación ([AccesoService.java:142](src/main/java/com/example/gestionacademica/infrastructure/security/AccesoService.java:142)):

```java
// Gestores reciben la lista completa; PROFESOR solo lo de sus asignaciones; cualquier otro rol, nada
public <T> List<T> soloMateriasAsignadas(Authentication auth, List<T> items, Function<T, String> idMateria) {
    if (esGestor(auth)) {
        return items;
    }
    Set<String> materias = asignaciones(auth, a -> a.getMateria().getIdMateria());
    return items.stream().filter(item -> materias.contains(idMateria.apply(item))).toList();
}
```

Uso en [NotaController.java](src/main/java/com/example/gestionacademica/presentation/controller/NotaController.java):
```java
@GetMapping
@PreAuthorize("@acceso.esPersonal(authentication)")
public List<NotaResponse> listar(Authentication authentication) {
    return acceso.soloMateriasAsignadas(authentication, notaServices.obtenerTodos(), n -> n.getMateria().getIdMateria()).stream()
            .map(NotaResponse::from).toList();
}
```

**Explicación:**
- `<T>` (genérico): el mismo método sirve para `Nota` y `MovimientoNota`.
- `Function<T, String> idMateria`: le dices cómo sacar la materia de cada elemento (una nota: `n.getMateria().getIdMateria()`; una calificación: `m.getNota().getMateria().getIdMateria()`).
- `Set` para que `contains` sea rápido.
- Primero `@PreAuthorize` bloquea al ESTUDIANTE (403); después el recorte filtra al PROFESOR.

| Listado | Método de recorte | Criterio para el PROFESOR |
|---|---|---|
| `GET /api/notas` | `soloMateriasAsignadas` | Materias que dicta. |
| `GET /api/movimiento-notas` | `soloMateriasAsignadas` | Materia de la nota de cada calificación. |
| `GET /api/asistencia` | `soloGruposAsignados` | Grupos donde dicta. |
| `GET /api/inscripciones` | `soloGruposAsignados` | Grupos donde dicta. |
| `GET /api/estudiantes` | `soloEstudiantesDeSusGrupos` | Estudiantes inscritos en sus grupos. |

**Resultado real con los datos de demostración:** `demo_prof001` (dicta una materia en un grupo de 50 estudiantes) recibe 100 notas, 300 calificaciones, 500 asistencias, 50 inscripciones y 50 estudiantes; el coordinador recibe todo (10 002 notas, 20 002 asistencias, 2002 estudiantes).

## 2.12 Matriz de permisos completa

Leyenda: ✅ todo · 👁 solo consulta · 🔸 solo lo suyo · ❌ prohibido (403)

| Endpoint | ADMIN | COORD | PROFESOR | ESTUDIANTE |
|---|---|---|---|---|
| `POST /api/auth/login` | público | público | público | público |
| `GET /api/auth/me` | ✅ | ✅ | ✅ | ✅ |
| `/api/usuarios/**` | ✅ | ❌ | ❌ | ❌ |
| Carreras, niveles, materias, plan, grupos, asignaciones: `GET` | ✅ | ✅ | 👁 | 👁 |
| Ídem: `POST/PUT/DELETE` | ✅ | ✅ | ❌ | ❌ |
| `GET /api/profesores[/{id}]` | ✅ | ✅ | 👁 | ❌ |
| `POST/PUT/DELETE /api/profesores` | ✅ | ✅ | ❌ | ❌ |
| `GET /api/estudiantes` | ✅ | ✅ | 🔸 inscritos en sus grupos | ❌ |
| `GET /api/estudiantes/{id}` | ✅ | ✅ | 👁 | 🔸 solo él |
| `POST/PUT/DELETE /api/estudiantes` | ✅ | ✅ | ❌ | ❌ |
| `GET /api/inscripciones` | ✅ | ✅ | 🔸 sus grupos | ❌ |
| `GET /api/inscripciones/estudiante/{id}` | ✅ | ✅ | 👁 | 🔸 solo las suyas |
| `GET /api/inscripciones/grupo/{id}`, `/{id}` | ✅ | ✅ | 👁 | ❌ |
| `POST/PUT/DELETE /api/inscripciones` | ✅ | ✅ | ❌ | ❌ |
| `GET /api/notas` | ✅ | ✅ | 🔸 sus materias | ❌ |
| `GET /api/notas/estudiante/{id}[/promedio]` | ✅ | ✅ | 👁 | 🔸 solo las suyas |
| `GET /api/notas/{id}` | ✅ | ✅ | 👁 | 🔸 solo si es suya |
| `POST/PUT/DELETE /api/notas` | ✅ | ✅ | 🔸 sus materias | ❌ |
| `GET /api/movimiento-notas` | ✅ | ✅ | 🔸 sus materias | ❌ |
| `GET /api/movimiento-notas/nota/{id}` | ✅ | ✅ | 👁 | 🔸 solo si la nota es suya |
| `POST/PUT/DELETE /api/movimiento-notas` | ✅ | ✅ | 🔸 sus materias | ❌ |
| `GET /api/asistencia` | ✅ | ✅ | 🔸 sus grupos | ❌ |
| `GET /api/asistencia/estudiante/{id}[/resumen]` | ✅ | ✅ | 👁 | 🔸 solo la suya |
| `POST/PUT/DELETE /api/asistencia` | ✅ | ✅ | 🔸 sus grupos | ❌ |
| `GET /api/reportes/boletin/{id}` | ✅ | ✅ | 👁 | 🔸 solo el suyo |
| `GET /api/reportes/grupo/{id}/en-riesgo` | ✅ | ✅ | 🔸 sus grupos | ❌ |
| `GET /api/reportes/grupo/{id}/planilla-notas` | ✅ | ✅ | 🔸 su materia en su grupo | ❌ |

## 2.13 Errores de seguridad: 401 vs 403

| Código | Significado | Cuándo | `codigo` |
|---|---|---|---|
| **401 No autenticado** | "No sé quién eres." | Sin token | `NO_AUTENTICADO` |
| | | Token alterado, vencido o mal formado | `TOKEN_INVALIDO` |
| | | Usuario o clave incorrectos | `CREDENCIALES_INVALIDAS` |
| **403 Acceso denegado** | "Sé quién eres, pero no puedes." | Rol insuficiente o recurso ajeno | `SIN_PERMISO` |
| | | Cuenta desactivada (clave correcta) | `CUENTA_DESACTIVADA` |

**Dos lugares generan estos errores, con el mismo formato:**

| Dónde ocurre | Quién responde | Casos |
|---|---|---|
| En los **filtros** (antes del controlador) | [ErroresSeguridadHandler](src/main/java/com/example/gestionacademica/infrastructure/security/ErroresSeguridadHandler.java) (`AuthenticationEntryPoint` + `AccessDeniedHandler`) | Sin token, token inválido, reglas por URL. |
| **Dentro** del controlador | [ApiExceptionHandler](src/main/java/com/example/gestionacademica/presentation/controller/ApiExceptionHandler.java) (`@RestControllerAdvice`) | `@PreAuthorize` (`AccessDeniedException`), login fallido, cuenta desactivada. |

¿Por qué dos? Porque un `@RestControllerAdvice` **solo** atrapa excepciones que ocurren dentro del `DispatcherServlet`; lo que falla en un filtro nunca llega ahí. Ambos usan `CuerpoError.crear(...)` para que el JSON sea idéntico.

```java
// ErroresSeguridadHandler.commence: sin cabecera → no inició sesión; con cabecera → el token no sirve
if (request.getHeader(HttpHeaders.AUTHORIZATION) == null) {
    escribir(response, HttpStatus.UNAUTHORIZED, "NO_AUTENTICADO", "Debes iniciar sesión para usar este recurso");
} else {
    escribir(response, HttpStatus.UNAUTHORIZED, "TOKEN_INVALIDO", "El token es inválido o está vencido: inicia sesión de nuevo");
}
```

**Por qué el mensaje del 403 es genérico** ("Tu rol no tiene permiso para esta operación") y no dice *por qué*: no se le da al atacante información sobre la estructura de permisos ni sobre qué registros existen.

## 2.14 CORS a fondo

**El problema:** el front corre en `http://localhost:5173` y la API en `http://localhost:8080`. Son **orígenes distintos** (cambia el puerto). Por seguridad, el navegador bloquea que JavaScript de un origen lea respuestas de otro, salvo que el servidor lo autorice con cabeceras `Access-Control-Allow-*`.

**El preflight:** antes de un `POST` con `Authorization` y `Content-Type: application/json`, el navegador envía:
```http
OPTIONS /api/notas
Origin: http://localhost:5173
Access-Control-Request-Method: POST
Access-Control-Request-Headers: authorization,content-type
```
**Respuesta real de la API:**
```http
HTTP/1.1 200
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS
Access-Control-Allow-Headers: authorization, content-type
Access-Control-Max-Age: 3600
```
Un origen no permitido (`http://evil.example`) recibe **403**.

**Configuración** ([CorsConfig.java](src/main/java/com/example/gestionacademica/presentation/config/CorsConfig.java)):
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:5174", "http://localhost:5175"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    config.setMaxAge(3600L);   // el navegador recuerda el permiso 1 hora y no repite el preflight
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

| Decisión | Por qué |
|---|---|
| Lista explícita de orígenes, no `*` | Solo el front propio puede llamar desde un navegador. |
| Solo cabeceras `Authorization` y `Content-Type` | Mínimo privilegio: son las únicas que el front necesita. |
| Sin `allowCredentials` | No se usan cookies; el token va en una cabecera. |
| `.cors()` en `SecurityConfig` | El `CorsFilter` se ejecuta **antes** de la autenticación; así el preflight (que no trae token) se responde 200 y no 401. Además los errores 401/403 también llevan las cabeceras CORS, y el front puede leer su mensaje. |

**Importante:** CORS **no** protege la API de herramientas como curl o Postman; solo controla qué páginas web pueden llamarla desde un navegador. La protección real es el JWT.

## 2.15 CSRF y sesiones sin estado

**CSRF (*Cross-Site Request Forgery*):** una página maliciosa hace que el navegador de la víctima envíe una petición a tu sitio; el navegador **adjunta automáticamente las cookies de sesión** y la petición parece legítima.

**Por qué aquí está desactivado y es seguro:**
1. No hay cookies de sesión (`STATELESS`).
2. El token viaja en la cabecera `Authorization`, que el navegador **nunca** agrega solo: el código JavaScript del front tiene que ponerla explícitamente.
3. Una página maliciosa no puede leer el token del front (otro origen) ni agregar esa cabecera a una petición entre sitios sin pasar por CORS.

**Ventajas de `STATELESS`:** el servidor no guarda sesiones en memoria, cualquier instancia puede atender cualquier petición (escala horizontalmente) y no hay fijación de sesión (*session fixation*).

**Desventaja (honesta):** un token emitido no se puede invalidar antes de que venza (ver Parte VI).

## 2.16 Gestión segura de usuarios

Todo en [UsuarioServicesImpl.java](src/main/java/com/example/gestionacademica/application/Services/impl/UsuarioServicesImpl.java) y [UsuarioController.java](src/main/java/com/example/gestionacademica/presentation/controller/UsuarioController.java).

| Regla | Implementación | Error |
|---|---|---|
| Solo ADMINISTRATIVO administra cuentas | Regla por URL + `@PreAuthorize("hasRole('ADMINISTRATIVO')")` en la clase. | 403 |
| La clave nunca se guarda ni se devuelve en texto | `passwordEncoder.encode(password)`; `UsuarioResponse` no tiene campo de clave. | — |
| Clave de 8 a 72 caracteres | `@Size(min = 8, max = 72)` en `UsuarioRequest`. | 400 `VALIDACION` |
| Username único | `existsByUsername` (+ índice único). | 409 `USERNAME_DUPLICADO` |
| ESTUDIANTE debe vincular un estudiante; PROFESOR un profesor; los demás, nada | `validarVinculo` (l. 111). | 409 `VINCULO_INVALIDO` |
| El estudiante o profesor vinculado debe existir | `Referencias.estudianteOpcional/profesorOpcional` al convertir el DTO. | 404 `ESTUDIANTE_NO_ENCONTRADO` / `PROFESOR_NO_ENCONTRADO` |
| Un solo usuario por estudiante o profesor | `existsByEstudianteIdEstudiante` / `existsByProfesorIdProfesor` (+ índices únicos). | 409 `VINCULO_DUPLICADO` |
| Nadie se borra a sí mismo | `eliminar(id, idUsuarioActual)`: compara con el `idUsuario` del token. | 409 `CUENTA_PROPIA` |
| Nadie se desactiva ni se quita el rol ADMINISTRATIVO | `actualizar(...)`: `dejaDeSerAdminActivo && id.equals(idUsuarioActual)`. | 409 `CUENTA_PROPIA` |
| Siempre queda al menos un ADMINISTRATIVO activo | `exigirOtroAdminActivo()`: `countByRolAndActivoTrue(ADMINISTRATIVO) <= 1` → error. | 409 `ULTIMO_ADMINISTRATIVO` |
| Una cuenta desactivada no entra | `AuthServicesImpl.login`. | 403 `CUENTA_DESACTIVADA` |
| Siempre existe un administrador inicial | `AdminInicialRunner`: al arrancar, si no hay ADMINISTRATIVO, crea `admin` con la clave de `app.admin.password` (hasheada). Si la clave está vacía, no lo crea y lo advierte en el log. | — |

**El id del usuario actual sale del token, no del cuerpo de la petición:**
```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication authentication) {
    usuarioServices.eliminar(id, acceso.idUsuario(authentication));
    return ResponseEntity.noContent().build();
}
```
Si viniera en el JSON, el cliente podría mentir. El token está firmado, así que es confiable.

**Por qué la regla del "último administrativo" también es necesaria:** el token de un administrador desactivado sigue siendo válido hasta que vence. Sin esta regla, ese administrador podría desactivar al único activo y dejar el sistema sin nadie que administre cuentas. Hay una prueba automática exactamente de este caso.

**Asignación masiva (*mass assignment*):** el cliente no puede enviar `passwordHash`, `idUsuario` ni campos internos porque el `UsuarioRequest` no los tiene; solo se copian los campos permitidos.

## 2.17 Manejo de secretos y configuración

| Práctica | Cómo se aplica |
|---|---|
| Los secretos no están en el repositorio | `application-local.properties` (clave de MySQL, secreto JWT, clave del admin) está en `.gitignore`. Se sube un `.example` sin valores. |
| Alternativa por variables de entorno | `${DB_PASSWORD:}`, `${JWT_SECRET:}`, `${ADMIN_PASSWORD:}` en `application.properties`. |
| *Fail fast* | Si el secreto JWT tiene menos de 32 caracteres, la app no arranca. |
| Secreto distinto en pruebas | `src/test/resources/application.properties` usa uno propio; nunca el real. |
| No se filtran detalles internos | El 500 responde un mensaje genérico; el *stack trace* queda solo en el log. |
| Duración limitada del token | `app.jwt.expiracion-minutos=480` (8 horas). |

**Pregunta probable:** *"¿Qué pasa si se filtra el secreto JWT?"* Cualquiera podría fabricar tokens válidos con cualquier rol. Habría que cambiar el secreto inmediatamente (invalida todos los tokens emitidos) y revisar el acceso. Por eso no está en git.

## 2.18 Defensa en profundidad

Varias barreras independientes protegen la misma regla. Si una falla, la siguiente sigue funcionando.

| Capa | Ejemplo: "un profesor solo califica sus materias" | Ejemplo: "no inscribir dos veces" |
|---|---|---|
| 1. Front | Oculta el botón de calificar en otras materias. | Oculta estudiantes ya inscritos. |
| 2. Filtros | El token debe ser válido. | El token debe ser válido. |
| 3. `@PreAuthorize` | `puedeModificarNota` → 403. | `esGestor` → solo gestores inscriben. |
| 4. Validación DTO | `calificacion` entre 0.0 y 5.0. | `anioInscripcion` obligatorio. |
| 5. Servicio | — | `existsByEstudiante...AndAnioInscripcion` → 409 `INSCRIPCION_DUPLICADA`. |
| 6. Base de datos | FK `ID_Nota` | Índice único `uk_inscripcion` → 409 `INTEGRIDAD_DATOS`. |

**Frase clave:** *"El front oculta botones para la experiencia del usuario, pero la seguridad real está en el servidor: aunque alguien llame la API con Postman, se aplica la misma regla."*

## 2.19 Ataques probados contra la API (resultados reales)

Todas estas pruebas se ejecutaron contra la aplicación corriendo.

### 1. Alterar el token para subirse de rol
Se tomó el token de `demo_prof001`, se cambió `"rol":"PROFESOR"` por `"rol":"ADMINISTRATIVO"` en el payload, se dejó la firma original y se llamó `GET /api/usuarios`:
```json
{"status":401,"error":"No autenticado","codigo":"TOKEN_INVALIDO",
 "message":"El token es inválido o está vencido: inicia sesión de nuevo"}
```
**Por qué falla:** la firma HMAC se calculó sobre el payload original; al cambiarlo ya no coincide.

### 2. Token sin firma (`alg: none`)
Token fabricado con header `{"alg":"none"}`, payload `{"sub":"admin","rol":"ADMINISTRATIVO"}` y sin firma:
```json
{"status":401,"codigo":"TOKEN_INVALIDO", ...}
```
**Por qué falla:** el `JwtDecoder` está configurado para aceptar **solo HS256** (`.macAlgorithm(MacAlgorithm.HS256)`). Este es un ataque clásico contra implementaciones de JWT mal configuradas.

### 3. Inyección SQL en el login
```json
{ "username": "admin' OR '1'='1", "password": "x' OR '1'='1" }
```
```json
{"status":401,"codigo":"CREDENCIALES_INVALIDAS","message":"Usuario o contraseña incorrectos"}
```
**Por qué falla:** Spring Data JPA usa **consultas parametrizadas** (`PreparedStatement`): el texto se envía como **dato**, nunca se concatena al SQL. Además la clave no se compara en SQL, sino con BCrypt en Java.

### 4. Profesor que califica una materia que no dicta
`demo_prof001` intenta `POST /api/movimiento-notas` sobre una nota de otra materia:
```json
{"status":403,"error":"Acceso denegado","codigo":"SIN_PERMISO","message":"Tu rol no tiene permiso para esta operación"}
```
**Por qué falla:** `puedeModificarNota` consulta las asignaciones del profesor.

### 5. Estudiante que mira las notas de otro (IDOR)
`demo_est0001` pide `/api/notas/estudiante/DEMO-E0001` → **200**; pide `/api/notas/estudiante/DEMO-E0002` → **403 `SIN_PERMISO`**.

### 6. Profesor que entra a la administración de usuarios
`GET /api/usuarios` con token de profesor → **403 `SIN_PERMISO`** (lo bloquea la regla por URL en los filtros).

### 7. Sin token
`GET /api/notas` sin cabecera → **401 `NO_AUTENTICADO`** ("Debes iniciar sesión para usar este recurso").

### 8. Cuenta desactivada
Login de `demo_est0020` (inactiva) con la clave correcta → **403 `CUENTA_DESACTIVADA`** ("Tu cuenta está desactivada"). Con clave incorrecta → 401 genérico (no revela que la cuenta existe).

### 9. Clave débil al crear un usuario
`"password": "123"` →
```json
{"status":400,"codigo":"VALIDACION","message":"Campos inválidos u obligatorios faltantes",
 "errores":{"password":"password debe tener entre 8 y 72 caracteres"}}
```

### 10. Origen web no autorizado
Preflight desde `http://evil.example` → **403**: el navegador no deja que esa página use la API.

## 2.20 OWASP Top 10 aplicado al proyecto

OWASP Top 10 (2021) es la lista de los riesgos de seguridad web más comunes.

| Riesgo | Qué es | Cómo lo mitiga el proyecto |
|---|---|---|
| **A01 Control de acceso roto** | Acceder a lo que no te corresponde (IDOR, escalar rol). | Reglas por URL + `@PreAuthorize` + `AccesoService` que verifica dueño y asignaciones; listados recortados en el servidor; rol dentro de un token firmado. |
| **A02 Fallas criptográficas** | Claves débiles, datos sensibles sin proteger. | BCrypt con sal; HS256 con secreto ≥ 256 bits obligatorio; el token no lleva datos sensibles. |
| **A03 Inyección** | SQL u otros comandos inyectados en la entrada. | JPA con consultas parametrizadas; ningún SQL armado con concatenación; validación de entrada. |
| **A04 Diseño inseguro** | Fallas en las reglas mismas. | Reglas de negocio en el servidor (cupo, último administrativo, cuenta propia, no justificar asistencias). |
| **A05 Mala configuración** | CORS abierto, errores con detalles internos, valores por defecto inseguros. | CORS con lista blanca; 500 sin *stack trace*; `ddl-auto=none`; secreto obligatorio; cabeceras de seguridad por defecto de Spring. |
| **A06 Componentes vulnerables** | Librerías desactualizadas. | Versiones actuales: Spring Boot 4.1.1, Spring Security 7.1.1, Java 21. |
| **A07 Fallas de identificación y autenticación** | Enumeración de usuarios, claves débiles, sesiones eternas. | Mismo mensaje para usuario o clave errados; clave ≥ 8; token que vence en 8 h; cuenta desactivada bloqueada. |
| **A08 Integridad de software y datos** | Datos manipulados sin detectarlo. | Firma del JWT: cualquier cambio se detecta. DTOs que impiden asignación masiva. |
| **A09 Registro y monitoreo** | No saber qué pasó. | Errores 500 registrados en el log con su detalle. (Mejorable: no hay auditoría de cambios.) |
| **A10 SSRF** | El servidor hace peticiones a URLs que manda el atacante. | No aplica: la API no hace peticiones salientes. |

## 2.21 Pruebas automáticas de seguridad

Archivo: [SeguridadYDtosIntegrationTest.java](src/test/java/com/example/gestionacademica/SeguridadYDtosIntegrationTest.java). Usa `@SpringBootTest` + `MockMvc` sobre H2 en memoria. Se ejecuta con `./mvnw test` (16 pruebas, todas pasan).

| Prueba | Qué verifica |
|---|---|
| `loginCorrectoDevuelveTokenYDatosDelUsuario` | Login devuelve token, tipo Bearer, datos del usuario **sin `passwordHash`**. |
| `loginConClaveErradaDa401` | Clave incorrecta → 401. |
| `sinTokenDa401` | Ruta protegida sin token → 401. |
| `adminInicialSeCreaAlArrancar` | `AdminInicialRunner` crea `admin`. |
| `estudianteSoloVeSuInformacion` | Ve lo suyo (200), lo ajeno 403, listados generales 403, no crea carreras. |
| `profesorCalificaSoloSusMaterias` | Califica su materia (y la definitiva se recalcula); otra materia 403; planilla ajena 403; usuarios 403. |
| `profesorSoloRecibeSusMateriasYGrupos` | Los cinco listados recortados. |
| `gestorRecibeListadosCompletos` | El coordinador ve todo. |
| `coordinadorGestionaLoAcademicoPeroNoLosUsuarios` | Crea materias, pero `/api/usuarios` 403. |
| `administrativoCreaUsuariosVinculados` | Crea un profesor vinculado; sin vínculo → 409. |
| `erroresLlevanCodigoYDescripcionEnEspanol` | 401 `NO_AUTENTICADO` y `TOKEN_INVALIDO`, `CREDENCIALES_INVALIDAS`, 400, 415, 403, 404, 405 con su formato. |
| `preflightCorsNoExigeToken` | `OPTIONS` desde 5173 → 200; desde otro origen → 403. |
| `administrativoNoSeBorraNiDejaElSistemaSinAdministrativos` | `CUENTA_PROPIA`, vínculo inexistente/duplicado, `ULTIMO_ADMINISTRATIVO` con token de un admin desactivado y login 403 `CUENTA_DESACTIVADA`. |

## 2.22 Guion de demostración en vivo

Arranca la app (IntelliJ o `./mvnw spring-boot:run`) y usa Postman (carpeta `postman/`) o estos comandos. Clave de los usuarios demo: `Demo1234*`.

1. **Login de un profesor** y muestra el token:
```bash
curl -s -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"username\":\"demo_prof001\",\"password\":\"Demo1234*\"}"
```
2. **Pega el token en jwt.io** (o decodifícalo) y muestra los claims `rol` e `idProfesor`. Explica que se puede leer pero no modificar.
3. **Sin token** → 401 `NO_AUTENTICADO`:
```bash
curl -s http://localhost:8080/api/notas
```
4. **El profesor solo ve sus notas** (cuenta los registros frente al coordinador). Reemplaza `TOKEN` por el del paso 1:
```bash
curl -s -H "Authorization: Bearer TOKEN" http://localhost:8080/api/notas
```
5. **El profesor intenta administrar usuarios** → 403:
```bash
curl -s -H "Authorization: Bearer TOKEN" http://localhost:8080/api/usuarios
```
6. **Un estudiante pide las notas de otro** → 403 (inicia sesión como `demo_est0001` y pide `/api/notas/estudiante/DEMO-E0002`).
7. **Cuenta desactivada** → 403:
```bash
curl -s -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"username\":\"demo_est0020\",\"password\":\"Demo1234*\"}"
```
8. **Regla de negocio:** como `admin`, intenta borrar tu propia cuenta → 409 `CUENTA_PROPIA`.
9. **Implementación:** registra una calificación con el profesor sobre una nota suya y muestra cómo cambia la `definitiva` de esa nota.

---

# Parte III. Implementaciones

## 3.1 Patrón puerto–adaptador (repositorios)

Para cada entidad hay tres piezas. Ejemplo con `Nota`:

```java
// 1. PUERTO (domain/repository): lo que el negocio necesita, sin tecnología
public interface NotaRepository {
    Nota save(Nota nota);
    Optional<Nota> findById(Long id);
    List<Nota> findByEstudianteIdEstudiante(String idEstudiante);
    ...
}

// 2. SPRING DATA (infrastructure/persistence): Spring genera la implementación y el SQL
public interface NotaJpaRepository extends JpaRepository<Nota, Long> {
    List<Nota> findByEstudianteIdEstudiante(String idEstudiante);   // → WHERE ID_Estudiante = ?
}

// 3. ADAPTADOR (infrastructure/persistence): implementa el puerto delegando en Spring Data
@Repository
public class NotaRepositoryAdapter implements NotaRepository {
    private final NotaJpaRepository jpaRepository;
    public NotaRepositoryAdapter(NotaJpaRepository jpaRepository) { this.jpaRepository = jpaRepository; }
    @Override
    public List<Nota> findByEstudianteIdEstudiante(String id) { return jpaRepository.findByEstudianteIdEstudiante(id); }
    ...
}
```

**Consultas derivadas:** Spring Data lee el nombre del método y genera el SQL.
- `findBy` + `Estudiante` + `IdEstudiante` → navega `nota.estudiante.idEstudiante`.
- `existsBy...And...` → `SELECT COUNT(*) > 0 ... WHERE ... AND ...`.
- `countByRolAndActivoTrue` → `COUNT(*) WHERE Rol = ? AND Activo = true`.

**Por qué:** el servicio depende de `NotaRepository` (interfaz), no de JPA. Si se cambia la tecnología, solo se reescribe el adaptador (principio de **inversión de dependencias**).

## 3.2 DTOs, Referencias y grupos de validación

**DTO de entrada** (`record` con validaciones y conversión):
```java
// "aprobada" no se recibe: el servicio la calcula a partir de la definitiva
public record NotaRequest(
        @DecimalMin("0.0") @DecimalMax("5.0") Float definitiva,
        @NotNull LocalDate fechaRegistro,
        @NotBlank String idEstudiante,
        @NotBlank String idMateria) {

    public Nota toEntity(Referencias ref) {
        return Nota.builder()
                .definitiva(definitiva)
                .fechaRegistro(fechaRegistro)
                .estudiante(ref.estudiante(idEstudiante))   // carga la entidad real o 404
                .materia(ref.materia(idMateria))
                .build();
    }
}
```

**DTO de salida** (aplanado):
```java
public record NotaResponse(Long idNota, Float definitiva, LocalDate fechaRegistro, Boolean aprobada,
                           String idEstudiante, String nombreEstudiante,
                           String idMateria, String nombreMateria) {
    public static NotaResponse from(Nota n) { ... }
}
```

**`Referencias`** ([Referencias.java](src/main/java/com/example/gestionacademica/presentation/mapper/Referencias.java)): convierte los ids del JSON en entidades reales con los servicios. Si el id no existe → 404 (`ESTUDIANTE_NO_ENCONTRADO`, etc.). Así las respuestas traen los nombres completos.

**Grupo de validación `AlCrear`:**
```java
public interface AlCrear extends Default { }
```
- En el `POST` se usa `@Validated(AlCrear.class)`: se exigen las reglas normales **y** las marcadas con `groups = AlCrear.class` (por ejemplo, el `idCarrera` o el `password`).
- En el `PUT` se usa `@Valid`: el id viene en la URL y la clave es opcional.

**Beneficios de seguridad de los DTO:** no se exponen campos internos (`passwordHash`), no hay asignación masiva y se evita serializar proxies *lazy* de Hibernate o ciclos infinitos.

## 3.3 Transacciones

- Todos los servicios tienen `@Transactional` (los de lectura, `readOnly = true`).
- Si un método lanza una `RuntimeException`, se hace **rollback** de todo lo que hizo.
- Ejemplo crítico: crear una calificación inserta el movimiento **y** actualiza la definitiva de la nota. O se guardan ambos, o ninguno.
- Por eso todas las excepciones del proyecto extienden `RuntimeException`.

## 3.4 Definitiva automática de las notas

```java
// MovimientoNotaServicesImpl.crear
MovimientoNota guardado = movimientoNotaRepository.save(movimientoNota);
notaServices.recalcularDefinitiva(nota.getIdNota());

// NotaServicesImpl.recalcularDefinitiva
List<MovimientoNota> movimientos = movimientoNotaRepository.findByNotaIdNota(idNota);
if (movimientos.isEmpty()) {
    nota.setDefinitiva(null);                         // en curso
} else {
    nota.setDefinitiva(promedioMovimientos(movimientos));   // promedio redondeado a 2 decimales
}
nota.setAprobada(calcularAprobada(nota.getDefinitiva()));   // >= 3.0 → true; null si en curso
```
- Se llama al **crear, editar y borrar** una calificación.
- Si una calificación se mueve a otra nota, se recalculan **las dos**.
- Si la nota ya tiene calificaciones, la definitiva no se puede editar a mano (sale del promedio).
- No se puede borrar una nota con calificaciones (409 `TIENE_DEPENDENCIAS`).
- Redondeo: `Math.round(valor * 100.0) / 100.0`.

## 3.5 Control de cupo

```java
// InscripcionServicesImpl.validar
Grupo grupo = buscarGrupo(idGrupo);
if (grupo.getCupoMaximo() != null) {                                   // null = sin límite
    long inscritos = inscripcionRepository.countByGrupoIdGrupoAndAnioInscripcion(idGrupo, anio);
    if (inscritos >= grupo.getCupoMaximo()) {
        throw new BusinessException("CUPO_LLENO", "El grupo ... no tiene cupo disponible ...");
    }
}
```
Antes se valida que el estudiante esté activo (`ESTUDIANTE_INACTIVO`) y que no esté ya inscrito ese año (`INSCRIPCION_DUPLICADA`). `GET /api/grupos/{id}/cupo` devuelve `cupoMaximo`, `inscritos` y `disponibles`. Al editar, solo se revalida si cambia el estudiante, el grupo o el año (para no chocar con la propia inscripción).

## 3.6 Reglas de asistencia

```java
// AsistenciaServicesImpl.validar
if (fecha.isAfter(LocalDate.now()))                 → FECHA_FUTURA
if (!inscripcionRepository.existsByEstudiante...AndGrupo...) → ESTUDIANTE_NO_INSCRITO
if (asistenciaRepository.existsBy...AndFechaAsistencia(...)) → ASISTENCIA_DUPLICADA
// validarJustificacion
if (asistio && justificada)                         → JUSTIFICACION_INVALIDA
```
El resumen (`/api/asistencia/estudiante/{id}/resumen`) calcula total, asistidas, inasistencias, justificadas y porcentaje con 2 decimales (`null` si no hay registros).

## 3.7 Notas duplicadas y materias repetidas

```java
// NotaServicesImpl.validarDuplicado
for (Nota existente : notaRepository.findByEstudianteIdEstudianteAndMateriaIdMateria(idEstudiante, idMateria)) {
    if (existente.getAprobada() == null || existente.getAprobada()) {
        throw new BusinessException("NOTA_DUPLICADA", "... ya tiene una nota en curso o aprobada ...");
    }
}
```
Un estudiante no puede tener dos notas **en curso o aprobadas** de la misma materia, pero sí puede **repetir** una que reprobó. En la planilla se usa la nota más reciente (`notaMasReciente`, el `idNota` mayor).

## 3.8 Asignación de profesores

`AsignacionMateriaProfesorServicesImpl.validar`:
1. El profesor existe y está **activo** → `PROFESOR_INACTIVO`.
2. La materia está en el **plan del nivel** del grupo (`niveles_materias`) → `MATERIA_FUERA_DEL_NIVEL`.
3. **Un solo profesor** por materia en cada grupo → `MATERIA_YA_ASIGNADA`.

Al editar, si solo cambia el profesor, no se revisa el duplicado (chocaría consigo misma). Estas asignaciones son **la base de los permisos** del profesor en `AccesoService`.

## 3.9 Reportes

`ReporteServicesImpl` (`@Transactional(readOnly = true)`):

| Reporte | Qué arma |
|---|---|
| `boletin(idEstudiante)` | Datos del estudiante, grupos inscritos, cada materia con definitiva, estado ("Aprobada", "Reprobada", "En curso") y número de calificaciones, resumen de notas y de asistencia. |
| `estudiantesEnRiesgo(idGrupo, anio)` | Inscritos del grupo con **promedio < 3.0** o **asistencia en ese grupo < 80 %**, con la lista de motivos. |
| `planillaNotas(idGrupo, idMateria, anio)` | Valida que la materia sea del nivel; profesor asignado (o "Sin asignar"); cada inscrito con su nota más reciente; aprobados, reprobados y promedio del grupo. |

Devuelven `Map<String, Object>` con `LinkedHashMap` para que el JSON conserve el orden de los campos. Si no se envía `anio`, el controlador usa el año actual.

## 3.10 Manejo de errores unificado

**Formato** ([CuerpoError.java](src/main/java/com/example/gestionacademica/presentation/dto/CuerpoError.java)):
```json
{ "timestamp": "...", "status": 409, "error": "Conflicto", "codigo": "CUPO_LLENO", "message": "El grupo ... no tiene cupo ..." }
```

**`BusinessException` con código estable:**
```java
public class BusinessException extends RuntimeException {
    public static final String CODIGO_GENERICO = "REGLA_NEGOCIO";
    private final String codigo;
    public BusinessException(String codigo, String message) { super(message); this.codigo = codigo; }
    public String getCodigo() { return codigo; }
}
```
El front puede reaccionar al `codigo` sin depender del texto del mensaje.

**Qué maneja [ApiExceptionHandler](src/main/java/com/example/gestionacademica/presentation/controller/ApiExceptionHandler.java):**

| Excepción | HTTP | `codigo` |
|---|---|---|
| 13 `XxxNotFoundException` | 404 | `ESTUDIANTE_NO_ENCONTRADO`, `NOTA_NO_ENCONTRADA`… (mapa por clase) |
| `NoResourceFoundException` | 404 | `RUTA_NO_ENCONTRADA` |
| `BusinessException` | 409 | el de la regla |
| `DataIntegrityViolationException` | 409 | `INTEGRIDAD_DATOS` |
| `MethodArgumentNotValidException` | 400 | `VALIDACION` + `errores` |
| `HttpMessageNotReadableException` | 400 | `CUERPO_INVALIDO` |
| `MissingServletRequestParameterException` | 400 | `PARAMETRO_FALTANTE` |
| `MethodArgumentTypeMismatchException` | 400 | `PARAMETRO_INVALIDO` (ej. `/api/notas/abc`) |
| `HttpRequestMethodNotSupportedException` | 405 | `METODO_NO_PERMITIDO` |
| `HttpMediaTypeNotSupportedException` | 415 | `TIPO_CONTENIDO_NO_SOPORTADO` |
| `CredencialesInvalidasException` | 401 | `CREDENCIALES_INVALIDAS` |
| `CuentaDesactivadaException` | 403 | `CUENTA_DESACTIVADA` |
| `AccessDeniedException` | 403 | `SIN_PERMISO` |
| `Exception` (cualquier otra) | 500 | `ERROR_INTERNO` (detalle solo en el log) |

## 3.11 Borrado protegido

Ningún `eliminar` rompe llaves foráneas: antes de borrar se cuentan las dependencias y, si hay, se responde 409 `TIENE_DEPENDENCIAS` diciendo cuántas hay. Ejemplos: una carrera con niveles, un grupo con inscripciones/asignaciones/asistencias, un estudiante con inscripciones/notas/asistencias, una nota con calificaciones.

## 3.12 Datos de demostración

- `db/usuarios_masivos.sql`: 3000 usuarios (2000 estudiantes, 700 profesores, 250 coordinadores, 50 administrativos; 1 de cada 20 inactivo) con sus estudiantes y profesores `DEMO-*`. Todos con el mismo hash BCrypt de `Demo1234*`.
- `db/datos_academicos_demo.sql`: 5 carreras, 20 niveles, 100 materias, 40 grupos, 200 asignaciones, 2000 inscripciones, 10 000 notas, 30 000 calificaciones, 20 000 asistencias.
- Generados con **CTE recursivas** de MySQL 8 (`WITH RECURSIVE n(i) AS (SELECT 1 UNION ALL SELECT i + 1 FROM n WHERE i < 2000)`) y tablas temporales.
- Se deshacen con los scripts `*_borrar.sql` (primero el académico).

## 3.13 Todas las reglas de negocio

| # | Regla | Código |
|---|---|---|
| 1 | No repetir id al crear | `YA_EXISTE` |
| 2 | Correo único de estudiantes y profesores | `CORREO_DUPLICADO` |
| 3 | No borrar con dependencias | `TIENE_DEPENDENCIAS` |
| 4 | Materia una sola vez por nivel | `MATERIA_YA_ASOCIADA` |
| 5 | Solo profesores activos reciben asignaciones | `PROFESOR_INACTIVO` |
| 6 | Materia del plan del nivel del grupo | `MATERIA_FUERA_DEL_NIVEL` |
| 7 | Un profesor por materia en cada grupo | `MATERIA_YA_ASIGNADA` |
| 8 | Solo estudiantes activos se inscriben | `ESTUDIANTE_INACTIVO` |
| 9 | No inscribir dos veces el mismo año | `INSCRIPCION_DUPLICADA` |
| 10 | Respetar el cupo máximo | `CUPO_LLENO` |
| 11 | No asistencia futura | `FECHA_FUTURA` |
| 12 | Asistencia solo de inscritos | `ESTUDIANTE_NO_INSCRITO` |
| 13 | Una asistencia por día | `ASISTENCIA_DUPLICADA` |
| 14 | Solo se justifica una falta | `JUSTIFICACION_INVALIDA` |
| 15 | No dos notas en curso/aprobadas de una materia | `NOTA_DUPLICADA` |
| 16 | Definitiva = promedio de calificaciones; aprueba con ≥ 3.0 | — |
| 17 | Calificaciones entre 0.0 y 5.0 | `VALIDACION` |
| 18 | En riesgo: promedio < 3.0 o asistencia < 80 % | — |
| 19 | Vínculo correcto según el rol | `VINCULO_INVALIDO` |
| 20 | Un usuario por estudiante/profesor | `VINCULO_DUPLICADO` |
| 21 | Username único | `USERNAME_DUPLICADO` |
| 22 | No borrarse/desactivarse a sí mismo | `CUENTA_PROPIA` |
| 23 | Siempre un ADMINISTRATIVO activo | `ULTIMO_ADMINISTRATIVO` |
| 24 | Cuenta desactivada no entra | `CUENTA_DESACTIVADA` |

---

# Parte IV. Glosarios

## 4.1 Anotaciones

### Spring y seguridad
| Anotación | Qué hace |
|---|---|
| `@SpringBootApplication` | Clase principal: configuración + autoconfiguración + escaneo de componentes. |
| `@Configuration` / `@Bean` | Clase que declara beans; el método `@Bean` registra el objeto que devuelve. |
| `@Component`, `@Service`, `@Repository` | Spring crea y administra la clase (bean). `@Component("acceso")` le da nombre para usarla en SpEL. |
| `@Value("${...}")` | Inyecta un valor de configuración. |
| `@EnableMethodSecurity` | Activa `@PreAuthorize`. |
| `@PreAuthorize("expr")` | Evalúa la expresión antes de ejecutar el método; si es `false` → 403. |
| `@Transactional` / `readOnly = true` | Transacción con *commit* o *rollback* automático. |

### Web
| Anotación | Qué hace |
|---|---|
| `@RestController` | Controlador que devuelve JSON. |
| `@RequestMapping` | Prefijo de URL de la clase. |
| `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` | Verbo + ruta. |
| `@PathVariable` | Valor de la ruta (`/{id}`). |
| `@RequestParam` | Parámetro `?anio=2026` (`required = false` lo hace opcional). |
| `@RequestBody` | JSON del cuerpo → objeto. |
| `@Valid` / `@Validated(Grupo.class)` | Ejecuta las validaciones (con o sin grupo). |
| `@RestControllerAdvice` / `@ExceptionHandler` | Manejo global de excepciones. |

### JPA
| Anotación | Qué hace |
|---|---|
| `@Entity` / `@Table` | La clase es una tabla. |
| `@Id` / `@GeneratedValue(IDENTITY)` | Llave primaria / generada por la BD (`AUTO_INCREMENT`). |
| `@Column` | Nombre, longitud, nulo, único. |
| `@ManyToOne(fetch = LAZY)` / `@JoinColumn` | Relación muchos-a-uno cargada bajo demanda / columna de la FK. |
| `@Enumerated(EnumType.STRING)` | Guarda el enum como texto. |

### Lombok y validación
| Anotación | Qué hace |
|---|---|
| `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder` | Generan código al compilar. |
| `@NotNull`, `@NotBlank`, `@Size`, `@Email`, `@PastOrPresent`, `@Min`, `@DecimalMin`, `@DecimalMax` | Reglas de validación con `message` en español. |

### Pruebas
| Anotación | Qué hace |
|---|---|
| `@SpringBootTest` | Levanta la app completa. |
| `@AutoConfigureMockMvc` | Prepara `MockMvc`. |
| `@Sql` | Carga datos antes de las pruebas. |
| `@Autowired` | Inyección en la prueba. |
| `@Test` | Caso de prueba. |

## 4.2 Java: palabras clave y conceptos

| Palabra / concepto | Significado | Ejemplo |
|---|---|---|
| `package` / `import` | Paquete de la clase / usar otra clase | `import java.util.List;` |
| `public` / `private` | Visible desde todo / solo en la clase | `private final NotaRepository notaRepository;` |
| `class` / `interface` | Molde de objetos / contrato | `NotaServicesImpl implements NotaServices` |
| `implements` / `extends` | Cumplir una interfaz / heredar | `BusinessException extends RuntimeException` |
| `enum` | Valores fijos | `Rol` |
| `record` | Clase inmutable de datos | DTOs |
| `final` | No reasignable (campo) / no heredable (clase) | `private final ...`, `final class CuerpoError` |
| `static` | De la clase, no del objeto | `NotaResponse.from(n)` |
| `static final` | Constante | `NOTA_MINIMA_APROBACION = 3.0f` |
| `void` / `return` | Sin valor de retorno / devolver | — |
| `new` / `this` | Crear objeto / objeto actual | `this.repo = repo;` |
| `throw` / `throws` | Lanzar / declarar excepción | `throw new BusinessException(...)` |
| `try` / `catch` | Atrapar excepciones | `AdminInicialRunner` |
| `instanceof Tipo var` | Comprobar tipo y crear variable | `auth.getPrincipal() instanceof Jwt jwt` |
| `switch ... ->` | Selección moderna | `CuerpoError.descripcion` |
| `null` | Sin valor | `definitiva == null` → en curso |
| `Optional<T>` | Valor que puede no estar | `findById(id).orElseThrow(...)` |
| `List`, `Set`, `Map`, `LinkedHashMap` | Colecciones (`LinkedHashMap` conserva el orden) | Reportes y errores |
| Genéricos `<T>` | Código para cualquier tipo | `soloMateriasAsignadas` |
| Lambda `x -> ...` | Función anónima | `n -> n.getMateria().getIdMateria()` |
| Referencia a método `Clase::metodo` | Atajo de lambda | `NotaResponse::from` |
| Streams | `stream().filter().map().toList()` | Listados |
| Wrappers (`Long`, `Float`, `Boolean`) | Pueden ser `null` | `Float definitiva` |
| Inyección por constructor | Dependencias recibidas en el constructor | Todos los servicios |
| Encapsulamiento, herencia, polimorfismo, abstracción | Pilares de la POO | Campos privados, excepciones, interfaces |

---

# Parte V. Banco de preguntas

## 5.1 Seguridad

**¿Cuál es la diferencia entre autenticación y autorización?**
Autenticación es comprobar quién eres (login, token). Autorización es decidir qué puedes hacer (roles y reglas de `AccesoService`).

**¿Cómo funciona el login de principio a fin?**
`AuthController` recibe usuario y clave; `AuthServicesImpl` busca el usuario, verifica la clave con `BCrypt.matches` y que la cuenta esté activa; `JwtTokenServices` crea un JWT firmado con HS256 con el rol y el estudiante o profesor vinculado; el front lo envía en cada petición como `Authorization: Bearer`.

**¿Qué es un JWT y qué partes tiene?**
Un token de tres partes separadas por punto: header (algoritmo), payload (claims: `sub`, `rol`, `idProfesor`, `exp`…) y firma (HMAC-SHA256 con el secreto del servidor).

**¿El JWT está cifrado?**
No, está codificado en Base64URL: cualquiera puede leerlo. Lo que garantiza la firma es que **no se pueda modificar**. Por eso no lleva datos sensibles.

**¿Qué pasa si alguien cambia el rol dentro del token?**
La firma deja de coincidir y el servidor responde 401 `TOKEN_INVALIDO`. Lo probamos en vivo.

**¿Qué es HS256 y por qué se eligió?**
HMAC con SHA-256, un algoritmo **simétrico**: la misma clave firma y verifica. Sirve porque el mismo servidor emite y valida los tokens. Si hubiera varios servicios validando, convendría RS256 (asimétrico).

**¿Por qué el secreto debe tener al menos 32 caracteres?**
HS256 necesita una llave de al menos 256 bits (32 bytes); una más corta se podría adivinar por fuerza bruta. Si es más corta, la app ni siquiera arranca.

**¿Dónde está el secreto?**
En `application-local.properties` (fuera de git) o en la variable de entorno `JWT_SECRET`.

**¿Cuánto dura el token y qué pasa al vencer?**
480 minutos. Al vencer, el `JwtDecoder` lo rechaza y la API responde 401 `TOKEN_INVALIDO`; el usuario debe iniciar sesión de nuevo.

**¿Cómo se guardan las contraseñas?**
Con BCrypt: hash lento con sal aleatoria y costo 10. Nunca en texto plano y nunca se devuelven.

**¿Por qué BCrypt y no SHA-256?**
SHA-256 es rápido y eso facilita los ataques de fuerza bruta. BCrypt es lento a propósito, incluye sal y su costo se puede aumentar.

**¿Por qué el mismo mensaje si el usuario no existe o la clave es incorrecta?**
Para evitar la enumeración de usuarios: un atacante no puede saber qué cuentas existen.

**¿Por qué la cuenta desactivada se informa solo si la clave es correcta?**
Para no revelar que la cuenta existe a quien no conoce la clave, y a la vez darle un mensaje claro al usuario legítimo.

**¿Cómo se convierte el rol del token en un permiso de Spring?**
`JwtGrantedAuthoritiesConverter` lee el claim `rol` y le pone el prefijo `ROLE_` → `ROLE_PROFESOR`. `hasRole('PROFESOR')` busca exactamente esa autoridad.

**¿Cuáles son los dos niveles de autorización?**
Por URL en `SecurityConfig` (rápido, grueso) y por método con `@PreAuthorize` + `AccesoService` (fino, depende de los datos).

**Explica `@PreAuthorize("@acceso.puedeCalificarMateria(authentication, #request.idMateria())")`.**
Antes de ejecutar el método, Spring llama al bean `acceso` (`AccesoService`) con el usuario actual y la materia que viene en el cuerpo. Si devuelve `false`, lanza `AccessDeniedException` → 403.

**¿Cómo sabe el sistema que un profesor dicta una materia?**
El token trae `idProfesor`; `AccesoService` consulta `asignaciones_materia_profesor` con `existsByProfesorIdProfesorAndMateriaIdMateria`.

**¿Qué es IDOR y cómo se previene?**
*Insecure Direct Object Reference*: cambiar el id en la URL para ver datos ajenos. Se previene verificando en cada endpoint que el recurso pertenezca al usuario (`puedeVerEstudiante`, `puedeVerNota`).

**¿Por qué `AccesoService` devuelve `true` si el registro no existe?**
Para que el servicio responda 404 y no un 403 engañoso sobre algo que no existe.

**¿Por qué el profesor recibe solo sus notas en `GET /api/notas`?**
El controlador pasa la lista por `acceso.soloMateriasAsignadas`, que deja solo las materias asignadas al profesor (los gestores reciben todo).

**¿Qué pasa si el front oculta un botón pero alguien usa Postman?**
El servidor aplica la misma regla: 403. La seguridad no depende del front.

**¿Qué es la cadena de filtros de Spring Security?**
Una serie de filtros que se ejecutan antes del controlador: CORS, validación del token, usuario anónimo, traducción de excepciones y autorización por URL.

**¿Por qué hay dos manejadores de errores de seguridad?**
Los errores en los filtros ocurren antes del `DispatcherServlet` y no llegan al `@RestControllerAdvice`; por eso `ErroresSeguridadHandler` los atiende. Los de `@PreAuthorize` sí llegan a `ApiExceptionHandler`. Ambos usan `CuerpoError`.

**¿Cuál es la diferencia entre 401 y 403?**
401: no estás autenticado (sin token, token inválido, credenciales malas). 403: estás autenticado pero no tienes permiso.

**¿Qué es CORS y por qué se configuró?**
Regla del navegador que bloquea llamadas entre orígenes distintos. El front (5173) y la API (8080) son orígenes distintos; se permitieron solo los orígenes del front y las cabeceras `Authorization` y `Content-Type`.

**¿Por qué `.cors()` dentro de Spring Security?**
Para que el preflight `OPTIONS`, que no trae token, se responda antes de la autenticación. Antes respondía 401 y el front no funcionaba.

**¿CORS protege la API de ataques?**
No de herramientas como curl o Postman; solo controla qué páginas web pueden llamarla desde un navegador. La protección real es el JWT.

**¿Por qué CSRF está desactivado? ¿No es inseguro?**
CSRF aprovecha las cookies que el navegador envía solo. Aquí no hay cookies: el token va en una cabecera que el navegador no agrega por su cuenta. Por eso no aplica.

**¿Qué significa STATELESS?**
El servidor no guarda sesión; cada petición se autentica con su token. Escala mejor y no hay fijación de sesión.

**¿Se puede hacer inyección SQL?**
No: JPA usa consultas parametrizadas y no hay SQL concatenado. Se probó con `admin' OR '1'='1` y respondió 401.

**¿Qué pasa con un token con `alg: none`?**
Se rechaza (401) porque el decoder solo acepta HS256.

**¿Cómo se evita que un admin se borre a sí mismo?**
El controlador saca el `idUsuario` del token (no del cuerpo) y el servicio lo compara con el id a borrar → 409 `CUENTA_PROPIA`.

**¿Por qué la regla del último administrativo?**
Para que nunca quede el sistema sin alguien que administre cuentas, incluso si un admin desactivado todavía tiene un token válido.

**¿Cómo se crea el primer administrador?**
`AdminInicialRunner` (un `ApplicationRunner`) se ejecuta al arrancar; si no hay ADMINISTRATIVO, crea `admin` con la clave de configuración hasheada.

**¿Qué es la asignación masiva y cómo se evita?**
Que el cliente envíe campos que no debería (como `passwordHash` o `aprobada`). Se evita porque los DTO de entrada solo tienen los campos permitidos.

**¿Qué información sensible podría filtrarse y cómo se evita?**
Hashes de clave (no se devuelven), *stack traces* (el 500 da un mensaje genérico), existencia de usuarios (mensaje único de login) y estructura de permisos (403 genérico).

**¿Qué haría si se filtra el secreto JWT?**
Cambiarlo de inmediato: todos los tokens emitidos dejan de ser válidos y los usuarios deben iniciar sesión otra vez.

**¿Qué mejorarías en la seguridad?**
Límite de intentos de login (contra fuerza bruta), tokens cortos con *refresh token* o lista de revocación, auditoría de cambios, HTTPS en producción y RS256 si hubiera varios servicios. Ver Parte VI.

**¿Dónde guarda el front el token y qué riesgo hay?**
Lo maneja el front (normalmente en memoria o `localStorage`). Si el front tuviera una vulnerabilidad XSS, un script podría leerlo; por eso el token vence y el front no debe inyectar HTML sin escapar.

**¿Por qué HTTPS es importante aunque no esté configurado aquí?**
En local se usa HTTP. En producción el token viajaría en texto plano sin HTTPS y podría interceptarse; se configuraría en el servidor o en un proxy (TLS).

## 5.2 Implementación y arquitectura

**¿Qué arquitectura usa el proyecto?**
Por capas con puertos y adaptadores: dominio, aplicación, infraestructura y presentación.

**¿Por qué tres piezas por repositorio?**
El puerto (interfaz del dominio) desacopla los servicios de JPA; Spring Data genera el SQL; el adaptador une ambos.

**¿Por qué DTOs?**
Seguridad (no exponer campos internos), respuestas planas con nombres, contrato estable con el front, evitar ciclos y proxies de Hibernate, y evitar la asignación masiva.

**¿Qué hace `Referencias`?**
Convierte los ids del JSON en entidades reales, con 404 si no existen.

**¿Para qué sirve `AlCrear`?**
Grupo de validación: en el POST exige campos (id, clave) que en el PUT no.

**¿Cómo se calcula la definitiva?**
Promedio de las calificaciones, redondeado a 2 decimales; aprueba con ≥ 3.0; se recalcula cada vez que cambia una calificación.

**¿Cómo se controla el cupo?**
Se cuentan los inscritos del grupo en el año; si es ≥ `cupoMaximo` → 409 `CUPO_LLENO`.

**¿Por qué 409 para las reglas de negocio?**
La petición está bien formada, pero choca con el estado actual de los datos: es un conflicto.

**¿Por qué `BusinessException` tiene un código?**
Para que el front reaccione a un valor estable (`CUPO_LLENO`) sin depender del texto del mensaje.

**¿Qué es una transacción y dónde importa?**
Operaciones que se guardan todas o ninguna. Importa al crear una calificación: se inserta el movimiento y se actualiza la definitiva juntos.

**¿Qué principios SOLID se aplican?**
Responsabilidad única (cada capa), abierto/cerrado (nuevas reglas en `AccesoService`), sustitución (cualquier implementación del puerto), segregación de interfaces (repositorios específicos) e inversión de dependencias (todo depende de interfaces).

## 5.3 Java y Spring

**¿Qué es la inyección de dependencias?**
Spring crea los objetos y los entrega por el constructor; ninguna clase hace `new` de sus dependencias.

**¿Qué es un bean?**
Objeto creado y administrado por Spring; por defecto hay una sola instancia (*singleton*).

**¿Qué es un `record`?**
Clase inmutable de solo datos; Java genera constructor, accesores, `equals`, `hashCode` y `toString`.

**¿Qué hace `Optional.orElseThrow`?**
Devuelve el valor o lanza la excepción indicada si está vacío.

**¿Por qué `Float definitiva` y no `float`?**
Porque puede ser `null` (materia en curso).

**¿Qué es un proxy y qué tiene que ver con `@PreAuthorize` y `@Transactional`?**
Spring envuelve el bean en un objeto intermediario que ejecuta lógica antes y después del método: evaluar el permiso o abrir y cerrar la transacción.

**¿Qué es SpEL?**
*Spring Expression Language*: el lenguaje de las expresiones de `@PreAuthorize` (`@bean.metodo(authentication, #param)`).

**¿Por qué las excepciones extienden `RuntimeException`?**
No obligan a `try/catch` y provocan *rollback* automático en `@Transactional`.

## 5.4 Base de datos

**¿Qué es JPA y Hibernate?**
JPA es la especificación; Hibernate, la implementación que genera el SQL.

**¿Qué es LAZY?**
La relación no se carga hasta que se usa; evita consultas innecesarias.

**¿Qué es el problema N+1?**
1 consulta para la lista + N consultas para la relación de cada elemento. Se resuelve con `JOIN FETCH` o `@EntityGraph`.

**¿Por qué `ddl-auto=none`?**
Para que Hibernate no modifique la BD real; el esquema está en `db/schema.sql`.

**¿Por qué validar en el servicio si hay índices únicos?**
El servicio da un mensaje claro; el índice es la última barrera ante peticiones simultáneas.

**¿Por qué algunos ids son texto y otros autoincrementales?**
Los de texto son códigos que la institución ya usa (documento, código de materia); los numéricos son registros sin código natural.

---

# Parte VI. Limitaciones y mejoras

## 6. Limitaciones conocidas

Reconocerlas con una solución en mente demuestra dominio del tema.

| Limitación | Detalle | Mejora |
|---|---|---|
| **Sin límite de intentos de login** | Se pueden probar claves sin límite (BCrypt lo hace lento, pero no lo impide). | *Rate limiting* por IP/usuario o bloqueo temporal tras N fallos. |
| **Diferencia de tiempo en el login** | Si el usuario no existe se responde sin ejecutar BCrypt, un poco más rápido; midiendo tiempos se podría inferir qué usuarios existen. | Ejecutar siempre un `matches` contra un hash ficticio. |
| **El token no se puede revocar** | Una cuenta desactivada conserva su token hasta que vence (8 h). | Tokens cortos + *refresh token*, o lista de revocación / versión de token en la BD. |
| **El `issuer` no se valida** | El token trae `iss`, pero el decoder no lo exige. | Agregar un validador de *issuer* al `JwtDecoder`. |
| **Detalle de registros para el profesor** | Los listados están recortados, pero un profesor todavía puede abrir el detalle de una nota o inscripción de otra materia (solo lectura). | Aplicar en esos endpoints los mismos métodos de `AccesoService`. |
| **Sin HTTPS** | En local se usa HTTP. | TLS en producción (servidor o proxy). |
| **Sin auditoría** | No se registra quién cambió una nota. | Tabla de auditoría o Hibernate Envers. |
| **Listados lentos con muchos datos** | `GET /api/movimiento-notas` tarda unos 9 s con 30 000 filas (N+1 y filtro en memoria). | Paginación, `JOIN FETCH` y filtrar por profesor en la consulta SQL. |
| **`open-in-view = true`** | La conexión queda abierta mientras se arma el JSON. | Construir los DTO dentro del servicio. |
| **HS256 compartido** | Si hubiera varios servicios, todos necesitarían el secreto. | RS256 con llave privada/pública. |

---

### Consejos finales

- Responde siempre con esta estructura: **qué es → dónde está en el código → por qué se hizo así**.
- Si te preguntan algo que no recuerdas, ubícalo: *"eso está en `AccesoService`, la clase de reglas de acceso; se lo muestro"*, y ábrelo.
- Para la seguridad, la frase que resume todo: *"Autenticación con JWT firmado, autorización en dos niveles (URL y método) con reglas que dependen de los datos, contraseñas con BCrypt, y la seguridad siempre en el servidor."*
