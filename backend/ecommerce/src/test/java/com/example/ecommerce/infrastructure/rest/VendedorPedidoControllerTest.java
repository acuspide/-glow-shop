package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.response.PedidoResponse;
import com.example.ecommerce.application.usecase.ActualizarEstadoPedidoUseCase;
import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.exception.PagoNoRegistradoException;
import com.example.ecommerce.domain.exception.TiendaAjenaException;
import com.example.ecommerce.domain.valueobject.DetallePedido;
import com.example.ecommerce.domain.valueobject.EstadoPedido;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.infrastructure.rest.mapper.PedidoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VendedorPedidoController.class)
class VendedorPedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ActualizarEstadoPedidoUseCase actualizarEstadoPedidoUseCase;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private PedidoMapper mapper;

    @BeforeEach
    void sesionDeVera() {
        when(tokenService.extraerUsuarioId("token-vera")).thenReturn("v-1");
    }

    private String cuerpo(String estado) {
        return "{\"estado\": \"" + estado + "\"}";
    }

    @Test
    void elVendedorEnviaElPedidoYRecibe200() throws Exception {
        Pedido pedido = Pedido.crear("p-1", "u-1", "t-1",
                List.of(new DetallePedido("a-1", 1, new Precio(new BigDecimal("20000")))));
        when(actualizarEstadoPedidoUseCase.ejecutar("p-1", "v-1", EstadoPedido.ENVIADO)).thenReturn(pedido);
        when(mapper.toResponse(pedido)).thenReturn(new PedidoResponse("p-1", "u-1", "t-1", "ENVIADO",
                List.of(), new BigDecimal("20000"), new BigDecimal("20000")));

        mockMvc.perform(put("/api/vendedor/pedidos/p-1/estado").header("Authorization", "Bearer token-vera")
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo("ENVIADO")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ENVIADO"));
    }

    @Test
    void sinTokenRetorna401() throws Exception {
        mockMvc.perform(put("/api/vendedor/pedidos/p-1/estado")
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo("ENVIADO")))
                .andExpect(status().isUnauthorized());

        verify(actualizarEstadoPedidoUseCase, never()).ejecutar(any(), any(), any());
    }

    @Test
    void siElPedidoEsDeOtraTiendaRetorna403() throws Exception {
        when(actualizarEstadoPedidoUseCase.ejecutar("p-1", "v-1", EstadoPedido.ENVIADO))
                .thenThrow(new TiendaAjenaException());

        mockMvc.perform(put("/api/vendedor/pedidos/p-1/estado").header("Authorization", "Bearer token-vera")
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo("ENVIADO")))
                .andExpect(status().isForbidden());
    }

    @Test
    void siElPedidoNoEstaPagadoRetorna400() throws Exception {
        when(actualizarEstadoPedidoUseCase.ejecutar("p-1", "v-1", EstadoPedido.EN_PREPARACION))
                .thenThrow(new PagoNoRegistradoException());

        mockMvc.perform(put("/api/vendedor/pedidos/p-1/estado").header("Authorization", "Bearer token-vera")
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo("EN_PREPARACION")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sinEstadoRetorna400() throws Exception {
        mockMvc.perform(put("/api/vendedor/pedidos/p-1/estado").header("Authorization", "Bearer token-vera")
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }
}
