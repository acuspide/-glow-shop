package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.response.TiendaResponse;
import com.example.ecommerce.application.usecase.AprobarTiendaUseCase;
import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.domain.exception.AccionSoloParaAdministradorException;
import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.example.ecommerce.domain.exception.TiendaNoPendienteException;
import com.example.ecommerce.domain.valueobject.EstadoTienda;
import com.example.ecommerce.infrastructure.rest.mapper.TiendaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminTiendaController.class)
class AdminTiendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AprobarTiendaUseCase aprobarTiendaUseCase;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private TiendaMapper mapper;

    @BeforeEach
    void sesionDelAdministrador() {
        when(tokenService.extraerUsuarioId("token-admin")).thenReturn("ad-1");
    }

    @Test
    void elAdministradorApruebaLaTiendaYRecibe200ConEstadoActiva() throws Exception {
        Tienda tienda = Tienda.reconstruir("t-1", "Glow Natural", "", "v-1", EstadoTienda.ACTIVA);
        when(aprobarTiendaUseCase.ejecutar("t-1", "ad-1")).thenReturn(tienda);
        when(mapper.toResponse(tienda)).thenReturn(new TiendaResponse("t-1", "Glow Natural", "", "v-1", "ACTIVA"));

        mockMvc.perform(put("/api/admin/tiendas/t-1/aprobar").header("Authorization", "Bearer token-admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("t-1"))
                .andExpect(jsonPath("$.estado").value("ACTIVA"));
    }

    @Test
    void sinTokenRetorna401() throws Exception {
        mockMvc.perform(put("/api/admin/tiendas/t-1/aprobar"))
                .andExpect(status().isUnauthorized());

        verify(aprobarTiendaUseCase, never()).ejecutar(any(), any());
    }

    @Test
    void siNoEsAdministradorRetorna403() throws Exception {
        when(aprobarTiendaUseCase.ejecutar("t-1", "ad-1")).thenThrow(new AccionSoloParaAdministradorException());

        mockMvc.perform(put("/api/admin/tiendas/t-1/aprobar").header("Authorization", "Bearer token-admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    void siLaTiendaNoExisteRetorna404() throws Exception {
        when(aprobarTiendaUseCase.ejecutar("t-9", "ad-1")).thenThrow(new RecursoNoEncontradoException("la tienda", "t-9"));

        mockMvc.perform(put("/api/admin/tiendas/t-9/aprobar").header("Authorization", "Bearer token-admin"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No se encontró la tienda con id: t-9"));
    }

    @Test
    void siLaTiendaYaEstabaActivaRetorna400() throws Exception {
        when(aprobarTiendaUseCase.ejecutar("t-1", "ad-1")).thenThrow(new TiendaNoPendienteException(EstadoTienda.ACTIVA));

        mockMvc.perform(put("/api/admin/tiendas/t-1/aprobar").header("Authorization", "Bearer token-admin"))
                .andExpect(status().isBadRequest());
    }
}
