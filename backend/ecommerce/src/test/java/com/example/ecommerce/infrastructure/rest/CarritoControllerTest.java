package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.dto.response.CarritoResponse;
import com.example.ecommerce.application.usecase.AgregarAlCarritoUseCase;
import com.example.ecommerce.domain.entity.Carrito;
import com.example.ecommerce.domain.exception.ArticuloAgotadoException;
import com.example.ecommerce.infrastructure.rest.mapper.CarritoMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarritoController.class)
class CarritoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AgregarAlCarritoUseCase agregarAlCarritoUseCase;

    @MockitoBean
    private CarritoMapper mapper;

    @Test
    void deberiaAgregarArticuloCuandoDatosValidos() throws Exception {

        // Arrange
        String requestJson = """
            {
                "articuloId": 1,
                "cantidad": 2
            }
            """;

        Carrito carritoSimulado = Carrito.crear(1L, 100L);
        when(agregarAlCarritoUseCase.ejecutar(anyLong(), anyLong(), anyInt()))
                .thenReturn(carritoSimulado);
        when(mapper.toResponse(any()))
                .thenReturn(new CarritoResponse(1L, 100L, List.of(), new BigDecimal("40000")));

        // Act y Assert
        mockMvc.perform(post("/api/carritos/100/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value(100))
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
        mockMvc.perform(post("/api/carritos/100/items")
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

        when(agregarAlCarritoUseCase.ejecutar(anyLong(), anyLong(), anyInt()))
                .thenThrow(new ArticuloAgotadoException());

        // Act y Assert
        mockMvc.perform(post("/api/carritos/100/items")
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

        when(agregarAlCarritoUseCase.ejecutar(anyLong(), anyLong(), anyInt()))
                .thenThrow(new NoSuchElementException());

        // Act y Assert
        mockMvc.perform(post("/api/carritos/100/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound());
    }
}