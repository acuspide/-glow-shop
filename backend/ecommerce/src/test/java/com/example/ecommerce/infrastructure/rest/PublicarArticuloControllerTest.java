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
import static org.mockito.ArgumentMatchers.anyString;
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
                        "a-1",
                        "Labial",
                        new BigDecimal("20000"),
                        "cat-1",
                        "Labios",
                        "m-1",
                        "Maybelline",
                        "CLARO",
                        List.of("NORMAL"),
                        10,
                        LocalDate.of(2027, 12, 31),
                        "t-1",
                        "Tienda Beauty",
                        true
                );

        when(mostrarDetalleArticuloUseCase.ejecutar("a-1"))
                .thenReturn(articulo);

        when(mostrarDetalleArticuloMapper.toResponse(articulo))
                .thenReturn(response);

        mockMvc.perform(get("/api/articulos/a-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("a-1"))
                .andExpect(jsonPath("$.nombre").value("Labial"))
                .andExpect(jsonPath("$.precio").value(20000))
                .andExpect(jsonPath("$.marcaNombre").value("Maybelline"))
                .andExpect(jsonPath("$.cantidadDisponible").value(10))
                .andExpect(jsonPath("$.publicado").value(true));
    }
    @Test
    void debeRetornar404CuandoElArticuloNoExiste() throws Exception {

        when(mostrarDetalleArticuloUseCase.ejecutar("a-999"))
                .thenThrow(
                        new com.example.ecommerce.domain.exception.ArticuloNoEncontradoException("a-999")
                );

        mockMvc.perform(get("/api/articulos/a-999"))
                .andExpect(status().isNotFound());
    }


    @Test
    void debePublicarArticuloCorrectamente() throws Exception {

        Articulo articulo = mock(Articulo.class);

        PublicarArticuloResponse response =
                new PublicarArticuloResponse(
                        "a-1",
                        "Labial",
                        new BigDecimal("20000"),
                        "cat-1",
                        "Labios",
                        "m-1",
                        "Maybelline",
                        "CLARO",
                        List.of("NORMAL"),
                        10,
                        LocalDate.of(2027, 12, 31),
                        "t-1",
                        "Tienda Beauty",
                        true
                );

        when(publicarArticuloMapper.toEntity(anyString(), any(PublicarArticuloRequest.class)))
                .thenReturn(articulo);

        when(publicarArticuloUseCase.ejecutar(articulo))
                .thenReturn(articulo);

        when(publicarArticuloMapper.toResponse(articulo))
                .thenReturn(response);

        when(articulo.getId()).thenReturn("a-1");

        mockMvc.perform(post("/api/articulos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "nombre": "Labial",
                                "precio": 20000,
                                "categoriaId": "cat-1",
                                "categoriaNombre": "Labios",
                                "marcaId": "m-1",
                                "marcaNombre": "Maybelline",
                                "tono": "CLARO",
                                "tiposPiel": ["NORMAL"],
                                "cantidadDisponible": 10,
                                "fechaVencimiento": "2027-12-31",
                                "tiendaId": "t-1",
                                "tiendaNombre": "Tienda Beauty"
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("a-1"))
                .andExpect(jsonPath("$.nombre").value("Labial"))
                .andExpect(jsonPath("$.precio").value(20000))
                .andExpect(jsonPath("$.publicado").value(true));

        verify(publicarArticuloUseCase).ejecutar(articulo);
    }
    @Test
    void debeRetornar400CuandoNoSePuedePublicarArticuloVencido() throws Exception {
        Articulo articulo = mock(Articulo.class);

        when(publicarArticuloMapper.toEntity(anyString(), any(PublicarArticuloRequest.class)))
                .thenReturn(articulo);

        when(publicarArticuloUseCase.ejecutar(articulo))
                .thenThrow(new ArticuloVencidoException());

        mockMvc.perform(post("/api/articulos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "nombre": "Labial",
                  "precio": 20000,
                  "categoriaId": "cat-1",
                  "categoriaNombre": "Labios",
                  "marcaId": "m-1",
                  "marcaNombre": "Maybelline",
                  "tono": "CLARO",
                  "tiposPiel": ["NORMAL"],
                  "cantidadDisponible": 10,
                  "fechaVencimiento": "2020-12-31",
                  "tiendaId": "t-1",
                  "tiendaNombre": "Tienda Beauty"
                }
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje")
                        .value("No se puede publicar un artículo que esté vencido."));

        verify(publicarArticuloUseCase).ejecutar(articulo);
        verify(publicarArticuloUseCase, never()).ejecutar(
                any(String.class),
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
                "a-1",
                "Labial",
                new BigDecimal("20000"),
                "cat-1",
                "Labios",
                "m-1",
                "Maybelline",
                "CLARO",
                List.of("NORMAL"),
                10,
                LocalDate.of(2027, 12, 31),
                "t-1",
                "Tienda Beauty"
        );

        when(listarArticulosUseCase.ejecutar())
                .thenReturn(List.of(mock(Articulo.class)));

        when(listarArticulosMapper.toResponseList(anyList()))
                .thenReturn(List.of(articulo));

        mockMvc.perform(get("/api/articulos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("a-1"))
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
                new ConsultarInventarioResponse("a-1", "Labial", 10);

        when(consultarInventarioUseCase.ejecutar("a-1"))
                .thenReturn(articulo);

        when(consultarInventarioMapper.toResponse(articulo))
                .thenReturn(response);

        mockMvc.perform(get("/api/articulos/a-1/inventario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articuloId").value("a-1"))
                .andExpect(jsonPath("$.nombre").value("Labial"))
                .andExpect(jsonPath("$.cantidadDisponible").value(10));
    }

    @Test
    void debeRetornar404CuandoNoExisteInventarioDelArticulo()
            throws Exception {

        when(consultarInventarioUseCase.ejecutar("a-999"))
                .thenThrow(new ArticuloNoEncontradoException("a-999"));

        mockMvc.perform(get("/api/articulos/a-999/inventario"))
                .andExpect(status().isNotFound());
    }



}
