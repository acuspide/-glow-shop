package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.response.PedidoResponse;
import com.example.ecommerce.application.usecase.ConfirmarPedidoUseCase;
import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.exception.CarritoVacioException;
import com.example.ecommerce.domain.exception.PrecioDelArticuloCambioException;
import com.example.ecommerce.domain.valueobject.DetallePedido;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.infrastructure.rest.mapper.PedidoMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConfirmarPedidoUseCase confirmarPedidoUseCase;

    @MockitoBean
    private PedidoMapper mapper;

    @MockitoBean
    private TokenService tokenService;

    @BeforeEach
    void sesionDeAna() {
        when(tokenService.extraerUsuarioId("token-ana")).thenReturn("u-100");
    }

    @Test
    void deberiaConfirmarElPedidoYRetornar201() throws Exception {

        // Arrange
        Pedido pedidoSimulado = Pedido.crear("p-1", "u-100",
                List.of(new DetallePedido("a-1", 2, new Precio(new BigDecimal("20000")))));
        when(confirmarPedidoUseCase.ejecutar(anyString(), eq("u-100"))).thenReturn(pedidoSimulado);
        when(mapper.toResponse(any()))
                .thenReturn(new PedidoResponse("p-1", "u-100", "CONFIRMADO", List.of(),
                        new BigDecimal("40000"), new BigDecimal("40000")));

        // Act y Assert
        mockMvc.perform(post("/api/pedidos").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isCreated())                     // HTTP 201
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.estado").value("CONFIRMADO"))
                .andExpect(jsonPath("$.total").value(40000));

        verify(confirmarPedidoUseCase).ejecutar(anyString(), eq("u-100"));
    }

    @Test
    void deberiaRetornar400CuandoElCarritoEstaVacio() throws Exception {

        // Arrange
        when(confirmarPedidoUseCase.ejecutar(anyString(), anyString()))
                .thenThrow(new CarritoVacioException());

        // Act y Assert
        mockMvc.perform(post("/api/pedidos").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400CuandoElPrecioDeUnArticuloCambio() throws Exception {

        // Arrange
        when(confirmarPedidoUseCase.ejecutar(anyString(), anyString()))
                .thenThrow(new PrecioDelArticuloCambioException("a-1"));

        // Act y Assert
        mockMvc.perform(post("/api/pedidos").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar404CuandoElClienteNoTieneCarrito() throws Exception {

        // Arrange
        when(confirmarPedidoUseCase.ejecutar(anyString(), anyString()))
                .thenThrow(new NoSuchElementException());

        // Act y Assert
        mockMvc.perform(post("/api/pedidos").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isNotFound());
    }

    @Test
    void sinTokenDebeResponder401() throws Exception {

        // Act y Assert
        mockMvc.perform(post("/api/pedidos"))
                .andExpect(status().isUnauthorized());

        verify(confirmarPedidoUseCase, never()).ejecutar(anyString(), anyString());
    }
}
