package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.dto.response.CarritoResponse;
import com.example.ecommerce.application.usecase.AgregarAlCarritoUseCase;
import com.example.ecommerce.domain.entity.Carrito;
import com.example.ecommerce.domain.exception.ArticuloAgotadoException;
import com.example.ecommerce.infrastructure.rest.mapper.CarritoMapper;

import com.example.ecommerce.application.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import com.example.ecommerce.application.usecase.VaciarCarritoUseCase;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import com.example.ecommerce.application.usecase.QuitarArticuloDelCarritoUseCase;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoEnCarritoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.ecommerce.application.usecase.VerCarritoUseCase;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(CarritoController.class)
class CarritoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AgregarAlCarritoUseCase agregarAlCarritoUseCase;
    @MockitoBean
    private VerCarritoUseCase verCarritoUseCase;

    @MockitoBean
    private CarritoMapper mapper;

    @MockitoBean
    private VaciarCarritoUseCase vaciarCarritoUseCase;
    @MockitoBean
    private QuitarArticuloDelCarritoUseCase quitarArticuloDelCarritoUseCase;

    @MockitoBean
    private TokenService tokenService;

    @BeforeEach
    void sesionDeAna() {
        when(tokenService.extraerUsuarioId("token-ana")).thenReturn("u-100");
    }

    @Test
    void deberiaAgregarArticuloCuandoDatosValidos() throws Exception {

        // Arrange
        String requestJson = """
                {
                    "articuloId": 1,
                    "cantidad": 2
                }
                """;

        Carrito carritoSimulado = Carrito.crear("c-1", "u-100");
        when(agregarAlCarritoUseCase.ejecutar(anyString(), anyString(), anyLong(), anyInt()))
                .thenReturn(carritoSimulado);
        when(mapper.toResponse(any()))
                .thenReturn(new CarritoResponse(1L, "u-100", List.of(), new BigDecimal("40000")));

        // Act y Assert
        mockMvc.perform(post("/api/carrito/items").header("Authorization", "Bearer token-ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value("u-100"))
                .andExpect(jsonPath("$.total").value(40000));
    }

    @Test
    void deberiaRetornar400CuandoLaCantidadEsCero() throws Exception {

        // Arrange
        String requestJson = """
                {
                    "articuloId": 1,
                    "cantidad": 0
                }
                """;

        // Act y Assert
        mockMvc.perform(post("/api/carrito/items").header("Authorization", "Bearer token-ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400CuandoElArticuloEstaAgotado() throws Exception {

        // Arrange
        String requestJson = """
                {
                    "articuloId": 1,
                    "cantidad": 1
                }
                """;

        when(agregarAlCarritoUseCase.ejecutar(anyString(), anyString(), anyLong(), anyInt()))
                .thenThrow(new ArticuloAgotadoException());

        // Act y Assert
        mockMvc.perform(post("/api/carrito/items").header("Authorization", "Bearer token-ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar404CuandoElArticuloNoExiste() throws Exception {

        // Arrange
        String requestJson = """
                {
                    "articuloId": 99,
                    "cantidad": 1
                }
                """;

        when(agregarAlCarritoUseCase.ejecutar(anyString(), anyString(), anyLong(), anyInt()))
                .thenThrow(new NoSuchElementException());

        // Act y Assert
        mockMvc.perform(post("/api/carrito/items").header("Authorization", "Bearer token-ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void deberiaMostrarElCarritoDelCliente() throws Exception {

        // Arrange
        when(verCarritoUseCase.ejecutar(anyString()))
                .thenReturn(Carrito.crear("c-1", "u-100"));
        when(mapper.toResponse(any()))
                .thenReturn(new CarritoResponse(1L, "u-100", List.of(), new BigDecimal("90000")));

        // Act y Assert
        mockMvc.perform(get("/api/carrito").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value("u-100"))
                .andExpect(jsonPath("$.total").value(90000));
    }

    @Test
    void deberiaRetornar404CuandoElClienteNoTieneCarrito() throws Exception {

        // Arrange
        when(verCarritoUseCase.ejecutar(anyString()))
                .thenThrow(new NoSuchElementException());

        // Act y Assert
        mockMvc.perform(get("/api/carrito").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isNotFound());
    }
    @Test
    void deberiaQuitarElArticuloYRetornar204() throws Exception {

        // Act y Assert
        mockMvc.perform(delete("/api/carrito/items/1").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isNoContent());

        verify(quitarArticuloDelCarritoUseCase).ejecutar("u-100", 1L);
    }

    @Test
    void deberiaRetornar404AlQuitarCuandoElClienteNoTieneCarrito() throws Exception {

        // Arrange
        doThrow(new NoSuchElementException())
                .when(quitarArticuloDelCarritoUseCase).ejecutar(anyString(), anyLong());

        // Act y Assert
        mockMvc.perform(delete("/api/carrito/items/1").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deberiaRetornar400CuandoElArticuloNoEstaEnElCarrito() throws Exception {

        // Arrange
        doThrow(new ArticuloNoEncontradoEnCarritoException())
                .when(quitarArticuloDelCarritoUseCase).ejecutar(anyString(), anyLong());

        // Act y Assert
        mockMvc.perform(delete("/api/carrito/items/99").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sinTokenDebeResponder401() throws Exception {
        mockMvc.perform(get("/api/carrito"))
                .andExpect(status().isUnauthorized());
    }
}
