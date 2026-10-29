package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.response.MostrarDetalleArticuloResponse;
import com.example.ecommerce.application.usecase.ActualizarArticuloUseCase;
import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.exception.TiendaAjenaException;
import com.example.ecommerce.infrastructure.rest.mapper.MostrarDetalleArticuloMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ActualizarArticuloController.class)
class ActualizarArticuloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ActualizarArticuloUseCase actualizarArticuloUseCase;

    @MockitoBean
    private MostrarDetalleArticuloMapper mapper;

    @MockitoBean
    private TokenService tokenService;

    private static final String CUERPO = """
            {
              "nombre": "Crema hidratante",
              "precio": 45000,
              "categoriaId": "cat-2",
              "marcaId": "m-2",
              "tono": "CLARO",
              "tiposPiel": ["NORMAL"],
              "fechaVencimiento": "2031-12-31"
            }
            """;

    @BeforeEach
    void sesionDeVera() {
        when(tokenService.extraerUsuarioId("token-vera")).thenReturn("v-1");
    }

    @Test
    void elVendedorDuenoActualizaYRecibe200() throws Exception {
        Articulo articulo = mock(Articulo.class);
        when(actualizarArticuloUseCase.ejecutar(eq("a-1"), eq("v-1"), any(), any(), eq("cat-2"), eq("m-2"),
                any(), any(), any())).thenReturn(articulo);
        when(mapper.toResponse(articulo)).thenReturn(new MostrarDetalleArticuloResponse(
                "a-1", "Crema hidratante", new BigDecimal("45000"), "cat-2", "Cuidado facial", "m-2", "Nueva marca",
                "CLARO", List.of("NORMAL"), 10, LocalDate.of(2031, 12, 31), "t-1", "Glow Natural", true));

        mockMvc.perform(put("/api/articulos/a-1").header("Authorization", "Bearer token-vera")
                        .contentType(MediaType.APPLICATION_JSON).content(CUERPO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Crema hidratante"));
    }

    @Test
    void sinTokenRetorna401() throws Exception {
        mockMvc.perform(put("/api/articulos/a-1")
                        .contentType(MediaType.APPLICATION_JSON).content(CUERPO))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(actualizarArticuloUseCase);
    }

    @Test
    void otroVendedorRecibe403() throws Exception {
        when(actualizarArticuloUseCase.ejecutar(eq("a-1"), eq("v-1"), any(), any(), any(), any(),
                any(), any(), any())).thenThrow(new TiendaAjenaException());

        mockMvc.perform(put("/api/articulos/a-1").header("Authorization", "Bearer token-vera")
                        .contentType(MediaType.APPLICATION_JSON).content(CUERPO))
                .andExpect(status().isForbidden());
    }

    @Test
    void sinNombreRetorna400() throws Exception {
        mockMvc.perform(put("/api/articulos/a-1").header("Authorization", "Bearer token-vera")
                        .contentType(MediaType.APPLICATION_JSON).content(CUERPO.replace("Crema hidratante", "")))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(actualizarArticuloUseCase);
    }
}
