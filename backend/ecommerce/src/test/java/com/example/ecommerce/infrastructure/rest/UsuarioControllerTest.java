package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.dto.response.UsuarioResponse;
import com.example.ecommerce.application.usecase.RegistrarUsuarioUseCase;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.CorreoElectronicoDuplicadoException;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.domain.valueobject.RolUsuario;
import com.example.ecommerce.infrastructure.rest.mapper.UsuarioMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
    private RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @MockitoBean
    private UsuarioMapper mapper;

    @Test
    void deberiaRegistrarUsuarioYNoExponerLaContrasena() throws Exception {

        // Arrange
        String requestJson = """
            {
                "nombre": "Ana",
                "email": "ana@correo.com",
                "contrasena": "clave123",
                "rol": "CLIENTE"
            }
            """;

        Usuario usuarioSimulado = Usuario.registrar(
                1L, "Ana", new Email("ana@correo.com"), "hash", RolUsuario.CLIENTE);
        when(registrarUsuarioUseCase.ejecutar(anyLong(), any(), any(), any(), any()))
                .thenReturn(usuarioSimulado);
        when(mapper.toResponse(any()))
                .thenReturn(new UsuarioResponse(1L, "Ana", "ana@correo.com", RolUsuario.CLIENTE, true));

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
                "contrasena": "clave123"
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
                "contrasena": "clave123"
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
                "contrasena": "clave123"
            }
            """;

        when(registrarUsuarioUseCase.ejecutar(anyLong(), any(), any(), any(), any()))
                .thenThrow(new CorreoElectronicoDuplicadoException());

        // Act y Assert
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isConflict());
    }
}
