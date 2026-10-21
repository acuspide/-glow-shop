package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.dto.response.ConsultarInventarioResponse;
import com.example.ecommerce.application.dto.response.MostrarDetalleArticuloResponse;
import com.example.ecommerce.application.usecase.ConsultarInventarioUseCase;
import com.example.ecommerce.application.usecase.MostrarDetalleArticuloUseCase;
import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
import com.example.ecommerce.infrastructure.rest.mapper.ConsultarInventarioMapper;
import com.example.ecommerce.infrastructure.rest.mapper.MostrarDetalleArticuloMapper;
import com.example.ecommerce.application.usecase.PublicarArticuloUseCase;
import com.example.ecommerce.infrastructure.rest.mapper.PublicarArticuloMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.example.ecommerce.application.dto.response.ListarArticulosResponse;
import java.util.Collections;
import com.example.ecommerce.application.usecase.ListarArticulosUseCase;
import com.example.ecommerce.infrastructure.rest.mapper.ListarArticulosMapper;
import com.example.ecommerce.application.dto.request.PublicarArticuloRequest;
import com.example.ecommerce.application.dto.response.PublicarArticuloResponse;
import com.example.ecommerce.domain.exception.ArticuloVencidoException;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PublicarArticuloController.class)
public class PublicarArticuloControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PublicarArticuloUseCase publicarArticuloUseCase;

    @MockitoBean
    private PublicarArticuloMapper publicarArticuloMapper;

    @MockitoBean
    private MostrarDetalleArticuloUseCase mostrarDetalleArticuloUseCase;

    @MockitoBean
    private MostrarDetalleArticuloMapper mostrarDetalleArticuloMapper;

    @MockitoBean
    private ListarArticulosUseCase listarArticulosUseCase;

    @MockitoBean
    private ListarArticulosMapper listarArticulosMapper;

    @MockitoBean
    private ConsultarInventarioUseCase consultarInventarioUseCase;

    @MockitoBean
    private ConsultarInventarioMapper consultarInventarioMapper;

    @Test
    void debeMostrarDetalleDeArticuloCuandoExiste() throws Exception {

        Articulo articulo = mock(Articulo.class);

        MostrarDetalleArticuloResponse response =
                new MostrarDetalleArticuloResponse(
                        1L,
                        "Labial",
                        new BigDecimal("20000"),
                        1L,
                        "Labios",
                        1L,
                        "Maybelline",
                        "CLARO",
                        List.of("NORMAL"),
                        10,
                        LocalDate.of(2027, 12, 31),
                        1L,
                        "Tienda Beauty",
                        true
                );

        when(mostrarDetalleArticuloUseCase.ejecutar(1L))
                .thenReturn(articulo);

        when(mostrarDetalleArticuloMapper.toResponse(articulo))
                .thenReturn(response);

        mockMvc.perform(get("/api/articulos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Labial"))
                .andExpect(jsonPath("$.precio").value(20000))
                .andExpect(jsonPath("$.marcaNombre").value("Maybelline"))
                .andExpect(jsonPath("$.cantidadDisponible").value(10))
                .andExpect(jsonPath("$.publicado").value(true));
    }
    @Test
    void debeRetornar404CuandoElArticuloNoExiste() throws Exception {

        when(mostrarDetalleArticuloUseCase.ejecutar(999L))
                .thenThrow(
                        new com.example.ecommerce.domain.exception.ArticuloNoEncontradoException(999L)
                );

        mockMvc.perform(get("/api/articulos/999"))
                .andExpect(status().isNotFound());
    }


    @Test
    void debePublicarArticuloCorrectamente() throws Exception {

        Articulo articulo = mock(Articulo.class);

        PublicarArticuloResponse response =
                new PublicarArticuloResponse(
                        1L,
                        "Labial",
                        new BigDecimal("20000"),
                        1L,
                        "Labios",
                        1L,
                        "Maybelline",
                        "CLARO",
                        List.of("NORMAL"),
                        10,
                        LocalDate.of(2027, 12, 31),
                        1L,
                        "Tienda Beauty",
                        true
                );

        when(publicarArticuloMapper.toEntity(any(PublicarArticuloRequest.class)))
                .thenReturn(articulo);

        when(publicarArticuloUseCase.ejecutar(articulo))
                .thenReturn(articulo);

        when(publicarArticuloMapper.toResponse(articulo))
                .thenReturn(response);

        when(articulo.getId()).thenReturn(1L);

        mockMvc.perform(post("/api/articulos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "id": 1,
                                "nombre": "Labial",
                                "precio": 20000,
                                "categoriaId": 1,
                                "categoriaNombre": "Labios",
                                "marcaId": 1,
                                "marcaNombre": "Maybelline",
                                "tono": "CLARO",
                                "tiposPiel": ["NORMAL"],
                                "cantidadDisponible": 10,
                                "fechaVencimiento": "2027-12-31",
                                "tiendaId": 1,
                                "tiendaNombre": "Tienda Beauty"
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Labial"))
                .andExpect(jsonPath("$.precio").value(20000))
                .andExpect(jsonPath("$.publicado").value(true));

        verify(publicarArticuloUseCase).ejecutar(articulo);
    }
    @Test
    void debeRetornar400CuandoNoSePuedePublicarArticuloVencido() throws Exception {
        Articulo articulo = mock(Articulo.class);

        when(publicarArticuloMapper.toEntity(any(PublicarArticuloRequest.class)))
                .thenReturn(articulo);

        when(publicarArticuloUseCase.ejecutar(articulo))
                .thenThrow(new ArticuloVencidoException());

        mockMvc.perform(post("/api/articulos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "id": 1,
                  "nombre": "Labial",
                  "precio": 20000,
                  "categoriaId": 1,
                  "categoriaNombre": "Labios",
                  "marcaId": 1,
                  "marcaNombre": "Maybelline",
                  "tono": "CLARO",
                  "tiposPiel": ["NORMAL"],
                  "cantidadDisponible": 10,
                  "fechaVencimiento": "2020-12-31",
                  "tiendaId": 1,
                  "tiendaNombre": "Tienda Beauty"
                }
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje")
                        .value("No se puede publicar un artículo que esté vencido."));

        verify(publicarArticuloUseCase).ejecutar(articulo);
        verify(publicarArticuloUseCase, never()).ejecutar(
                any(Long.class),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
        );
    }

    @Test
    void debeListarArticulosCorrectamente() throws Exception {

        ListarArticulosResponse articulo = new ListarArticulosResponse(
                1L,
                "Labial",
                new BigDecimal("20000"),
                1L,
                "Labios",
                1L,
                "Maybelline",
                "CLARO",
                List.of("NORMAL"),
                10,
                LocalDate.of(2027, 12, 31),
                1L,
                "Tienda Beauty"
        );

        when(listarArticulosUseCase.ejecutar())
                .thenReturn(List.of(mock(Articulo.class)));

        when(listarArticulosMapper.toResponseList(anyList()))
                .thenReturn(List.of(articulo));

        mockMvc.perform(get("/api/articulos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Labial"))
                .andExpect(jsonPath("$[0].precio").value(20000))
                .andExpect(jsonPath("$[0].marcaNombre").value("Maybelline"))
                .andExpect(jsonPath("$[0].cantidadDisponible").value(10));

        verify(listarArticulosUseCase).ejecutar();
        verify(listarArticulosMapper).toResponseList(anyList());
    }
    @Test
    void debeConsultarInventarioDeArticuloExistente() throws Exception {
        Articulo articulo = mock(Articulo.class);

        ConsultarInventarioResponse response =
                new ConsultarInventarioResponse(1L, "Labial", 10);

        when(consultarInventarioUseCase.ejecutar(1L))
                .thenReturn(articulo);

        when(consultarInventarioMapper.toResponse(articulo))
                .thenReturn(response);

        mockMvc.perform(get("/api/articulos/1/inventario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articuloId").value(1))
                .andExpect(jsonPath("$.nombre").value("Labial"))
                .andExpect(jsonPath("$.cantidadDisponible").value(10));
    }

    @Test
    void debeRetornar404CuandoNoExisteInventarioDelArticulo()
            throws Exception {

        when(consultarInventarioUseCase.ejecutar(999L))
                .thenThrow(new ArticuloNoEncontradoException(999L));

        mockMvc.perform(get("/api/articulos/999/inventario"))
                .andExpect(status().isNotFound());
    }



}
