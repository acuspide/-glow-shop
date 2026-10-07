package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.application.dto.response.UsuarioResponse;
import com.example.ecommerce.application.usecase.RegistrarClienteUseCase;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.ContrasenaDebilException;
import com.example.ecommerce.domain.exception.CorreoElectronicoDuplicadoException;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.domain.valueobject.RolUsuario;
import com.example.ecommerce.infrastructure.persistence.SecuenciaIdUsuario;
import com.example.ecommerce.infrastructure.rest.mapper.UsuarioMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrarClienteUseCase registrarClienteUseCase;

    @MockitoBean
    private UsuarioMapper mapper;

    @MockitoBean
    private SecuenciaIdUsuario secuenciaId;

    @Test
    void deberiaRegistrarUsuarioYNoExponerLaContrasena() throws Exception {

        // Arrange
        String requestJson = """
            {
                "nombre": "Ana",
                "email": "ana@correo.com",
                "contrasena": "Clave123",
                "telefono": "3001234567",
                "fechaNacimiento": "2000-01-01"
            }
            """;

        Usuario usuarioSimulado = Usuario.registrarCliente(
                1L, "Ana", new Email("ana@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);
        when(registrarClienteUseCase.ejecutar(anyLong(), any(), any(), any(), any(), any()))
                .thenReturn(usuarioSimulado);
        when(mapper.toResponse(any()))
                .thenReturn(new UsuarioResponse(1L, "Ana", "ana@correo.com", "3001234567", LocalDate.of(2000, 1, 1), RolUsuario.CLIENTE, true));

        // Act y Assert
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.email").value("ana@correo.com"))
                .andExpect(jsonPath("$.rol").value("CLIENTE"))
                .andExpect(jsonPath("$.contrasena").doesNotExist())
                .andExpect(jsonPath("$.contrasenaHash").doesNotExist());
    }

    @Test
    void deberiaRetornar400CuandoElCorreoTieneFormatoInvalido() throws Exception {

        // Arrange
        String requestJson = """
            {
                "nombre": "Ana",
                "email": "correo-invalido",
                "contrasena": "Clave123",
                "telefono": "3001234567",
                "fechaNacimiento": "2000-01-01"
            }
            """;

        // Act y Assert
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400CuandoFaltaElNombre() throws Exception {

        // Arrange
        String requestJson = """
            {
                "nombre": " ",
                "email": "ana@correo.com",
                "contrasena": "Clave123",
                "telefono": "3001234567",
                "fechaNacimiento": "2000-01-01"
            }
            """;

        // Act y Assert
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar409CuandoElCorreoYaEstaRegistrado() throws Exception {

        // Arrange
        String requestJson = """
            {
                "nombre": "Ana",
                "email": "ana@correo.com",
                "contrasena": "Clave123",
                "telefono": "3001234567",
                "fechaNacimiento": "2000-01-01"
            }
            """;

        when(registrarClienteUseCase.ejecutar(anyLong(), any(), any(), any(), any(), any()))
                .thenThrow(new CorreoElectronicoDuplicadoException());

        // Act y Assert
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isConflict());
    }

    @Test
    void deberiaRetornar400CuandoLaContrasenaEsDebil() throws Exception {

        // Arrange
        String requestJson = """
            {
                "nombre": "Ana",
                "email": "ana@correo.com",
                "contrasena": "Clave123",
                "telefono": "3001234567",
                "fechaNacimiento": "2000-01-01"
            }
            """;

        when(registrarClienteUseCase.ejecutar(anyLong(), any(), any(), any(), any(), any()))
                .thenThrow(new ContrasenaDebilException());

        // Act y Assert
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400CuandoFaltaElTelefono() throws Exception {

        // Arrange
        String requestJson = """
            {
                "nombre": "Ana",
                "email": "ana@correo.com",
                "contrasena": "Clave123",
                "telefono": " ",
                "fechaNacimiento": "2000-01-01"
            }
            """;

        // Act y Assert
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400CuandoLaFechaDeNacimientoEsFutura() throws Exception {

        // Arrange
        String requestJson = """
            {
                "nombre": "Ana",
                "email": "ana@correo.com",
                "contrasena": "Clave123",
                "telefono": "3001234567",
                "fechaNacimiento": "2999-01-01"
            }
            """;

        // Act y Assert
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }
}
