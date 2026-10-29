package com.example.ecommerce.infrastructure.persistence;

import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.valueobject.DetallePedido;
import com.example.ecommerce.domain.valueobject.Precio;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PedidoRepositoryEnMemoriaTest {

    private Pedido pedido(String id, String clienteId) {
        return Pedido.crear(id, clienteId, "t-1",
                List.of(new DetallePedido("a-1", 1, new Precio(new BigDecimal("20000")))));
    }

    @Test
    void deberiaEncontrarSoloLosPedidosDeLaClientaIndicada() {

        // Arrange
        PedidoRepositoryEnMemoria repository = new PedidoRepositoryEnMemoria();
        repository.guardar(pedido("p-1", "u-100"));
        repository.guardar(pedido("p-2", "u-100"));
        repository.guardar(pedido("p-3", "u-200"));

        // Act
        List<Pedido> pedidosDeAna = repository.obtenerPorClienteId("u-100");

        // Assert
        assertEquals(2, pedidosDeAna.size());
        assertTrue(pedidosDeAna.stream().allMatch(p -> p.getClienteId().equals("u-100")));
    }

    @Test
    void deberiaRetornarUnaListaVaciaSiLaClientaNoTienePedidos() {

        // Arrange
        PedidoRepositoryEnMemoria repository = new PedidoRepositoryEnMemoria();

        // Act y Assert
        assertTrue(repository.obtenerPorClienteId("u-100").isEmpty());
    }
}
