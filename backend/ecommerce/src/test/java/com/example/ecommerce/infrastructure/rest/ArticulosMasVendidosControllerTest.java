package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.domain.exception.ParametroInvalidoException;
import com.example.ecommerce.application.usecase.ConsultarArticulosMasVendidosUseCase;
import com.example.ecommerce.domain.repository.ArticuloVentas;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArticulosMasVendidosController.class)
class ArticulosMasVendidosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultarArticulosMasVendidosUseCase consultarArticulosMasVendidosUseCase;

    @Test
    void devuelveElRankingConElLimiteIndicado() throws Exception {
        when(consultarArticulosMasVendidosUseCase.ejecutar(2)).thenReturn(List.of(
                new ArticuloVentas("a-1", "Labial mate Ruby", 3L),
                new ArticuloVentas("a-2", "Protector solar FPS 50", 2L)));

        mockMvc.perform(get("/api/reportes/articulos-mas-vendidos").param("limite", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].articuloId").value("a-1"))
                .andExpect(jsonPath("$[0].nombre").value("Labial mate Ruby"))
                .andExpect(jsonPath("$[0].unidadesVendidas").value(3))
                .andExpect(jsonPath("$[1].unidadesVendidas").value(2));

        verify(consultarArticulosMasVendidosUseCase).ejecutar(2);
    }

    @Test
    void usaUnLimitePorDefectoCuandoNoSeIndica() throws Exception {
        when(consultarArticulosMasVendidosUseCase.ejecutar(5)).thenReturn(List.of());

        mockMvc.perform(get("/api/reportes/articulos-mas-vendidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(consultarArticulosMasVendidosUseCase).ejecutar(5);
    }

    @Test
    void unLimiteFueraDeRangoRespondeBadRequest() throws Exception {
        when(consultarArticulosMasVendidosUseCase.ejecutar(0))
                .thenThrow(new ParametroInvalidoException("El límite debe estar entre 1 y 20"));

        mockMvc.perform(get("/api/reportes/articulos-mas-vendidos").param("limite", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El límite debe estar entre 1 y 20"));
    }
}
