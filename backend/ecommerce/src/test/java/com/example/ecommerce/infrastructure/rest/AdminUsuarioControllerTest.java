package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.response.UsuarioResponse;
import com.example.ecommerce.application.usecase.CrearUsuarioPorAdministradorUseCase;
import com.example.ecommerce.application.usecase.DesactivarUsuarioUseCase;
import com.example.ecommerce.application.usecase.ListarUsuariosUseCase;
import com.example.ecommerce.domain.exception.UsuarioYaDesactivadoException;
import com.example.ecommerce.domain.exception.AccionSoloParaAdministradorException;
import com.example.ecommerce.domain.valueobject.FiltroUsuarios;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.CreacionUsuarioNoPermitidaException;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    private ListarUsuariosUseCase listarUsuariosUseCase;

    @MockitoBean
    private DesactivarUsuarioUseCase desactivarUsuarioUseCase;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UsuarioMapper mapper;


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
        Usuario vendedor = Usuario.reconstruir("u-2", "Vera", new Email("vera@tienda.com"), "hash",
                DatosPrueba.TELEFONO, DatosPrueba.FECHA, RolUsuario.VENDEDOR, true);
        when(tokenService.extraerUsuarioId("token-valido")).thenReturn("u-1");
        when(crearUsuarioUseCase.ejecutar(eq("u-1"), anyString(), any(), any(), any(), any(), any(), any()))
                .thenReturn(vendedor);
        when(mapper.toResponse(any()))
                .thenReturn(new UsuarioResponse("u-2", "Vera", "vera@tienda.com", "3001234567",
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
        when(tokenService.extraerUsuarioId("token-cliente")).thenReturn("u-9");
        when(crearUsuarioUseCase.ejecutar(eq("u-9"), anyString(), any(), any(), any(), any(), any(), any()))
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
        when(tokenService.extraerUsuarioId("token-valido")).thenReturn("u-1");

        // Act y Assert
        mockMvc.perform(post("/api/admin/usuarios")
                        .header("Authorization", "Bearer token-valido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarUsuariosDebeAplicarLosFiltrosYDevolverLaLista() throws Exception {
        // Arrange
        Usuario vera = Usuario.registrarVendedor("u-2", "Vera", new Email("vera@tienda.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);
        when(tokenService.extraerUsuarioId("token-admin")).thenReturn("a-1");
        when(listarUsuariosUseCase.ejecutar("a-1", new FiltroUsuarios(RolUsuario.VENDEDOR, true))).thenReturn(List.of(vera));
        when(mapper.toResponse(vera))
                .thenReturn(new UsuarioResponse("u-2", "Vera", "vera@tienda.com", "3001234567", LocalDate.of(1995, 3, 10), RolUsuario.VENDEDOR, true));

        // Act y Assert
        mockMvc.perform(get("/api/admin/usuarios")
                        .header("Authorization", "Bearer token-admin")
                        .param("rol", "VENDEDOR")
                        .param("activo", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("u-2"))
                .andExpect(jsonPath("$[0].rol").value("VENDEDOR"));
    }

    @Test
    void listarUsuariosSinSerAdministradorDebeResponder403() throws Exception {
        when(tokenService.extraerUsuarioId("token-cliente")).thenReturn("u-1");
        when(listarUsuariosUseCase.ejecutar(eq("u-1"), any())).thenThrow(new AccionSoloParaAdministradorException());

        mockMvc.perform(get("/api/admin/usuarios").header("Authorization", "Bearer token-cliente"))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarUsuariosSinTokenDebeResponder401() throws Exception {
        mockMvc.perform(get("/api/admin/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void desactivarUsuarioDebeResponder204() throws Exception {
        when(tokenService.extraerUsuarioId("token-admin")).thenReturn("a-1");

        mockMvc.perform(put("/api/admin/usuarios/u-1/desactivar").header("Authorization", "Bearer token-admin"))
                .andExpect(status().isNoContent());

        verify(desactivarUsuarioUseCase).ejecutar("a-1", "u-1");
    }

    @Test
    void desactivarUsuarioSinSerAdministradorDebeResponder403() throws Exception {
        when(tokenService.extraerUsuarioId("token-cliente")).thenReturn("u-2");
        doThrow(new AccionSoloParaAdministradorException()).when(desactivarUsuarioUseCase).ejecutar("u-2", "u-1");

        mockMvc.perform(put("/api/admin/usuarios/u-1/desactivar").header("Authorization", "Bearer token-cliente"))
                .andExpect(status().isForbidden());
    }

    @Test
    void desactivarUnUsuarioQueNoExisteDebeResponder404() throws Exception {
        when(tokenService.extraerUsuarioId("token-admin")).thenReturn("a-1");
        doThrow(new RecursoNoEncontradoException("el recurso", "no-existe")).when(desactivarUsuarioUseCase).ejecutar("a-1", "no-existe");

        mockMvc.perform(put("/api/admin/usuarios/no-existe/desactivar").header("Authorization", "Bearer token-admin"))
                .andExpect(status().isNotFound());
    }

    @Test
    void desactivarUnUsuarioYaDesactivadoDebeResponder400() throws Exception {
        when(tokenService.extraerUsuarioId("token-admin")).thenReturn("a-1");
        doThrow(new UsuarioYaDesactivadoException()).when(desactivarUsuarioUseCase).ejecutar("a-1", "u-1");

        mockMvc.perform(put("/api/admin/usuarios/u-1/desactivar").header("Authorization", "Bearer token-admin"))
                .andExpect(status().isBadRequest());
    }
}
