package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.ReglaDominioException;
import com.example.ecommerce.domain.valueobject.EstadoPago;
import com.example.ecommerce.domain.valueobject.Precio;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PagoTest {

    private Pago crearPago() {
        return new Pago(1L, 10L, new Precio(new BigDecimal("50000")));
    }

    @Test
    void unPagoNuevoDebeNacerPendienteYNoAprobado() {

        // Arrange y Act
        Pago pago = crearPago();

        // Assert
        assertEquals(EstadoPago.PENDIENTE, pago.getEstado());
        assertFalse(pago.estaAprobado());
    }

    @Test
    void unPagoSinMontoDebeLanzarReglaDominioException() {

        // Act y Assert
        assertThrows(ReglaDominioException.class, () -> {
            new Pago(1L, 10L, null);
        });
    }

    @Test
    void unPagoPendienteDebePoderAprobarse() {

        // Arrange
        Pago pago = crearPago();

        // Act
        pago.aprobar();

        // Assert
        assertTrue(pago.estaAprobado());
    }

    @Test
    void unPagoAprobadoNoDebePoderRechazarse() {

        // Arrange
        Pago pago = crearPago();
        pago.aprobar();

        // Act y Assert
        assertThrows(ReglaDominioException.class, pago::rechazar);
    }

    @Test
    void unPagoAprobadoDebePoderReembolsarse() {

        // Arrange
        Pago pago = crearPago();
        pago.aprobar();

        // Act
        pago.reembolsar();

        // Assert
        assertEquals(EstadoPago.REEMBOLSADO, pago.getEstado());
        assertFalse(pago.estaAprobado());
    }

    @Test
    void unPagoPendienteNoDebePoderReembolsarse() {

        // Arrange
        Pago pago = crearPago();

        // Act y Assert
        assertThrows(ReglaDominioException.class, pago::reembolsar);
    }
}
