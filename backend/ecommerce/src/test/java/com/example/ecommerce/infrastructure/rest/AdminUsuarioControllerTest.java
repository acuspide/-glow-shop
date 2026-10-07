package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.response.UsuarioResponse;
import com.example.ecommerce.application.usecase.CrearUsuarioPorAdministradorUseCase;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.CreacionUsuarioNoPermitidaException;
import com.example.ecommerce.domain.exception.TokenInvalidoException;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminUsuarioController.class)
class AdminUsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CrearUsuarioPorAdministradorUseCase crearUsuarioUseCase;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UsuarioMapper mapper;

    @MockitoBean
    private SecuenciaIdUsuario secuenciaId;

    private static final String VENDEDOR_JSON = """
        {
            "nombre": "Vera",
            "email": "vera@tienda.com",
            "contrasena": "Clave123",
            "telefono": "3001234567",
            "fechaNacimiento": "1995-03-10",
            "rol": "VENDEDOR"
        }
        """;

    @Test
    void deberiaCrearUnVendedorCuandoQuienPideEsAdministrador() throws Exception {

        // Arrange
        Usuario vendedor = new Usuario(2L, "Vera", new Email("vera@tienda.com"), "hash",
                DatosPrueba.TELEFONO, DatosPrueba.FECHA, RolUsuario.VENDEDOR);
        when(tokenService.extraerUsuarioId("token-valido")).thenReturn(1L);
        when(crearUsuarioUseCase.ejecutar(eq(1L), anyLong(), any(), any(), any(), any(), any(), any()))
                .thenReturn(vendedor);
        when(mapper.toResponse(any()))
                .thenReturn(new UsuarioResponse(2L, "Vera", "vera@tienda.com", "3001234567",
                        LocalDate.of(1995, 3, 10), RolUsuario.VENDEDOR, true));

        // Act y Assert
        mockMvc.perform(post("/api/admin/usuarios")
                        .header("Authorization", "Bearer token-valido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VENDEDOR_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.rol").value("VENDEDOR"))
                .andExpect(jsonPath("$.contrasenaHash").doesNotExist());
    }

    @Test
    void deberiaRetornar401CuandoNoSeEnviaElToken() throws Exception {

        // Act y Assert
        mockMvc.perform(post("/api/admin/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VENDEDOR_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deberiaRetornar401CuandoElTokenEsInvalido() throws Exception {

        // Arrange
        when(tokenService.extraerUsuarioId("token-falso")).thenThrow(new TokenInvalidoException());

        // Act y Assert
        mockMvc.perform(post("/api/admin/usuarios")
                        .header("Authorization", "Bearer token-falso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VENDEDOR_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deberiaRetornar403CuandoQuienPideNoEsAdministrador() throws Exception {

        // Arrange
        when(tokenService.extraerUsuarioId("token-cliente")).thenReturn(9L);
        when(crearUsuarioUseCase.ejecutar(eq(9L), anyLong(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new CreacionUsuarioNoPermitidaException());

        // Act y Assert
        mockMvc.perform(post("/api/admin/usuarios")
                        .header("Authorization", "Bearer token-cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VENDEDOR_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void deberiaRetornar400CuandoFaltaElRol() throws Exception {

        // Arrange
        String requestJson = """
            {
                "nombre": "Vera",
                "email": "vera@tienda.com",
                "contrasena": "Clave123",
                "telefono": "3001234567",
                "fechaNacimiento": "1995-03-10"
            }
            """;
        when(tokenService.extraerUsuarioId("token-valido")).thenReturn(1L);

        // Act y Assert
        mockMvc.perform(post("/api/admin/usuarios")
                        .header("Authorization", "Bearer token-valido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }
}
