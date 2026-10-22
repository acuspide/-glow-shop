package com.example.ecommerce.infrastructure.persistence;

import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.repository.PedidoRepository;
import com.example.ecommerce.domain.valueobject.DetallePedido;
import com.example.ecommerce.domain.valueobject.EstadoPedido;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HistorialDeComprasConPedidosTest {

    private final PedidoRepository pedidoRepository = mock(PedidoRepository.class);
    private final HistorialDeComprasConPedidos historial = new HistorialDeComprasConPedidos(pedidoRepository);

    // Se simula el pedido para no depender de cómo se construye (es del módulo de pedidos).
    private Pedido pedido(String clienteId, EstadoPedido estado, long articuloId) {
        Pedido pedido = mock(Pedido.class);
        DetallePedido detalle = mock(DetallePedido.class);
        when(detalle.getArticuloId()).thenReturn(articuloId);
        when(pedido.getClienteId()).thenReturn(clienteId);
        when(pedido.getEstado()).thenReturn(estado);
        when(pedido.getDetalles()).thenReturn(List.of(detalle));
        return pedido;
    }

    @Test
    void conUnPedidoEntregadoDelArticuloElClienteLoRecibio() {
        Pedido pedido = pedido("u-7", EstadoPedido.ENTREGADO, 10L); // se crea antes: Mockito no permite simular dentro de otro when
        when(pedidoRepository.listarTodos()).thenReturn(List.of(pedido));

        assertTrue(historial.clienteRecibioArticulo("u-7", 10L));
    }

    @Test
    void unPedidoQueNoSeHaEntregadoNoCuenta() {
        Pedido pedido = pedido("u-7", EstadoPedido.ENVIADO, 10L); // se crea antes: Mockito no permite simular dentro de otro when
        when(pedidoRepository.listarTodos()).thenReturn(List.of(pedido));

        assertFalse(historial.clienteRecibioArticulo("u-7", 10L));
    }

    @Test
    void elPedidoDebeSerDelMismoClienteYDelMismoArticulo() {
        Pedido pedido = pedido("u-7", EstadoPedido.ENTREGADO, 10L); // se crea antes: Mockito no permite simular dentro de otro when
        when(pedidoRepository.listarTodos()).thenReturn(List.of(pedido));

        assertFalse(historial.clienteRecibioArticulo("u-8", 10L));
        assertFalse(historial.clienteRecibioArticulo("u-7", 11L));
    }
}
