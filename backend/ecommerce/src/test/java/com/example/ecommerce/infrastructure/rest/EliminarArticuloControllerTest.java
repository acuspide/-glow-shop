package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.usecase.EliminarArticuloUseCase;
import com.example.ecommerce.domain.exception.ArticuloConPedidosActivosException;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
import com.example.ecommerce.domain.exception.TiendaAjenaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EliminarArticuloController.class)
public class EliminarArticuloControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EliminarArticuloUseCase eliminarArticuloUseCase;

    @MockitoBean
    private TokenService tokenService;

    @BeforeEach
    void sesionDeVera() {
        when(tokenService.extraerUsuarioId("token-vera")).thenReturn("v-1");
    }

    @Test
    void debeEliminarArticuloYRetornar204() throws Exception {
        mockMvc.perform(delete("/api/articulos/{id}", "a-1").header("Authorization", "Bearer token-vera"))
                .andExpect(status().isNoContent());

        verify(eliminarArticuloUseCase).ejecutar("a-1", "v-1");
    }

    @Test
    void debeRetornar401SinToken() throws Exception {
        mockMvc.perform(delete("/api/articulos/{id}", "a-1"))
                .andExpect(status().isUnauthorized());

        verify(eliminarArticuloUseCase, never()).ejecutar(any(), any());
    }

    @Test
    void debeRetornar403CuandoElArticuloEsDeOtraTienda() throws Exception {
        doThrow(new TiendaAjenaException())
                .when(eliminarArticuloUseCase).ejecutar("a-1", "v-1");

        mockMvc.perform(delete("/api/articulos/{id}", "a-1").header("Authorization", "Bearer token-vera"))
                .andExpect(status().isForbidden());
    }

    @Test
    void debeRetornar404CuandoArticuloNoExiste() throws Exception {
        doThrow(new ArticuloNoEncontradoException("a-99"))
                .when(eliminarArticuloUseCase).ejecutar("a-99", "v-1");

        mockMvc.perform(delete("/api/articulos/{id}", "a-99").header("Authorization", "Bearer token-vera"))
                .andExpect(status().isNotFound());
    }

    @Test
    void debeRetornar400CuandoArticuloTienePedidosActivos() throws Exception {
        doThrow(new ArticuloConPedidosActivosException("a-1"))
                .when(eliminarArticuloUseCase).ejecutar("a-1", "v-1");

        mockMvc.perform(delete("/api/articulos/{id}", "a-1").header("Authorization", "Bearer token-vera"))
                .andExpect(status().isBadRequest());
    }
}
