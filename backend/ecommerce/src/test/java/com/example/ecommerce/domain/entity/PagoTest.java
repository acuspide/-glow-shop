package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.MedioPagoRequeridoException;
import com.example.ecommerce.domain.exception.MontoPagoRequeridoException;
import com.example.ecommerce.domain.exception.TransaccionRequeridaException;
import com.example.ecommerce.domain.exception.TransicionEstadoInvalidaException;
import com.example.ecommerce.domain.valueobject.EstadoPago;
import com.example.ecommerce.domain.valueobject.MedioPago;
import com.example.ecommerce.domain.valueobject.Precio;
import org.junit.jupiter.api.Test;
import com.example.ecommerce.domain.exception.MotivoRechazoRequeridoException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PagoTest {

    private Pago crearPago() {
        return Pago.crear("pa-1", "p-10", new Precio(new BigDecimal("50000")), MedioPago.TARJETA_CREDITO);
    }

    @Test
    void unPagoNuevoDebeNacerPendienteSinTransaccionYConSuMedioDePago() {

        // Arrange y Act
        Pago pago = crearPago();

        // Assert
        assertEquals(EstadoPago.PENDIENTE, pago.getEstado());
        assertEquals(MedioPago.TARJETA_CREDITO, pago.getMedioPago());
        assertNull(pago.getTransaccionId());
        assertFalse(pago.estaAprobado());
    }

    @Test
    void unPagoSinMontoNoDebeCrearse() {

        // Act y Assert
        assertThrows(MontoPagoRequeridoException.class, () -> {
            Pago.crear("pa-1", "p-10", null, MedioPago.TARJETA_CREDITO);
        });
    }

    @Test
    void unPagoSinMedioDePagoNoDebeCrearse() {

        // Act y Assert
        assertThrows(MedioPagoRequeridoException.class, () -> {
            Pago.crear("pa-1", "p-10", new Precio(new BigDecimal("50000")), null);
        });
    }

    @Test
    void unPagoPendienteDebePoderAprobarseYGuardarLaTransaccion() {

        // Arrange
        Pago pago = crearPago();

        // Act
        pago.aprobar("tx-123");

        // Assert
        assertTrue(pago.estaAprobado());
        assertEquals("tx-123", pago.getTransaccionId());
    }

    @Test
    void unPagoPendienteDebePoderRechazarseYGuardarLaTransaccionYElMotivo() {

        // Arrange
        Pago pago = crearPago();

        // Act
        pago.rechazar("tx-456", "Fondos insuficientes");

        // Assert
        assertEquals(EstadoPago.RECHAZADO, pago.getEstado());
        assertEquals("tx-456", pago.getTransaccionId());
        assertEquals("Fondos insuficientes", pago.getMotivoRechazo());
    }

    @Test
    void noDebePermitirRechazarSinMotivo() {

        // Arrange
        Pago pago = crearPago();

        // Act y Assert
        assertThrows(MotivoRechazoRequeridoException.class, () -> pago.rechazar("tx-456", " "));
        assertEquals(EstadoPago.PENDIENTE, pago.getEstado());
    }

    @Test
    void noDebePermitirAprobarSinIdentificadorDeTransaccion() {

        // Arrange
        Pago pago = crearPago();

        // Act y Assert
        assertThrows(TransaccionRequeridaException.class, () -> pago.aprobar(" "));
        assertEquals(EstadoPago.PENDIENTE, pago.getEstado());
    }

    @Test
    void unPagoAprobadoNoDebePoderRechazarse() {

        // Arrange
        Pago pago = crearPago();
        pago.aprobar("tx-123");

        // Act y Assert
        assertThrows(TransicionEstadoInvalidaException.class, () -> pago.rechazar("tx-789", "Fondos insuficientes"));
    }

    @Test
    void unPagoAprobadoDebePoderReembolsarse() {

        // Arrange
        Pago pago = crearPago();
        pago.aprobar("tx-123");

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
        assertThrows(TransicionEstadoInvalidaException.class, pago::reembolsar);
    }
}