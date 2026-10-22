package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.usecase.ActualizarPerfilUseCase;
import com.example.ecommerce.application.usecase.CambiarContrasenaUseCase;
import com.example.ecommerce.application.usecase.ConsultarPerfilUseCase;
import com.example.ecommerce.domain.exception.ContrasenaActualIncorrectaException;
import com.example.ecommerce.application.dto.response.UsuarioResponse;
import com.example.ecommerce.application.usecase.RegistrarClienteUseCase;
import com.example.ecommerce.application.usecase.RegistrarVendedorUseCase;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.ContrasenaDebilException;
import com.example.ecommerce.domain.exception.CorreoElectronicoDuplicadoException;
import com.example.ecommerce.domain.exception.TokenInvalidoException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
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
    private RegistrarVendedorUseCase registrarVendedorUseCase;

    @MockitoBean
    private ConsultarPerfilUseCase consultarPerfilUseCase;

    @MockitoBean
    private ActualizarPerfilUseCase actualizarPerfilUseCase;

    @MockitoBean
    private CambiarContrasenaUseCase cambiarContrasenaUseCase;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UsuarioMapper mapper;


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
                "u-1", "Ana", new Email("ana@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);
        when(registrarClienteUseCase.ejecutar(anyString(), any(), any(), any(), any(), any()))
                .thenReturn(usuarioSimulado);
        when(mapper.toResponse(any()))
                .thenReturn(new UsuarioResponse("u-1", "Ana", "ana@correo.com", "3001234567", LocalDate.of(2000, 1, 1), RolUsuario.CLIENTE, true));

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

        when(registrarClienteUseCase.ejecutar(anyString(), any(), any(), any(), any(), any()))
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

        when(registrarClienteUseCase.ejecutar(anyString(), any(), any(), any(), any(), any()))
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

    private static final String VENDEDOR_JSON = """
            {
                "nombre": "Vera",
                "email": "vera@tienda.com",
                "contrasena": "Clave123",
                "telefono": "3001234567",
                "fechaNacimiento": "1995-03-10"
            }
            """;

    @Test
    void deberiaRegistrarUnVendedorConRolVendedor() throws Exception {

        // Arrange
        Usuario vendedorSimulado = Usuario.registrarVendedor(
                "u-2", "Vera", new Email("vera@tienda.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);
        when(registrarVendedorUseCase.ejecutar(anyString(), any(), any(), any(), any(), any()))
                .thenReturn(vendedorSimulado);
        when(mapper.toResponse(any()))
                .thenReturn(new UsuarioResponse("u-2", "Vera", "vera@tienda.com", "3001234567", LocalDate.of(1995, 3, 10), RolUsuario.VENDEDOR, true));

        // Act y Assert
        mockMvc.perform(post("/api/usuarios/vendedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VENDEDOR_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/api/usuarios/u-2")))
                .andExpect(jsonPath("$.rol").value("VENDEDOR"))
                .andExpect(jsonPath("$.contrasenaHash").doesNotExist());
    }

    @Test
    void deberiaRetornar409CuandoElCorreoDelVendedorYaEstaRegistrado() throws Exception {

        // Arrange
        when(registrarVendedorUseCase.ejecutar(anyString(), any(), any(), any(), any(), any()))
                .thenThrow(new CorreoElectronicoDuplicadoException());

        // Act y Assert
        mockMvc.perform(post("/api/usuarios/vendedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VENDEDOR_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void deberiaRetornar400CuandoAlVendedorLeFaltaElNombre() throws Exception {

        // Arrange
        String sinNombre = VENDEDOR_JSON.replace("\"nombre\": \"Vera\",", "");

        // Act y Assert
        mockMvc.perform(post("/api/usuarios/vendedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sinNombre))
                .andExpect(status().isBadRequest());
    }

    @Test
    void consultarPerfilDebeDevolverLosDatosDelUsuarioDelToken() throws Exception {
        // Arrange
        Usuario ana = Usuario.registrarCliente("u-1", "Ana", new Email("ana@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);
        when(tokenService.extraerUsuarioId("token-ana")).thenReturn("u-1");
        when(consultarPerfilUseCase.ejecutar("u-1")).thenReturn(ana);
        when(mapper.toResponse(ana))
                .thenReturn(new UsuarioResponse("u-1", "Ana", "ana@correo.com", "3001234567", LocalDate.of(2000, 1, 1), RolUsuario.CLIENTE, true));

        // Act y Assert
        mockMvc.perform(get("/api/usuarios/me").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("u-1"))
                .andExpect(jsonPath("$.email").value("ana@correo.com"))
                .andExpect(jsonPath("$.contrasenaHash").doesNotExist());
    }

    @Test
    void consultarPerfilSinTokenDebeResponder401() throws Exception {
        mockMvc.perform(get("/api/usuarios/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void consultarPerfilConTokenInvalidoDebeResponder401() throws Exception {
        when(tokenService.extraerUsuarioId("token-falso")).thenThrow(new TokenInvalidoException());

        mockMvc.perform(get("/api/usuarios/me").header("Authorization", "Bearer token-falso"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void actualizarPerfilDebeDevolverLosDatosNuevos() throws Exception {
        // Arrange
        Usuario ana = Usuario.registrarCliente("u-1", "Ana María", new Email("ana@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);
        when(tokenService.extraerUsuarioId("token-ana")).thenReturn("u-1");
        when(actualizarPerfilUseCase.ejecutar("u-1", "Ana María", "3109876543")).thenReturn(ana);
        when(mapper.toResponse(ana))
                .thenReturn(new UsuarioResponse("u-1", "Ana María", "ana@correo.com", "3109876543", LocalDate.of(2000, 1, 1), RolUsuario.CLIENTE, true));

        // Act y Assert
        mockMvc.perform(put("/api/usuarios/me")
                        .header("Authorization", "Bearer token-ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\": \"Ana María\", \"telefono\": \"3109876543\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana María"))
                .andExpect(jsonPath("$.telefono").value("3109876543"));
    }

    @Test
    void actualizarPerfilSinNombreDebeResponder400() throws Exception {
        when(tokenService.extraerUsuarioId("token-ana")).thenReturn("u-1");

        mockMvc.perform(put("/api/usuarios/me")
                        .header("Authorization", "Bearer token-ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"telefono\": \"3109876543\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cambiarContrasenaDebeResponder204() throws Exception {
        when(tokenService.extraerUsuarioId("token-ana")).thenReturn("u-1");

        mockMvc.perform(put("/api/usuarios/me/contrasena")
                        .header("Authorization", "Bearer token-ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"contrasenaActual\": \"Clave123\", \"nuevaContrasena\": \"NuevaClave9\"}"))
                .andExpect(status().isNoContent());

        verify(cambiarContrasenaUseCase).ejecutar("u-1", "Clave123", "NuevaClave9");
    }

    @Test
    void cambiarContrasenaConLaActualIncorrectaDebeResponder400() throws Exception {
        when(tokenService.extraerUsuarioId("token-ana")).thenReturn("u-1");
        doThrow(new ContrasenaActualIncorrectaException())
                .when(cambiarContrasenaUseCase).ejecutar("u-1", "Equivocada1", "NuevaClave9");

        mockMvc.perform(put("/api/usuarios/me/contrasena")
                        .header("Authorization", "Bearer token-ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"contrasenaActual\": \"Equivocada1\", \"nuevaContrasena\": \"NuevaClave9\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cambiarContrasenaSinTokenDebeResponder401() throws Exception {
        mockMvc.perform(put("/api/usuarios/me/contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"contrasenaActual\": \"Clave123\", \"nuevaContrasena\": \"NuevaClave9\"}"))
                .andExpect(status().isUnauthorized());
    }
}
