package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.response.PedidoResponse;
import com.example.ecommerce.application.usecase.ConfirmarPedidoUseCase;
import com.example.ecommerce.application.usecase.ConsultarPedidoUseCase;
import com.example.ecommerce.application.usecase.ListarMisPedidosUseCase;
import com.example.ecommerce.domain.exception.PedidoAjenoException;
import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.exception.CarritoVacioException;
import com.example.ecommerce.domain.exception.PrecioDelArticuloCambioException;
import com.example.ecommerce.domain.valueobject.DetallePedido;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.infrastructure.rest.mapper.PedidoMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConfirmarPedidoUseCase confirmarPedidoUseCase;

    @MockitoBean
    private ConsultarPedidoUseCase consultarPedidoUseCase;

    @MockitoBean
    private ListarMisPedidosUseCase listarMisPedidosUseCase;

    @MockitoBean
    private PedidoMapper mapper;

    @MockitoBean
    private TokenService tokenService;

    @BeforeEach
    void sesionDeAna() {
        when(tokenService.extraerUsuarioId("token-ana")).thenReturn("u-100");
    }

    @Test
    void deberiaConfirmarElPedidoYRetornar201() throws Exception {

        // Arrange
        Pedido pedidoSimulado = Pedido.crear("p-1", "u-100", "t-1",
                List.of(new DetallePedido("a-1", 2, new Precio(new BigDecimal("20000")))));
        when(confirmarPedidoUseCase.ejecutar(eq("u-100"), any())).thenReturn(List.of(pedidoSimulado));
        when(mapper.toResponse(any()))
                .thenReturn(new PedidoResponse("p-1", "u-100", "t-1", "CONFIRMADO", List.of(),
                        new BigDecimal("40000"), new BigDecimal("40000")));

        // Act y Assert
        mockMvc.perform(post("/api/pedidos").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isCreated())                     // HTTP 201
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tiendaId").value("t-1"))
                .andExpect(jsonPath("$[0].estado").value("CONFIRMADO"))
                .andExpect(jsonPath("$[0].total").value(40000));

        verify(confirmarPedidoUseCase).ejecutar(eq("u-100"), any());
    }

    @Test
    void deberiaRetornarUnPedidoPorCadaTiendaDelCarrito() throws Exception {

        // Arrange: el carrito tenía artículos de dos tiendas
        Pedido deT1 = Pedido.crear("p-1", "u-100", "t-1",
                List.of(new DetallePedido("a-1", 2, new Precio(new BigDecimal("20000")))));
        Pedido deT2 = Pedido.crear("p-2", "u-100", "t-2",
                List.of(new DetallePedido("a-2", 1, new Precio(new BigDecimal("50000")))));
        when(confirmarPedidoUseCase.ejecutar(eq("u-100"), any())).thenReturn(List.of(deT1, deT2));
        when(mapper.toResponse(deT1)).thenReturn(new PedidoResponse("p-1", "u-100", "t-1", "CONFIRMADO",
                List.of(), new BigDecimal("40000"), new BigDecimal("40000")));
        when(mapper.toResponse(deT2)).thenReturn(new PedidoResponse("p-2", "u-100", "t-2", "CONFIRMADO",
                List.of(), new BigDecimal("50000"), new BigDecimal("50000")));

        // Act y Assert
        mockMvc.perform(post("/api/pedidos").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].tiendaId").value("t-1"))
                .andExpect(jsonPath("$[1].tiendaId").value("t-2"));
    }

    @Test
    void deberiaRetornar400CuandoElCarritoEstaVacio() throws Exception {

        // Arrange
        when(confirmarPedidoUseCase.ejecutar(anyString(), any()))
                .thenThrow(new CarritoVacioException());

        // Act y Assert
        mockMvc.perform(post("/api/pedidos").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400CuandoElPrecioDeUnArticuloCambio() throws Exception {

        // Arrange
        when(confirmarPedidoUseCase.ejecutar(anyString(), any()))
                .thenThrow(new PrecioDelArticuloCambioException("a-1"));

        // Act y Assert
        mockMvc.perform(post("/api/pedidos").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar404CuandoElClienteNoTieneCarrito() throws Exception {

        // Arrange
        when(confirmarPedidoUseCase.ejecutar(anyString(), any()))
                .thenThrow(new RecursoNoEncontradoException("el recurso", "no-existe"));

        // Act y Assert
        mockMvc.perform(post("/api/pedidos").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isNotFound());
    }

    @Test
    void sinTokenDebeResponder401() throws Exception {

        // Act y Assert
        mockMvc.perform(post("/api/pedidos"))
                .andExpect(status().isUnauthorized());

        verify(confirmarPedidoUseCase, never()).ejecutar(any(), any());
    }

    @Test
    void deberiaMostrarElPedidoDelClienteYRetornar200() throws Exception {
        Pedido pedido = Pedido.crear("p-1", "u-100", "t-1",
                List.of(new DetallePedido("a-1", 2, new Precio(new BigDecimal("20000")))));
        when(consultarPedidoUseCase.ejecutar("p-1", "u-100")).thenReturn(pedido);
        when(mapper.toResponse(pedido)).thenReturn(new PedidoResponse("p-1", "u-100", "t-1", "PENDIENTE",
                List.of(), new BigDecimal("40000"), new BigDecimal("40000")));

        mockMvc.perform(get("/api/pedidos/p-1").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("p-1"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.total").value(40000));
    }

    @Test
    void deberiaRetornar403AlConsultarUnPedidoAjeno() throws Exception {
        when(consultarPedidoUseCase.ejecutar("p-2", "u-100")).thenThrow(new PedidoAjenoException());

        mockMvc.perform(get("/api/pedidos/p-2").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deberiaRetornar404AlConsultarUnPedidoQueNoExiste() throws Exception {
        when(consultarPedidoUseCase.ejecutar("p-9", "u-100"))
                .thenThrow(new RecursoNoEncontradoException("el pedido", "p-9"));

        mockMvc.perform(get("/api/pedidos/p-9").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No se encontró el pedido con id: p-9"));
    }

    @Test
    void deberiaRetornar401AlConsultarSinToken() throws Exception {
        mockMvc.perform(get("/api/pedidos/p-1"))
                .andExpect(status().isUnauthorized());

        verify(consultarPedidoUseCase, never()).ejecutar(any(), any());
    }

    @Test
    void deberiaListarLosPedidosDelClienteYRetornar200() throws Exception {
        Pedido pedido = Pedido.crear("p-1", "u-100", "t-1",
                List.of(new DetallePedido("a-1", 2, new Precio(new BigDecimal("20000")))));
        when(listarMisPedidosUseCase.ejecutar("u-100")).thenReturn(List.of(pedido));
        when(mapper.toResponse(pedido)).thenReturn(new PedidoResponse("p-1", "u-100", "t-1", "CONFIRMADO",
                List.of(), new BigDecimal("40000"), new BigDecimal("40000")));

        mockMvc.perform(get("/api/pedidos").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("p-1"))
                .andExpect(jsonPath("$[0].estado").value("CONFIRMADO"));
    }

    @Test
    void deberiaRetornarUnaListaVaciaSiElClienteNoTienePedidos() throws Exception {
        when(listarMisPedidosUseCase.ejecutar("u-100")).thenReturn(List.of());

        mockMvc.perform(get("/api/pedidos").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void deberiaRetornar401AlListarSinToken() throws Exception {
        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isUnauthorized());

        verify(listarMisPedidosUseCase, never()).ejecutar(any());
    }
}
