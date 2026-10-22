package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.application.SesionIniciada;
import com.example.ecommerce.application.dto.response.LoginResponse;
import com.example.ecommerce.application.dto.response.UsuarioResponse;
import com.example.ecommerce.application.usecase.CerrarSesionUseCase;
import com.example.ecommerce.application.usecase.IniciarSesionUseCase;
import com.example.ecommerce.application.usecase.RestablecerContrasenaUseCase;
import com.example.ecommerce.application.usecase.SolicitarRecuperacionContrasenaUseCase;
import com.example.ecommerce.domain.exception.CodigoRecuperacionInvalidoException;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.CredencialesInvalidasException;
import com.example.ecommerce.domain.exception.TokenInvalidoException;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
    private CerrarSesionUseCase cerrarSesionUseCase;

    @MockitoBean
    private SolicitarRecuperacionContrasenaUseCase solicitarRecuperacionUseCase;

    @MockitoBean
    private RestablecerContrasenaUseCase restablecerContrasenaUseCase;

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
                "u-1", "Ana", new Email("ana@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);
        when(iniciarSesionUseCase.ejecutar(anyString(), anyString()))
                .thenReturn(new SesionIniciada("token.de.prueba", usuarioSimulado));
        when(mapper.toLoginResponse(any()))
                .thenReturn(new LoginResponse("token.de.prueba", "Bearer",
                        new UsuarioResponse("u-1", "Ana", "ana@correo.com", "3001234567",
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

    @Test
    void logoutDebeResponder204YCerrarLaSesionDelToken() throws Exception {
        // Act y Assert
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer token-valido"))
                .andExpect(status().isNoContent());

        verify(cerrarSesionUseCase).ejecutar("token-valido");
    }

    @Test
    void logoutSinTokenDebeResponder401() throws Exception {
        // Act y Assert
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(cerrarSesionUseCase);
    }

    @Test
    void logoutConTokenInvalidoOYaCerradoDebeResponder401() throws Exception {
        // Arrange
        doThrow(new TokenInvalidoException()).when(cerrarSesionUseCase).ejecutar("token-cerrado");

        // Act y Assert
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer token-cerrado"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void recuperarContrasenaDebeResponder202() throws Exception {
        mockMvc.perform(post("/api/auth/recuperar-contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"ana@correo.com\"}"))
                .andExpect(status().isAccepted());

        verify(solicitarRecuperacionUseCase).ejecutar("ana@correo.com");
    }

    @Test
    void restablecerContrasenaConCodigoValidoDebeResponder204() throws Exception {
        mockMvc.perform(post("/api/auth/restablecer-contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"ana@correo.com\", \"codigo\": \"123456\", \"nuevaContrasena\": \"NuevaClave9\"}"))
                .andExpect(status().isNoContent());

        verify(restablecerContrasenaUseCase).ejecutar("ana@correo.com", "123456", "NuevaClave9");
    }

    @Test
    void restablecerContrasenaConCodigoInvalidoDebeResponder400() throws Exception {
        doThrow(new CodigoRecuperacionInvalidoException())
                .when(restablecerContrasenaUseCase).ejecutar("ana@correo.com", "999999", "NuevaClave9");

        mockMvc.perform(post("/api/auth/restablecer-contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"ana@correo.com\", \"codigo\": \"999999\", \"nuevaContrasena\": \"NuevaClave9\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void restablecerContrasenaConCodigoQueNoTiene6DigitosDebeResponder400() throws Exception {
        mockMvc.perform(post("/api/auth/restablecer-contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"ana@correo.com\", \"codigo\": \"12ab\", \"nuevaContrasena\": \"NuevaClave9\"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(restablecerContrasenaUseCase);
    }
}
