package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.response.ActualizarInventarioResponse;
import com.example.ecommerce.application.usecase.ActualizarInventarioUseCase;
import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
import com.example.ecommerce.domain.exception.TiendaAjenaException;
import com.example.ecommerce.infrastructure.rest.mapper.ActualizarInventarioMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ActualizarInventarioController.class)
class ActualizarInventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ActualizarInventarioUseCase actualizarInventarioUseCase;

    @MockitoBean
    private ActualizarInventarioMapper mapper;

    @MockitoBean
    private TokenService tokenService;

    @BeforeEach
    void sesionDeVera() {
        when(tokenService.extraerUsuarioId("token-vera")).thenReturn("v-1");
    }

    private String cuerpo(int cantidad) {
        return "{\"cantidad\": " + cantidad + "}";
    }

    @Test
    void elVendedorDuenoAumentaElInventarioYRecibe200() throws Exception {
        Articulo articulo = mock(Articulo.class);
        when(actualizarInventarioUseCase.aumentar("a-1", "v-1", 5)).thenReturn(articulo);
        when(mapper.toResponse(articulo)).thenReturn(new ActualizarInventarioResponse("a-1", 15, true));

        mockMvc.perform(post("/api/articulos/a-1/inventario/aumentar").header("Authorization", "Bearer token-vera")
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo(5)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadDisponible").value(15));
    }

    @Test
    void elVendedorDuenoDisminuyeElInventarioYRecibe200() throws Exception {
        Articulo articulo = mock(Articulo.class);
        when(actualizarInventarioUseCase.disminuir("a-1", "v-1", 3)).thenReturn(articulo);
        when(mapper.toResponse(articulo)).thenReturn(new ActualizarInventarioResponse("a-1", 7, true));

        mockMvc.perform(post("/api/articulos/a-1/inventario/disminuir").header("Authorization", "Bearer token-vera")
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo(3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadDisponible").value(7));
    }

    @Test
    void sinTokenRetorna401() throws Exception {
        mockMvc.perform(post("/api/articulos/a-1/inventario/aumentar")
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo(5)))
                .andExpect(status().isUnauthorized());

        verify(actualizarInventarioUseCase, never()).aumentar(any(), any(), anyInt());
    }

    @Test
    void otroVendedorRecibe403() throws Exception {
        when(actualizarInventarioUseCase.aumentar("a-1", "v-1", 5)).thenThrow(new TiendaAjenaException());

        mockMvc.perform(post("/api/articulos/a-1/inventario/aumentar").header("Authorization", "Bearer token-vera")
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo(5)))
                .andExpect(status().isForbidden());
    }

    @Test
    void articuloQueNoExisteRetorna404() throws Exception {
        when(actualizarInventarioUseCase.disminuir("a-99", "v-1", 1)).thenThrow(new ArticuloNoEncontradoException("a-99"));

        mockMvc.perform(post("/api/articulos/a-99/inventario/disminuir").header("Authorization", "Bearer token-vera")
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo(1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void cantidadCeroRetorna400() throws Exception {
        mockMvc.perform(post("/api/articulos/a-1/inventario/aumentar").header("Authorization", "Bearer token-vera")
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo(0)))
                .andExpect(status().isBadRequest());
    }
}
