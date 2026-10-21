package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.application.dto.response.UsuarioResponse;
import com.example.ecommerce.application.usecase.RegistrarClienteUseCase;
import com.example.ecommerce.application.usecase.RegistrarVendedorUseCase;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.ContrasenaDebilException;
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

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    private RegistrarVendedorUseCase registrarVendedorUseCase;

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
}
