package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.response.PagoResponse;
import com.example.ecommerce.application.usecase.ProcesarPagoDePedidoUseCase;
import com.example.ecommerce.domain.entity.Pago;
import com.example.ecommerce.domain.exception.PagoNoRegistrableException;
import com.example.ecommerce.domain.exception.PedidoAjenoException;
import com.example.ecommerce.domain.valueobject.MedioPago;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.infrastructure.rest.mapper.PagoMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
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

@WebMvcTest(PagoController.class)
class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProcesarPagoDePedidoUseCase procesarPagoDePedidoUseCase;

    @MockitoBean
    private PagoMapper mapper;

    @MockitoBean
    private TokenService tokenService;

    private static final String CON_NEQUI = """
            {
                "medioPago": "NEQUI"
            }
            """;

    @BeforeEach
    void sesionDeAna() {
        when(tokenService.extraerUsuarioId("token-ana")).thenReturn("u-100");
    }

    private Pago pagoSimulado() {
        return Pago.crear("pa-1", "p-1", new Precio(new BigDecimal("40000")), MedioPago.NEQUI);
    }

    @Test
    void deberiaProcesarElPagoYRetornar201ConElPagoAprobado() throws Exception {

        // Arrange
        when(procesarPagoDePedidoUseCase.ejecutar(anyString(), eq("p-1"), eq("u-100"), eq(MedioPago.NEQUI)))
                .thenReturn(pagoSimulado());
        when(mapper.toResponse(any()))
                .thenReturn(new PagoResponse("pa-1", "p-1", new BigDecimal("40000"), "NEQUI",
                        "APROBADO", "tx-1", null));

        // Act y Assert
        mockMvc.perform(post("/api/pedidos/p-1/pagos").header("Authorization", "Bearer token-ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CON_NEQUI))
                .andExpect(status().isCreated())                    // HTTP 201
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.estado").value("APROBADO"));

        verify(procesarPagoDePedidoUseCase).ejecutar(anyString(), eq("p-1"), eq("u-100"), eq(MedioPago.NEQUI));
    }

    @Test
    void unPagoRechazadoTambienRetorna201ConSuMotivo() throws Exception {

        // Arrange
        when(procesarPagoDePedidoUseCase.ejecutar(anyString(), anyString(), anyString(), any()))
                .thenReturn(pagoSimulado());
        when(mapper.toResponse(any()))
                .thenReturn(new PagoResponse("pa-1", "p-1", new BigDecimal("1500000"), "NEQUI",
                        "RECHAZADO", "tx-2", "Fondos insuficientes"));

        // Act y Assert
        mockMvc.perform(post("/api/pedidos/p-1/pagos").header("Authorization", "Bearer token-ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CON_NEQUI))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("RECHAZADO"))
                .andExpect(jsonPath("$.motivoRechazo").value("Fondos insuficientes"));
    }

    @Test
    void deberiaRetornar400CuandoNoSeEnviaElMedioDePago() throws Exception {

        // Act y Assert
        mockMvc.perform(post("/api/pedidos/p-1/pagos").header("Authorization", "Bearer token-ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(procesarPagoDePedidoUseCase, never()).ejecutar(anyString(), anyString(), anyString(), any());
    }

    @Test
    void deberiaRetornar400CuandoElPedidoNoAceptaPago() throws Exception {

        // Arrange
        when(procesarPagoDePedidoUseCase.ejecutar(anyString(), anyString(), anyString(), any()))
                .thenThrow(new PagoNoRegistrableException());

        // Act y Assert
        mockMvc.perform(post("/api/pedidos/p-1/pagos").header("Authorization", "Bearer token-ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CON_NEQUI))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar403CuandoElPedidoEsDeOtroCliente() throws Exception {

        // Arrange
        when(procesarPagoDePedidoUseCase.ejecutar(anyString(), anyString(), anyString(), any()))
                .thenThrow(new PedidoAjenoException());

        // Act y Assert
        mockMvc.perform(post("/api/pedidos/p-1/pagos").header("Authorization", "Bearer token-ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CON_NEQUI))
                .andExpect(status().isForbidden());
    }

    @Test
    void deberiaRetornar404CuandoElPedidoNoExiste() throws Exception {

        // Arrange
        when(procesarPagoDePedidoUseCase.ejecutar(anyString(), anyString(), anyString(), any()))
                .thenThrow(new NoSuchElementException());

        // Act y Assert
        mockMvc.perform(post("/api/pedidos/p-99/pagos").header("Authorization", "Bearer token-ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CON_NEQUI))
                .andExpect(status().isNotFound());
    }

    @Test
    void sinTokenDebeResponder401() throws Exception {

        // Act y Assert
        mockMvc.perform(post("/api/pedidos/p-1/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CON_NEQUI))
                .andExpect(status().isUnauthorized());

        verify(procesarPagoDePedidoUseCase, never()).ejecutar(anyString(), anyString(), anyString(), any());
    }
}
