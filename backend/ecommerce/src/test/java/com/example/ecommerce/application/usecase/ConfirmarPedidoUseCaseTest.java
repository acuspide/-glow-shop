package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.exception.ReglaDominioException;
import com.example.ecommerce.domain.repository.PedidoRepository;
import com.example.ecommerce.domain.valueobject.DetallePedido;
import com.example.ecommerce.domain.valueobject.EstadoPedido;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.infrastructure.persistence.PedidoRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConfirmarPedidoUseCaseTest {

    @Test
    void debeConfirmarUnPedidoConAlMenosUnDetalle() {

        // Arrange
        PedidoRepository repository = new PedidoRepositoryEnMemoria();
        ConfirmarPedidoUseCase useCase = new ConfirmarPedidoUseCase(repository);
        Precio precio = new Precio(new BigDecimal("20000"));
        List<DetallePedido> detalles = List.of(new DetallePedido(1L, 2, precio));

        // Act
        Pedido pedido = useCase.ejecutar("p-1", "u-100", detalles);

        // Assert
        assertEquals(EstadoPedido.CONFIRMADO, pedido.getEstado());
        assertEquals(repository.obtenerPorId("p-1").get(), pedido);
    }

    @Test
    void noDebePermitirConfirmarUnPedidoSinDetalles() {

        // Arrange
        PedidoRepository repository = new PedidoRepositoryEnMemoria();
        ConfirmarPedidoUseCase useCase = new ConfirmarPedidoUseCase(repository);

        // Act y Assert
        assertThrows(ReglaDominioException.class, () -> {
            useCase.ejecutar("p-1", "u-100", List.of());
        });
    }
}
