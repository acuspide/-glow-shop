package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.response.TiendaResponse;
import com.example.ecommerce.application.usecase.RegistrarTiendaUseCase;
import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.domain.exception.AccionSoloParaVendedorException;
import com.example.ecommerce.domain.exception.TokenInvalidoException;
import com.example.ecommerce.domain.exception.VendedorYaTieneTiendaException;
import com.example.ecommerce.domain.valueobject.EstadoTienda;
import com.example.ecommerce.infrastructure.rest.mapper.TiendaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TiendaController.class)
class TiendaControllerTest {

    private static final String CUERPO = "{\"nombre\": \"Glow Natural\", \"descripcion\": \"Cosmética vegana\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrarTiendaUseCase registrarTiendaUseCase;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private TiendaMapper mapper;

    @BeforeEach
    void sesionDeVera() {
        when(tokenService.extraerUsuarioId("token-vera")).thenReturn("v-1");
    }

    @Test
    void elVendedorRegistraSuTiendaYRecibe201ConEstadoPendiente() throws Exception {
        Tienda tienda = Tienda.reconstruir("t-1", "Glow Natural", "Cosmética vegana", "v-1", EstadoTienda.PENDIENTE);
        when(registrarTiendaUseCase.ejecutar(anyString(), eq("v-1"), eq("Glow Natural"), eq("Cosmética vegana")))
                .thenReturn(tienda);
        when(mapper.toResponse(tienda))
                .thenReturn(new TiendaResponse("t-1", "Glow Natural", "Cosmética vegana", "v-1", "PENDIENTE"));

        mockMvc.perform(post("/api/tiendas").header("Authorization", "Bearer token-vera")
                        .contentType(MediaType.APPLICATION_JSON).content(CUERPO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("t-1"))
                .andExpect(jsonPath("$.vendedorId").value("v-1"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void sinTokenRetorna401() throws Exception {
        mockMvc.perform(post("/api/tiendas").contentType(MediaType.APPLICATION_JSON).content(CUERPO))
                .andExpect(status().isUnauthorized());

        verify(registrarTiendaUseCase, never()).ejecutar(any(), any(), any(), any());
    }

    @Test
    void conTokenInvalidoRetorna401() throws Exception {
        when(tokenService.extraerUsuarioId("token-falso")).thenThrow(new TokenInvalidoException());

        mockMvc.perform(post("/api/tiendas").header("Authorization", "Bearer token-falso")
                        .contentType(MediaType.APPLICATION_JSON).content(CUERPO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void sinNombreRetorna400() throws Exception {
        mockMvc.perform(post("/api/tiendas").header("Authorization", "Bearer token-vera")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"nombre\": \"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void siNoEsVendedorRetorna403() throws Exception {
        when(registrarTiendaUseCase.ejecutar(anyString(), eq("v-1"), any(), any()))
                .thenThrow(new AccionSoloParaVendedorException());

        mockMvc.perform(post("/api/tiendas").header("Authorization", "Bearer token-vera")
                        .contentType(MediaType.APPLICATION_JSON).content(CUERPO))
                .andExpect(status().isForbidden());
    }

    @Test
    void siElVendedorYaTieneTiendaRetorna409() throws Exception {
        when(registrarTiendaUseCase.ejecutar(anyString(), eq("v-1"), any(), any()))
                .thenThrow(new VendedorYaTieneTiendaException());

        mockMvc.perform(post("/api/tiendas").header("Authorization", "Bearer token-vera")
                        .contentType(MediaType.APPLICATION_JSON).content(CUERPO))
                .andExpect(status().isConflict());
    }
}
