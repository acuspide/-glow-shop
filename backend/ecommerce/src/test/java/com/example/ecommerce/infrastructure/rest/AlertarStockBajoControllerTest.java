package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.dto.response.ConsultarInventarioCompletoResponse;
import com.example.ecommerce.application.usecase.AlertarStockBajoUseCase;
import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.infrastructure.rest.mapper.ConsultarInventarioCompletoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlertarStockBajoController.class)
public class AlertarStockBajoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlertarStockBajoUseCase alertarStockBajoUseCase;

    @MockitoBean
    private ConsultarInventarioCompletoMapper inventarioMapper;

    @Test
    void debeRetornarArticulosConStockBajo() throws Exception {
        Articulo articulo = mock(Articulo.class);

        when(alertarStockBajoUseCase.ejecutar())
                .thenReturn(List.of(articulo));

        when(inventarioMapper.toResponse(List.of(articulo)))
                .thenReturn(
                        new ConsultarInventarioCompletoResponse(
                                List.of(
                                        new ConsultarInventarioCompletoResponse
                                                .ArticuloInventarioResponse(
                                                "a-1",
                                                "Shampoo",
                                                3
                                        )
                                )
                        )
                );

        mockMvc.perform(get("/api/inventario/stock-bajo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articulos").isArray())
                .andExpect(jsonPath("$.articulos.length()").value(1))
                .andExpect(jsonPath("$.articulos[0].articuloId").value("a-1"))
                .andExpect(jsonPath("$.articulos[0].nombre").value("Shampoo"))
                .andExpect(jsonPath("$.articulos[0].cantidadDisponible").value(3));

        verify(alertarStockBajoUseCase).ejecutar();
        verify(inventarioMapper).toResponse(List.of(articulo));
    }

    @Test
    void debeRetornarListaVaciaCuandoNoHayArticulosConStockBajo()
            throws Exception {

        when(alertarStockBajoUseCase.ejecutar())
                .thenReturn(List.of());

        when(inventarioMapper.toResponse(List.of()))
                .thenReturn(
                        new ConsultarInventarioCompletoResponse(List.of())
                );

        mockMvc.perform(get("/api/inventario/stock-bajo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articulos").isArray())
                .andExpect(jsonPath("$.articulos.length()").value(0));

        verify(alertarStockBajoUseCase).ejecutar();
        verify(inventarioMapper).toResponse(List.of());
    }
}
