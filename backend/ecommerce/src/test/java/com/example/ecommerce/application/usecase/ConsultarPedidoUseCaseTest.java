package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.exception.PedidoAjenoException;
import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.example.ecommerce.domain.valueobject.DetallePedido;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.infrastructure.persistence.PedidoRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConsultarPedidoUseCaseTest {

    private final PedidoRepositoryEnMemoria pedidos = new PedidoRepositoryEnMemoria();
    private final ConsultarPedidoUseCase useCase = new ConsultarPedidoUseCase(pedidos);
    private Pedido pedidoDeAna;

    @BeforeEach
    void prepararPedido() {
        pedidoDeAna = Pedido.crear("p-1", "u-1", "t-1",
                List.of(new DetallePedido("a-1", 2, new Precio(new BigDecimal("20000")))));
        pedidos.guardar(pedidoDeAna);
    }

    @Test
    void elClienteConsultaSuPropioPedido() {
        Pedido pedido = useCase.ejecutar("p-1", "u-1");

        assertSame(pedidoDeAna, pedido);
        assertEquals(new BigDecimal("40000"), pedido.calcularTotal());
    }

    @Test
    void otroClienteNoPuedeVerElPedido() {
        assertThrows(PedidoAjenoException.class, () -> useCase.ejecutar("p-1", "u-2"));
    }

    @Test
    void unPedidoQueNoExisteRetornaNoEncontrado() {
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,
                () -> useCase.ejecutar("p-9", "u-1"));

        assertEquals("No se encontró el pedido con id: p-9", error.getMessage());
    }
}
