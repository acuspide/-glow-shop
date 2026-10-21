package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.dto.request.BuscarArticulosRequest;
import com.example.ecommerce.application.usecase.BuscarArticulosUseCase;
import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.valueobject.CriteriosBusquedaArticulo;
import com.example.ecommerce.infrastructure.rest.mapper.BuscarArticulosMapper;
import com.example.ecommerce.infrastructure.rest.mapper.MostrarDetalleArticuloMapper;
import com.example.ecommerce.application.dto.response.MostrarDetalleArticuloResponse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BuscarArticulosController.class)
public class BuscarArticulosControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BuscarArticulosUseCase buscarArticulosUseCase;

    @MockitoBean
    private BuscarArticulosMapper buscarArticulosMapper;

    @MockitoBean
    private MostrarDetalleArticuloMapper mostrarDetalleArticuloMapper;

    @Test
    void debeBuscarArticulosYRetornar200() throws Exception {
        Articulo articulo = mock(Articulo.class);

        MostrarDetalleArticuloResponse response =
                new MostrarDetalleArticuloResponse(
                        1L,
                        "Labial rojo",
                        new BigDecimal("35000"),
                        1L,
                        "Labios",
                        1L,
                        "Maybelline",
                        "CLARO",
                        List.of("NORMAL"),
                        10,
                        LocalDate.of(2028, 12, 31),
                        1L,
                        "Tienda Beauty",
                        true
                );

        when(buscarArticulosMapper.toCriterios(
                any(BuscarArticulosRequest.class)))
                .thenReturn(new CriteriosBusquedaArticulo(
                        "labial", null, null, null,
                        null, null, null, null
                ));

        when(buscarArticulosUseCase.ejecutar(
                any(CriteriosBusquedaArticulo.class)))
                .thenReturn(List.of(articulo));

        when(mostrarDetalleArticuloMapper.toResponse(articulo))
                .thenReturn(response);

        mockMvc.perform(get("/api/buscarArticulos")
                        .param("texto", "labial"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Labial rojo"))
                .andExpect(jsonPath("$[0].precio").value(35000));

        verify(buscarArticulosMapper).toCriterios(
                any(BuscarArticulosRequest.class));
        verify(buscarArticulosUseCase).ejecutar(
                any(CriteriosBusquedaArticulo.class));
        verify(mostrarDetalleArticuloMapper).toResponse(articulo);
    }

    @Test
    void debeRetornarListaVaciaCuandoNoHayCoincidencias() throws Exception {
        when(buscarArticulosMapper.toCriterios(
                any(BuscarArticulosRequest.class)))
                .thenReturn(new CriteriosBusquedaArticulo(
                        "inexistente", null, null, null,
                        null, null, null, null
                ));

        when(buscarArticulosUseCase.ejecutar(
                any(CriteriosBusquedaArticulo.class)))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/buscarArticulos")
                        .param("texto", "inexistente"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(buscarArticulosUseCase).ejecutar(
                any(CriteriosBusquedaArticulo.class));
        verifyNoInteractions(mostrarDetalleArticuloMapper);
    }
}
