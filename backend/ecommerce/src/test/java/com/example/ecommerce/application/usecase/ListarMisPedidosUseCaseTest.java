package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.valueobject.DetallePedido;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.infrastructure.persistence.PedidoRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListarMisPedidosUseCaseTest {

    private final PedidoRepositoryEnMemoria pedidos = new PedidoRepositoryEnMemoria();
    private final ListarMisPedidosUseCase useCase = new ListarMisPedidosUseCase(pedidos);

    private Pedido pedido(String id, String clienteId) {
        return Pedido.crear(id, clienteId,
                List.of(new DetallePedido("a-1", 1, new Precio(new BigDecimal("20000")))));
    }

    @Test
    void devuelveSoloLosPedidosDelCliente() {
        pedidos.guardar(pedido("p-1", "u-1"));
        pedidos.guardar(pedido("p-2", "u-2"));
        pedidos.guardar(pedido("p-3", "u-1"));

        List<Pedido> deAna = useCase.ejecutar("u-1");

        assertEquals(2, deAna.size());
        assertTrue(deAna.stream().allMatch(p -> p.getClienteId().equals("u-1")));
    }

    @Test
    void unClienteSinComprasRecibeUnaListaVacia() {
        pedidos.guardar(pedido("p-1", "u-1"));

        assertTrue(useCase.ejecutar("u-9").isEmpty());
    }
}
