package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.response.CarritoResponse;
import com.example.ecommerce.application.usecase.*;
import com.example.ecommerce.domain.entity.Carrito;
import com.example.ecommerce.domain.exception.*;
import com.example.ecommerce.infrastructure.rest.mapper.CarritoMapper;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @MockitoBean
    private ModificarCantidadEnCarritoUseCase modificarCantidadEnCarritoUseCase;

    @MockitoBean
    private ValidarDisponibilidadDelCarritoUseCase validarDisponibilidadDelCarritoUseCase;

    @BeforeEach
    void sesionDeAna() {
        when(tokenService.extraerUsuarioId("token-ana")).thenReturn("u-100");
    }

    @Test
    void deberiaAgregarArticuloCuandoDatosValidos() throws Exception {

        // Arrange
        String requestJson = """
                {
                    "articuloId": "a-1",
                    "cantidad": 2
                }
                """;

        Carrito carritoSimulado = Carrito.crear("c-1", "u-100");
        when(agregarAlCarritoUseCase.ejecutar(anyString(), anyString(), anyString(), anyInt()))
                .thenReturn(carritoSimulado);
        when(mapper.toResponse(any()))
                .thenReturn(new CarritoResponse("c-1", "u-100", List.of(), new BigDecimal("40000")));

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
                    "articuloId": "a-1",
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
                    "articuloId": "a-1",
                    "cantidad": 1
                }
                """;

        when(agregarAlCarritoUseCase.ejecutar(anyString(), anyString(), anyString(), anyInt()))
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
                    "articuloId": "a-99",
                    "cantidad": 1
                }
                """;

        when(agregarAlCarritoUseCase.ejecutar(anyString(), anyString(), anyString(), anyInt()))
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
                .thenReturn(new CarritoResponse("c-1", "u-100", List.of(), new BigDecimal("90000")));

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
        mockMvc.perform(delete("/api/carrito/items/a-1").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isNoContent());

        verify(quitarArticuloDelCarritoUseCase).ejecutar("u-100", "a-1");
    }

    @Test
    void deberiaRetornar404AlQuitarCuandoElClienteNoTieneCarrito() throws Exception {

        // Arrange
        doThrow(new NoSuchElementException())
                .when(quitarArticuloDelCarritoUseCase).ejecutar(anyString(), anyString());

        // Act y Assert
        mockMvc.perform(delete("/api/carrito/items/a-1").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deberiaRetornar400CuandoElArticuloNoEstaEnElCarrito() throws Exception {

        // Arrange
        doThrow(new ArticuloNoEncontradoEnCarritoException())
                .when(quitarArticuloDelCarritoUseCase).ejecutar(anyString(), anyString());

        // Act y Assert
        mockMvc.perform(delete("/api/carrito/items/a-99").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaModificarLaCantidadYRetornar204() throws Exception {

        // Arrange
        String requestJson = """
                {
                    "cantidad": 5
                }
                """;

        // Act y Assert
        mockMvc.perform(put("/api/carrito/items/a-1").header("Authorization", "Bearer token-ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNoContent());

        verify(modificarCantidadEnCarritoUseCase).ejecutar("u-100", "a-1", 5);
    }

    @Test
    void deberiaRetornar400AlModificarACantidadCero() throws Exception {

        // Arrange
        String requestJson = """
                {
                    "cantidad": 0
                }
                """;

        // Act y Assert
        mockMvc.perform(put("/api/carrito/items/a-1").header("Authorization", "Bearer token-ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(modificarCantidadEnCarritoUseCase, never()).ejecutar(anyString(), anyString(), anyInt());
    }

    @Test
    void deberiaRetornar400CuandoLaNuevaCantidadSuperaElStock() throws Exception {

        // Arrange
        String requestJson = """
                {
                    "cantidad": 11
                }
                """;

        doThrow(new CantidadSuperaStockException())
                .when(modificarCantidadEnCarritoUseCase).ejecutar(anyString(), anyString(), anyInt());

        // Act y Assert
        mockMvc.perform(put("/api/carrito/items/a-1").header("Authorization", "Bearer token-ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar404AlModificarCuandoElClienteNoTieneCarrito() throws Exception {

        // Arrange
        String requestJson = """
                {
                    "cantidad": 5
                }
                """;

        doThrow(new NoSuchElementException())
                .when(modificarCantidadEnCarritoUseCase).ejecutar(anyString(), anyString(), anyInt());

        // Act y Assert
        mockMvc.perform(put("/api/carrito/items/a-1").header("Authorization", "Bearer token-ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void deberiaVaciarElCarritoYRetornar204() throws Exception {

        // Act y Assert
        mockMvc.perform(delete("/api/carrito/items").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isNoContent());

        verify(vaciarCarritoUseCase).ejecutar("u-100");
    }

    @Test
    void deberiaRetornar404AlVaciarCuandoElClienteNoTieneCarrito() throws Exception {

        // Arrange
        doThrow(new NoSuchElementException())
                .when(vaciarCarritoUseCase).ejecutar(anyString());

        // Act y Assert
        mockMvc.perform(delete("/api/carrito/items").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isNotFound());
    }
    @Test
    void deberiaRetornar204CuandoElCarritoPuedePagarse() throws Exception {

        // Act y Assert
        mockMvc.perform(get("/api/carrito/disponibilidad").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isNoContent());

        verify(validarDisponibilidadDelCarritoUseCase).ejecutar("u-100");
    }

    @Test
    void deberiaRetornar400CuandoElPrecioDeUnArticuloCambio() throws Exception {

        // Arrange
        doThrow(new PrecioDelArticuloCambioException("a-1"))
                .when(validarDisponibilidadDelCarritoUseCase).ejecutar(anyString());

        // Act y Assert
        mockMvc.perform(get("/api/carrito/disponibilidad").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400CuandoElCarritoEstaVacio() throws Exception {

        // Arrange
        doThrow(new CarritoVacioException())
                .when(validarDisponibilidadDelCarritoUseCase).ejecutar(anyString());

        // Act y Assert
        mockMvc.perform(get("/api/carrito/disponibilidad").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar404AlValidarCuandoElClienteNoTieneCarrito() throws Exception {

        // Arrange
        doThrow(new NoSuchElementException())
                .when(validarDisponibilidadDelCarritoUseCase).ejecutar(anyString());

        // Act y Assert
        mockMvc.perform(get("/api/carrito/disponibilidad").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isNotFound());
    }
    @Test
    void sinTokenDebeResponder401() throws Exception {
        mockMvc.perform(get("/api/carrito"))
                .andExpect(status().isUnauthorized());
    }
}
