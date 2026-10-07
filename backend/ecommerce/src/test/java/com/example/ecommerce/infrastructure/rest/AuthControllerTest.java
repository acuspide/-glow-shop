package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.application.SesionIniciada;
import com.example.ecommerce.application.dto.response.LoginResponse;
import com.example.ecommerce.application.dto.response.UsuarioResponse;
import com.example.ecommerce.application.usecase.IniciarSesionUseCase;
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

import java.time.LocalDate;

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
    private IniciarSesionUseCase iniciarSesionUseCase;

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
        Usuario usuarioSimulado = Usuario.registrarCliente(
                1L, "Ana", new Email("ana@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);
        when(iniciarSesionUseCase.ejecutar(anyString(), anyString()))
                .thenReturn(new SesionIniciada("token.de.prueba", usuarioSimulado));
        when(mapper.toLoginResponse(any()))
                .thenReturn(new LoginResponse("token.de.prueba", "Bearer",
                        new UsuarioResponse(1L, "Ana", "ana@correo.com", "3001234567",
                                LocalDate.of(2000, 1, 1), RolUsuario.CLIENTE, true)));

        // Act y Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token.de.prueba"))
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.usuario.email").value("ana@correo.com"))
                .andExpect(jsonPath("$.usuario.contrasenaHash").doesNotExist());
    }

    @Test
    void deberiaRetornar401CuandoLasCredencialesSonInvalidas() throws Exception {

        // Arrange
        when(iniciarSesionUseCase.ejecutar(anyString(), anyString()))
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
        when(iniciarSesionUseCase.ejecutar(anyString(), anyString()))
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
