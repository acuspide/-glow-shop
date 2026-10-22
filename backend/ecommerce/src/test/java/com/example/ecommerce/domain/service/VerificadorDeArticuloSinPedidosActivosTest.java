package com.example.ecommerce.domain.service;

import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.exception.ArticuloConPedidosActivosException;
import com.example.ecommerce.domain.repository.PedidoRepository;
import com.example.ecommerce.domain.valueobject.DetallePedido;
import com.example.ecommerce.domain.valueobject.EstadoPedido;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VerificadorDeArticuloSinPedidosActivosTest {

    private final PedidoRepository pedidoRepository = mock(PedidoRepository.class);
    private final VerificadorDeArticuloSinPedidosActivos verificador =
            new VerificadorDeArticuloSinPedidosActivos(pedidoRepository);

    // Se simula el pedido para no depender de cómo se construye (es del módulo de pedidos).
    private Pedido pedido(EstadoPedido estado, long articuloId) {
        Pedido pedido = mock(Pedido.class);
        DetallePedido detalle = mock(DetallePedido.class);
        when(detalle.getArticuloId()).thenReturn(articuloId);
        when(pedido.getEstado()).thenReturn(estado);
        when(pedido.getDetalles()).thenReturn(List.of(detalle));
        return pedido;
    }

    @Test
    void unArticuloSinPedidosPuedeEliminarse() {
        when(pedidoRepository.listarTodos()).thenReturn(List.of());

        assertDoesNotThrow(() -> verificador.verificarQuePuedeEliminarse(10L));
    }

    @Test
    void unArticuloEnUnPedidoActivoNoPuedeEliminarse() {
        Pedido confirmado = pedido(EstadoPedido.CONFIRMADO, 10L);
        when(pedidoRepository.listarTodos()).thenReturn(List.of(confirmado));

        assertThrows(ArticuloConPedidosActivosException.class, () -> verificador.verificarQuePuedeEliminarse(10L));
    }

    @Test
    void losPedidosEntregadosOCanceladosNoImpidenEliminar() {
        Pedido entregado = pedido(EstadoPedido.ENTREGADO, 10L);
        Pedido cancelado = pedido(EstadoPedido.CANCELADO, 10L);
        when(pedidoRepository.listarTodos()).thenReturn(List.of(entregado, cancelado));

        assertDoesNotThrow(() -> verificador.verificarQuePuedeEliminarse(10L));
    }

    @Test
    void unPedidoActivoDeOtroArticuloNoImpideEliminar() {
        Pedido deOtroArticulo = pedido(EstadoPedido.CONFIRMADO, 11L);
        when(pedidoRepository.listarTodos()).thenReturn(List.of(deOtroArticulo));

        assertDoesNotThrow(() -> verificador.verificarQuePuedeEliminarse(10L));
    }
}
