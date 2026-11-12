package com.example.gestionacademica;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Inicio de sesión, permisos por rol y formato de las respuestas DTO contra una BD H2 en memoria
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/datos-prueba.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class SeguridadYDtosIntegrationTest {

    private static final String DEMO = "Demo1234*";

    @Autowired
    private MockMvc mockMvc;

    // ---------- Inicio de sesión ----------

    @Test
    void loginCorrectoDevuelveTokenYDatosDelUsuario() throws Exception {
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"est001\",\"password\":\"" + DEMO + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.usuario.rol").value("ESTUDIANTE"))
                .andExpect(jsonPath("$.usuario.idEstudiante").value("EST001"))
                .andExpect(jsonPath("$.usuario.passwordHash").doesNotExist());
    }

    @Test
    void loginConClaveErradaDa401() throws Exception {
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"est001\",\"password\":\"otra\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void sinTokenDa401() throws Exception {
        mockMvc.perform(get("/api/carreras")).andExpect(status().isUnauthorized());
    }

    @Test
    void adminInicialSeCreaAlArrancar() throws Exception {
        mockMvc.perform(conToken(get("/api/auth/me"), login("admin", "Admin123*")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("ADMINISTRATIVO"));
    }

    // ---------- ESTUDIANTE ----------

    @Test
    void estudianteSoloVeSuInformacion() throws Exception {
        String token = login("est001", DEMO);
        mockMvc.perform(conToken(get("/api/notas/estudiante/EST001"), token)).andExpect(status().isOk());
        mockMvc.perform(conToken(get("/api/reportes/boletin/EST001"), token)).andExpect(status().isOk());
        mockMvc.perform(conToken(get("/api/notas/1"), token)).andExpect(status().isOk());

        mockMvc.perform(conToken(get("/api/notas/estudiante/EST002"), token)).andExpect(status().isForbidden());
        mockMvc.perform(conToken(get("/api/notas/3"), token)).andExpect(status().isForbidden());
        mockMvc.perform(conToken(get("/api/estudiantes"), token)).andExpect(status().isForbidden());
        mockMvc.perform(conToken(get("/api/carreras"), token)).andExpect(status().isOk());
        mockMvc.perform(conToken(post("/api/carreras").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idCarrera\":\"X\",\"nombreCarrera\":\"X\"}"), token))
                .andExpect(status().isForbidden());
    }

    // ---------- PROFESOR ----------

    @Test
    void profesorCalificaSoloSusMaterias() throws Exception {
        String token = login("profesor1", DEMO); // dicta MAT001 en GRP001

        // Nota 1 es de MAT001: puede calificar y la definitiva se recalcula
        mockMvc.perform(conToken(post("/api/movimiento-notas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fechaRegistro\":\"2026-03-10\",\"calificacion\":4.0,\"idNota\":1}"), token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idNota").value(1))
                .andExpect(jsonPath("$.idMateria").value("MAT001"));
        mockMvc.perform(conToken(get("/api/notas/1"), token))
                .andExpect(jsonPath("$.definitiva").value(4.0))
                .andExpect(jsonPath("$.aprobada").value(true));

        // Nota 2 es de MAT002, que dicta otro profesor
        mockMvc.perform(conToken(post("/api/movimiento-notas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fechaRegistro\":\"2026-03-10\",\"calificacion\":4.0,\"idNota\":2}"), token))
                .andExpect(status().isForbidden());

        mockMvc.perform(conToken(get("/api/reportes/grupo/GRP001/planilla-notas?idMateria=MAT001&anio=2026"), token))
                .andExpect(status().isOk());
        mockMvc.perform(conToken(get("/api/reportes/grupo/GRP001/planilla-notas?idMateria=MAT002&anio=2026"), token))
                .andExpect(status().isForbidden());
        mockMvc.perform(conToken(get("/api/usuarios"), token)).andExpect(status().isForbidden());
    }

    // ---------- COORDINADOR / ADMINISTRATIVO ----------

    @Test
    void coordinadorGestionaLoAcademicoPeroNoLosUsuarios() throws Exception {
        String token = login("coordinador", DEMO);
        mockMvc.perform(conToken(post("/api/materias").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idMateria\":\"MAT900\",\"nombreMateria\":\"Física I\"}"), token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreMateria").value("Física I"));
        mockMvc.perform(conToken(get("/api/usuarios"), token)).andExpect(status().isForbidden());
    }

    @Test
    void administrativoCreaUsuariosVinculados() throws Exception {
        String token = login("admin", "Admin123*");
        mockMvc.perform(conToken(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"profesor2\",\"password\":\"Clave1234\",\"rol\":\"PROFESOR\","
                                + "\"activo\":true,\"idProfesor\":\"PROF002\"}"), token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idProfesor").value("PROF002"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());

        // Un PROFESOR sin profesor vinculado es inválido
        mockMvc.perform(conToken(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"malo\",\"password\":\"Clave1234\",\"rol\":\"PROFESOR\",\"activo\":true}"), token))
                .andExpect(status().isConflict());

        login("profesor2", "Clave1234");
    }

    // ---------- DTOs ----------

    @Test
    void respuestasSonDtosPlanosSinEntidadesAnidadas() throws Exception {
        String token = login("coordinador", DEMO);
        mockMvc.perform(conToken(get("/api/notas/3"), token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreEstudiante").value("María López"))
                .andExpect(jsonPath("$.nombreMateria").value("Cálculo I"))
                .andExpect(jsonPath("$.definitiva").value(nullValue()))
                .andExpect(jsonPath("$.estudiante").doesNotExist())
                .andExpect(jsonPath("$.hibernateLazyInitializer").doesNotExist());

        mockMvc.perform(conToken(get("/api/grupos/GRP001"), token))
                .andExpect(jsonPath("$.nombreNivel").value("Primer semestre"));
    }

    @Test
    void referenciaInexistenteDa404() throws Exception {
        String token = login("coordinador", DEMO);
        mockMvc.perform(conToken(post("/api/grupos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idGrupo\":\"G9\",\"nombreGrupo\":\"G9\",\"idNivel\":\"NO-EXISTE\"}"), token))
                .andExpect(status().isNotFound());
    }

    // ---------- Listados recortados por rol ----------

    @Test
    void profesorSoloRecibeSusMateriasYGrupos() throws Exception {
        String token = login("profesor1", DEMO); // MAT001 en GRP001; no dicta en GRP002
        mockMvc.perform(conToken(get("/api/notas"), token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].idMateria", everyItem(is("MAT001"))));
        mockMvc.perform(conToken(get("/api/movimiento-notas"), token))
                .andExpect(jsonPath("$[*].idMateria", everyItem(is("MAT001"))));
        mockMvc.perform(conToken(get("/api/asistencia"), token))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].idGrupo").value("GRP001"));
        mockMvc.perform(conToken(get("/api/inscripciones"), token))
                .andExpect(jsonPath("$[*].idGrupo", everyItem(is("GRP001"))));
        mockMvc.perform(conToken(get("/api/estudiantes"), token))
                .andExpect(jsonPath("$[*].idEstudiante", containsInAnyOrder("EST001", "EST002")));
    }

    @Test
    void gestorRecibeListadosCompletos() throws Exception {
        String token = login("coordinador", DEMO);
        mockMvc.perform(conToken(get("/api/notas"), token)).andExpect(jsonPath("$.length()").value(3));
        mockMvc.perform(conToken(get("/api/asistencia"), token)).andExpect(jsonPath("$.length()").value(2));
        mockMvc.perform(conToken(get("/api/estudiantes"), token))
                .andExpect(jsonPath("$[*].idEstudiante", hasItem("EST003")));
    }

    // ---------- Formato de errores ----------

    @Test
    void erroresLlevanCodigoYDescripcionEnEspanol() throws Exception {
        mockMvc.perform(get("/api/carreras"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("No autenticado"))
                .andExpect(jsonPath("$.codigo").value("NO_AUTENTICADO"));
        mockMvc.perform(get("/api/carreras").header("Authorization", "Bearer basura"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("TOKEN_INVALIDO"));
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"est001\",\"password\":\"otra\"}"))
                .andExpect(jsonPath("$.codigo").value("CREDENCIALES_INVALIDAS"));
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("VALIDACION"))
                .andExpect(jsonPath("$.errores.username").exists());
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.TEXT_PLAIN).content("x"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.error").value("Tipo de contenido no soportado"));

        String estudiante = login("est001", DEMO);
        mockMvc.perform(conToken(get("/api/notas"), estudiante))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Acceso denegado"))
                .andExpect(jsonPath("$.codigo").value("SIN_PERMISO"));

        String token = login("coordinador", DEMO);
        mockMvc.perform(conToken(get("/api/no-existe"), token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No encontrado"))
                .andExpect(jsonPath("$.codigo").value("RUTA_NO_ENCONTRADA"));
        mockMvc.perform(conToken(get("/api/estudiantes/NO-EXISTE"), token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("ESTUDIANTE_NO_ENCONTRADO"));
        mockMvc.perform(conToken(get("/api/notas/abc"), token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("PARAMETRO_INVALIDO"));
        mockMvc.perform(conToken(delete("/api/notas"), token))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.error").value("Método no permitido"));
    }

    @Test
    void preflightCorsNoExigeToken() throws Exception {
        mockMvc.perform(options("/api/notas")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "authorization,content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
        mockMvc.perform(options("/api/notas")
                        .header("Origin", "http://evil.example")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden());
    }

    // ---------- Reglas de usuarios ----------

    @Test
    void administrativoNoSeBorraNiDejaElSistemaSinAdministrativos() throws Exception {
        String admin = login("admin", "Admin123*");
        String idAdmin = JsonPath.read(mockMvc.perform(conToken(get("/api/auth/me"), admin))
                .andReturn().getResponse().getContentAsString(), "$.idUsuario").toString();

        mockMvc.perform(conToken(delete("/api/usuarios/" + idAdmin), admin))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("CUENTA_PROPIA"));
        mockMvc.perform(conToken(put("/api/usuarios/" + idAdmin).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"rol\":\"ADMINISTRATIVO\",\"activo\":false}"), admin))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("CUENTA_PROPIA"));

        // Vínculos: estudiante inexistente (404) y estudiante que ya tiene cuenta (409)
        mockMvc.perform(conToken(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"fantasma\",\"password\":\"Clave1234\",\"rol\":\"ESTUDIANTE\","
                                + "\"activo\":true,\"idEstudiante\":\"NO-EXISTE\"}"), admin))
                .andExpect(status().isNotFound());
        mockMvc.perform(conToken(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"otroest001\",\"password\":\"Clave1234\",\"rol\":\"ESTUDIANTE\","
                                + "\"activo\":true,\"idEstudiante\":\"EST001\"}"), admin))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("VINCULO_DUPLICADO"));

        // admin2 inicia sesión y luego es desactivado: su token sigue vigente, pero ya no puede
        // desactivar al único ADMINISTRATIVO activo que queda, y no puede volver a entrar
        String body = mockMvc.perform(conToken(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin2\",\"password\":\"Clave1234\",\"rol\":\"ADMINISTRATIVO\",\"activo\":true}"), admin))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String idAdmin2 = JsonPath.read(body, "$.idUsuario").toString();
        String admin2 = login("admin2", "Clave1234");
        mockMvc.perform(conToken(put("/api/usuarios/" + idAdmin2).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin2\",\"rol\":\"ADMINISTRATIVO\",\"activo\":false}"), admin))
                .andExpect(status().isOk());
        mockMvc.perform(conToken(put("/api/usuarios/" + idAdmin).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"rol\":\"ADMINISTRATIVO\",\"activo\":false}"), admin2))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("ULTIMO_ADMINISTRATIVO"));

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin2\",\"password\":\"Clave1234\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value("CUENTA_DESACTIVADA"))
                .andExpect(jsonPath("$.message").value("Tu cuenta está desactivada"));
    }

    private String login(String username, String password) throws Exception {
        String body = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.token");
    }

    private MockHttpServletRequestBuilder conToken(MockHttpServletRequestBuilder request, String token) {
        return request.header("Authorization", "Bearer " + token);
    }
}
