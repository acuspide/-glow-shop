package com.example.ecommerce.integracion;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de la seguridad REAL sobre una ruta protegida: GET /api/admin/usuarios (solo ADMINISTRADOR).
 *
 * No usamos Spring Security, así que no hay jwt() de spring-security-test: levantamos la aplicación
 * completa (@SpringBootTest) y pedimos un token de verdad con POST /api/auth/login, usando los
 * usuarios de data.sql. Así se prueba todo el camino: login, firma del token, lectura del token
 * en el controller y la regla de permisos del dominio.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SeguridadEndpointTest {

    private static final String RUTA_SOLO_ADMIN = "/api/admin/usuarios";

    @Autowired
    private MockMvc mockMvc;

    private String tokenDe(String email, String contrasena) throws Exception {
        String respuesta = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + email + "\", \"contrasena\": \"" + contrasena + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return respuesta.replaceAll(".*\"token\"\\s*:\\s*\"([^\"]+)\".*", "$1");
    }

    @Test
    void sinTokenDeberiaResponder401() throws Exception {
        // Act y Assert
        mockMvc.perform(get(RUTA_SOLO_ADMIN))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void conTokenDeClienteDeberiaResponder403() throws Exception {
        // Arrange: Ana es CLIENTE (data.sql)
        String tokenCliente = tokenDe("ana@correo.com", "Clave12345");

        // Act y Assert
        mockMvc.perform(get(RUTA_SOLO_ADMIN).header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    void conTokenDeAdministradorDeberiaResponder200() throws Exception {
        // Arrange: el administrador inicial se crea al arrancar
        String tokenAdmin = tokenDe("admin@glowshop.com", "Admin12345");

        // Act y Assert
        mockMvc.perform(get(RUTA_SOLO_ADMIN).header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void unTokenAlteradoDeberiaResponder401() throws Exception {
        // Arrange: se cambia un carácter de la firma
        String token = tokenDe("admin@glowshop.com", "Admin12345");
        String alterado = token.substring(0, token.length() - 2) + (token.endsWith("A") ? "BB" : "AA");

        // Act y Assert
        mockMvc.perform(get(RUTA_SOLO_ADMIN).header("Authorization", "Bearer " + alterado))
                .andExpect(status().isUnauthorized());
    }
}
