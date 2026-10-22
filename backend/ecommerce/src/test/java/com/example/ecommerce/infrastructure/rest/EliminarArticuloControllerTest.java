package com.example.ecommerce.infrastructure.rest;
import com.example.ecommerce.application.usecase.EliminarArticuloUseCase;
import com.example.ecommerce.domain.exception.ArticuloConPedidosActivosException;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EliminarArticuloController.class)
public class EliminarArticuloControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EliminarArticuloUseCase eliminarArticuloUseCase;

    @Test
    void debeEliminarArticuloYRetornar204() throws Exception {
        mockMvc.perform(delete("/api/articulos/{id}", "a-1"))
                .andExpect(status().isNoContent());

        verify(eliminarArticuloUseCase).ejecutar("a-1");
    }

    @Test
    void debeRetornar404CuandoArticuloNoExiste() throws Exception {
        doThrow(new ArticuloNoEncontradoException("a-99"))
                .when(eliminarArticuloUseCase).ejecutar("a-99");

        mockMvc.perform(delete("/api/articulos/{id}", "a-99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void debeRetornar400CuandoArticuloTienePedidosActivos() throws Exception {
        doThrow(new ArticuloConPedidosActivosException("a-1"))
                .when(eliminarArticuloUseCase).ejecutar("a-1");

        mockMvc.perform(delete("/api/articulos/{id}", "a-1"))
                .andExpect(status().isBadRequest());
    }
}
