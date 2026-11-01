package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.usecase.ActualizarEstadoPedidoUseCase;
import com.example.ecommerce.application.usecase.ListarPedidosDeMiTiendaUseCase;
import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.domain.repository.PedidoRepository;
import com.example.ecommerce.domain.repository.TiendaRepository;
import com.example.ecommerce.domain.valueobject.DetallePedido;
import com.example.ecommerce.domain.valueobject.EstadoPedido;
import com.example.ecommerce.domain.valueobject.EstadoTienda;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.infrastructure.rest.mapper.PedidoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Casos de error del controller de pedidos del vendedor. Aquí el caso de uso es el REAL
 * (@Import) y lo que se simula son los repositorios: así podemos comprobar con
 * verify(..., never()) que, cuando algo falla, el pedido nunca se guarda.
 */
@WebMvcTest(VendedorPedidoController.class)
@Import(ActualizarEstadoPedidoUseCase.class)
class VendedorPedidoControllerErroresTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoRepository pedidoRepository;

    @MockitoBean
    private TiendaRepository tiendaRepository;

    @MockitoBean
    private ListarPedidosDeMiTiendaUseCase listarPedidosDeMiTiendaUseCase;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private PedidoMapper mapper;

    @BeforeEach
    void sesionDeVera() {
        when(tokenService.extraerUsuarioId("token-vera")).thenReturn("v-1");
    }

    private Pedido pedidoConfirmadoSinPago() {
        return Pedido.reconstruir("p-1", "u-1", "t-1",
                List.of(new DetallePedido("a-1", 1, new Precio(new BigDecimal("20000")))),
                EstadoPedido.CONFIRMADO, null, new Precio(BigDecimal.ZERO), new Precio(BigDecimal.ZERO),
                new Precio(BigDecimal.ZERO), null);
    }

    @Test
    void deberiaResponder404CuandoElPedidoNoExisteYNoGuardarNada() throws Exception {
        // Arrange
        when(pedidoRepository.obtenerPorId("no-existe")).thenReturn(Optional.empty());

        // Act
        mockMvc.perform(put("/api/vendedor/pedidos/no-existe/estado")
                        .header("Authorization", "Bearer token-vera")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\": \"EN_PREPARACION\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No se encontró el pedido con id: no-existe"));

        // Assert
        verify(pedidoRepository, never()).guardar(any());
    }

    @Test
    void deberiaResponder422CuandoSePreparaUnPedidoSinPagoYNoGuardarNada() throws Exception {
        // Arrange: el pedido es de la tienda de Vera pero todavía no está pagado
        when(pedidoRepository.obtenerPorId("p-1")).thenReturn(Optional.of(pedidoConfirmadoSinPago()));
        when(tiendaRepository.obtenerPorId("t-1"))
                .thenReturn(Optional.of(Tienda.reconstruir("t-1", "Glow Natural", "", "v-1", EstadoTienda.ACTIVA)));

        // Act
        mockMvc.perform(put("/api/vendedor/pedidos/p-1/estado")
                        .header("Authorization", "Bearer token-vera")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\": \"EN_PREPARACION\"}"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.mensaje").value("El pedido no puede prepararse sin un pago confirmado registrado."));

        // Assert
        verify(pedidoRepository, never()).guardar(any());
    }

    @Test
    void deberiaResponder400SiElEstadoNoVieneYNoTocarElRepositorio() throws Exception {
        // Act
        mockMvc.perform(put("/api/vendedor/pedidos/p-1/estado")
                        .header("Authorization", "Bearer token-vera")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        // Assert
        verify(pedidoRepository, never()).obtenerPorId(any());
        verify(pedidoRepository, never()).guardar(any());
    }
}
