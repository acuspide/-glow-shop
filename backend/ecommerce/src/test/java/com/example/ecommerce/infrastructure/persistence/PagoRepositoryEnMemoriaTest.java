package com.example.ecommerce.infrastructure.persistence;

import com.example.ecommerce.domain.entity.Pago;
import com.example.ecommerce.domain.valueobject.MedioPago;
import com.example.ecommerce.domain.valueobject.Precio;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PagoRepositoryEnMemoriaTest {

    @Test
    void deberiaGuardarYEncontrarUnPagoPorSuId() {

        // Arrange
        PagoRepositoryEnMemoria repository = new PagoRepositoryEnMemoria();
        Pago pago = Pago.crear("pa-1", "p-1", new Precio(new BigDecimal("40000")), MedioPago.NEQUI);

        // Act
        repository.guardar(pago);

        // Assert
        assertEquals(pago, repository.obtenerPorId("pa-1").orElseThrow());
    }

    @Test
    void noDebeEncontrarUnPagoQueNoExiste() {

        // Arrange
        PagoRepositoryEnMemoria repository = new PagoRepositoryEnMemoria();

        // Act y Assert
        assertTrue(repository.obtenerPorId("pa-99").isEmpty());
    }
    @Test
    void deberiaEncontrarSoloLosPagosDelPedidoIndicado() {

        // Arrange
        PagoRepositoryEnMemoria repository = new PagoRepositoryEnMemoria();
        repository.guardar(Pago.crear("pa-1", "p-1", new Precio(new BigDecimal("40000")), MedioPago.NEQUI));
        repository.guardar(Pago.crear("pa-2", "p-1", new Precio(new BigDecimal("40000")), MedioPago.PSE));
        repository.guardar(Pago.crear("pa-3", "p-2", new Precio(new BigDecimal("90000")), MedioPago.NEQUI));

        // Act
        var pagosDelPedido = repository.obtenerPorPedidoId("p-1");

        // Assert
        assertEquals(2, pagosDelPedido.size());
        assertTrue(pagosDelPedido.stream().allMatch(p -> p.getPedidoId().equals("p-1")));
    }
}
