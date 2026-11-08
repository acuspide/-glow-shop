package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.domain.exception.ParametroInvalidoException;
import com.example.ecommerce.application.dto.request.BuscarArticulosRequest;
import com.example.ecommerce.application.usecase.BuscarArticulosUseCase;
import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.repository.Pagina;
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
import static org.mockito.ArgumentMatchers.eq;
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
    void debeBuscarArticulosYRetornarLaPaginaConSusTotales() throws Exception {
        Articulo articulo = mock(Articulo.class);

        MostrarDetalleArticuloResponse response =
                new MostrarDetalleArticuloResponse(
                        "a-1",
                        "Labial rojo",
                        new BigDecimal("35000"),
                        "cat-1",
                        "Labios",
                        "m-1",
                        "Maybelline",
                        "CLARO",
                        List.of("NORMAL"),
                        10,
                        LocalDate.of(2028, 12, 31),
                        "t-1",
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
                any(CriteriosBusquedaArticulo.class), eq(0), eq(10)))
                .thenReturn(new Pagina<>(List.of(articulo), 1, 1, 0, 10));

        when(mostrarDetalleArticuloMapper.toResponse(articulo))
                .thenReturn(response);

        mockMvc.perform(get("/api/buscarArticulos")
                        .param("texto", "labial"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value("a-1"))
                .andExpect(jsonPath("$.content[0].nombre").value("Labial rojo"))
                .andExpect(jsonPath("$.content[0].precio").value(35000))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));

        verify(buscarArticulosMapper).toCriterios(
                any(BuscarArticulosRequest.class));
        verify(buscarArticulosUseCase).ejecutar(
                any(CriteriosBusquedaArticulo.class), eq(0), eq(10));
        verify(mostrarDetalleArticuloMapper).toResponse(articulo);
    }

    @Test
    void debePasarLosParametrosPageYSizeAlCasoDeUso() throws Exception {
        when(buscarArticulosMapper.toCriterios(
                any(BuscarArticulosRequest.class)))
                .thenReturn(new CriteriosBusquedaArticulo(
                        null, null, null, null,
                        null, null, null, null
                ));

        when(buscarArticulosUseCase.ejecutar(
                any(CriteriosBusquedaArticulo.class), eq(2), eq(5)))
                .thenReturn(new Pagina<>(List.of(), 11, 3, 2, 5));

        mockMvc.perform(get("/api/buscarArticulos")
                        .param("page", "2")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(11))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.number").value(2))
                .andExpect(jsonPath("$.size").value(5));

        verify(buscarArticulosUseCase).ejecutar(
                any(CriteriosBusquedaArticulo.class), eq(2), eq(5));
    }

    @Test
    void debeRetornarPaginaVaciaCuandoNoHayCoincidencias() throws Exception {
        when(buscarArticulosMapper.toCriterios(
                any(BuscarArticulosRequest.class)))
                .thenReturn(new CriteriosBusquedaArticulo(
                        "inexistente", null, null, null,
                        null, null, null, null
                ));

        when(buscarArticulosUseCase.ejecutar(
                any(CriteriosBusquedaArticulo.class), eq(0), eq(10)))
                .thenReturn(new Pagina<>(List.of(), 0, 0, 0, 10));

        mockMvc.perform(get("/api/buscarArticulos")
                        .param("texto", "inexistente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));

        verify(buscarArticulosUseCase).ejecutar(
                any(CriteriosBusquedaArticulo.class), eq(0), eq(10));
        verifyNoInteractions(mostrarDetalleArticuloMapper);
    }

    @Test
    void unaPaginaNegativaRespondeBadRequest() throws Exception {
        when(buscarArticulosUseCase.ejecutar(any(), eq(-1), eq(10)))
                .thenThrow(new ParametroInvalidoException("La página no puede ser negativa"));

        mockMvc.perform(get("/api/buscarArticulos").param("page", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("La página no puede ser negativa"));
    }
}
