package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.dto.response.UsuarioResponse;
import com.example.ecommerce.application.usecase.AutenticarUsuarioUseCase;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.CredencialesInvalidasException;
import com.example.ecommerce.domain.exception.UsuarioInactivoException;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    @MockitoBean
    private UsuarioMapper mapper;

    private static final String LOGIN_JSON = """
        {
            "email": "ana@correo.com",
            "contrasena": "clave123"
        }
        """;

    @Test
    void deberiaAutenticarConCredencialesCorrectas() throws Exception {

        // Arrange
        Usuario usuarioSimulado = Usuario.registrar(
                1L, "Ana", new Email("ana@correo.com"), "hash", RolUsuario.CLIENTE);
        when(autenticarUsuarioUseCase.ejecutar(anyString(), anyString()))
                .thenReturn(usuarioSimulado);
        when(mapper.toResponse(any()))
                .thenReturn(new UsuarioResponse(1L, "Ana", "ana@correo.com", RolUsuario.CLIENTE, true));

        // Act y Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ana@correo.com"))
                .andExpect(jsonPath("$.contrasenaHash").doesNotExist());
    }

    @Test
    void deberiaRetornar401CuandoLasCredencialesSonInvalidas() throws Exception {

        // Arrange
        when(autenticarUsuarioUseCase.ejecutar(anyString(), anyString()))
                .thenThrow(new CredencialesInvalidasException());

        // Act y Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deberiaRetornar403CuandoElUsuarioEstaInactivo() throws Exception {

        // Arrange
        when(autenticarUsuarioUseCase.ejecutar(anyString(), anyString()))
                .thenThrow(new UsuarioInactivoException());

        // Act y Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void deberiaRetornar400CuandoFaltaLaContrasena() throws Exception {

        // Arrange
        String requestJson = """
            {
                "email": "ana@correo.com",
                "contrasena": ""
            }
            """;

        // Act y Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }
}
