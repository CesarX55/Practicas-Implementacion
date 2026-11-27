package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Pruebas de integracion de la API. Requieren PostgreSQL disponible con la
 * configuracion de application.properties (base de datos "chochos").
 */
@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTests {

    private static final String PRODUCTO = "{\"nombre\":\"Proteina\",\"descripcion\":\"2.2 kg\","
            + "\"lote\":\"123\",\"precioUnitario\":1500.5,\"fechaCaducidad\":\"2027-05-04\"}";
    private static final String COMENTARIO = "{\"username\":\"usuario1\",\"comentario_corto\":\"Bueno\","
            + "\"estrellas\":4,\"comentario_largo\":\"Llego a tiempo\"}";
    private static final String IMAGEN = "{\"url\":\"https://example.com/a.png\",\"prioridad\":1,"
            + "\"tamanio\":\"grande\",\"responsiva\":true}";
    private static final String CATEGORIA = "{\"nombre\":\"Suplementos\",\"descripcion\":\"Vitaminas\","
            + "\"tipo\":\"Principal\",\"icono\":\"i\",\"imagenNavbar\":\"x\"}";

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    private static RequestPostProcessor admin() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    private static RequestPostProcessor usuario() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"));
    }

    private String crearProducto() throws Exception {
        String body = mvc.perform(post("/producto").with(admin())
                        .contentType(MediaType.APPLICATION_JSON).content(PRODUCTO))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getContentAsString();
        return mapper.readTree(body).get("id").asText();
    }

    @Test
    void todosLosGetSonPublicosSinToken() throws Exception {
        for (String ruta : new String[] {"/productos", "/comentarios", "/categorias", "/imagenes"}) {
            mvc.perform(get(ruta)).andExpect(status().isOk());
        }
    }

    @Test
    void getDeIdInexistenteDevuelve404NoDevuelve401() throws Exception {
        mvc.perform(get("/producto/no-existe")).andExpect(status().isNotFound());
        mvc.perform(get("/categoria/no-existe")).andExpect(status().isNotFound());
        mvc.perform(get("/comentario/no-existe")).andExpect(status().isNotFound());
        mvc.perform(get("/imagen/no-existe")).andExpect(status().isNotFound());
    }

    @Test
    void escrituraProtegidaSinTokenDevuelve401() throws Exception {
        mvc.perform(post("/producto").contentType(MediaType.APPLICATION_JSON).content(PRODUCTO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenInvalidoDevuelve401() throws Exception {
        mvc.perform(post("/producto").header("Authorization", "Bearer token-falso")
                        .contentType(MediaType.APPLICATION_JSON).content(PRODUCTO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usuarioNoPuedeEscribirProductosNiCategorias() throws Exception {
        mvc.perform(post("/producto").with(usuario())
                        .contentType(MediaType.APPLICATION_JSON).content(PRODUCTO))
                .andExpect(status().isForbidden());
        mvc.perform(post("/categoria").with(usuario())
                        .contentType(MediaType.APPLICATION_JSON).content(CATEGORIA))
                .andExpect(status().isForbidden());
    }

    @Test
    void usuarioYAdminPuedenEscribirComentariosEImagenes() throws Exception {
        String productoId = crearProducto();
        for (RequestPostProcessor quien : new RequestPostProcessor[] {usuario(), admin()}) {
            mvc.perform(post("/comentario/" + productoId).with(quien)
                            .contentType(MediaType.APPLICATION_JSON).content(COMENTARIO))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"));
            mvc.perform(post("/imagen/" + productoId).with(quien)
                            .contentType(MediaType.APPLICATION_JSON).content(IMAGEN))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"));
        }
    }

    @Test
    void adminHaceElCrudCompletoDeProductos() throws Exception {
        String id = crearProducto();

        mvc.perform(get("/producto/" + id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Proteina"));

        mvc.perform(put("/producto/" + id).with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PRODUCTO.replace("Proteina", "Proteina de fresa")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Proteina de fresa"));

        mvc.perform(put("/producto/no-existe").with(admin())
                        .contentType(MediaType.APPLICATION_JSON).content(PRODUCTO))
                .andExpect(status().isNotFound());

        mvc.perform(delete("/producto/" + id).with(admin())).andExpect(status().isNoContent());
        mvc.perform(delete("/producto/" + id).with(admin())).andExpect(status().isNotFound());
    }

    @Test
    void dtoInvalidoDevuelve400() throws Exception {
        mvc.perform(post("/producto").with(admin())
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchModificaSoloLosCamposEnviados() throws Exception {
        String id = crearProducto();

        mvc.perform(patch("/producto/" + id).with(admin())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"Nuevo nombre\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Nuevo nombre"))
                .andExpect(jsonPath("$.lote").value("123"))
                .andExpect(jsonPath("$.descripcion").value("2.2 kg"));

        mvc.perform(patch("/producto/" + id).with(admin())
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registroSiempreCreaRolUserYRechazaDuplicados() throws Exception {
        String username = "usr-" + UUID.randomUUID().toString().substring(0, 8);
        String body = "{\"username\":\"" + username + "\",\"password\":\"clave-segura-123\",\"role\":\"ADMIN\"}";

        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.password").doesNotExist());

        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void loginEmiteJwtValidoYCredencialesInvalidasDan401() throws Exception {
        String username = "usr-" + UUID.randomUUID().toString().substring(0, 8);
        String credenciales = "{\"username\":\"" + username + "\",\"password\":\"clave-segura-123\"}";
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(credenciales))
                .andExpect(status().isCreated());

        String respuesta = mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON).content(credenciales))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn().getResponse().getContentAsString();
        String token = mapper.readTree(respuesta).get("accessToken").asText();
        assertThat(token).isNotBlank();

        // El token emitido por el login permite escribir un comentario como USER
        String productoId = crearProducto();
        mvc.perform(post("/comentario/" + productoId).header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(COMENTARIO))
                .andExpect(status().isCreated());

        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"otra-clave-123\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void openApiPublicaElEsquemaBearerJwt() throws Exception {
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.bearerFormat").value("JWT"));
    }
}
