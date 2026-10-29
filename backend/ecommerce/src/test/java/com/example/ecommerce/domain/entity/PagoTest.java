package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.*;
import com.example.ecommerce.domain.valueobject.EstadoPago;
import com.example.ecommerce.domain.valueobject.MedioPago;
import com.example.ecommerce.domain.valueobject.Precio;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

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
    @Test
    void unPagoRechazadoSePuedeReintentar() {

        // Arrange
        Pago pago = crearPago();
        pago.rechazar("tx-456", "Fondos insuficientes");

        // Act y Assert
        assertDoesNotThrow(pago::verificarQueSePuedeReintentar);
    }

    @Test
    void unPagoAprobadoNoSePuedeReintentar() {

        // Arrange
        Pago pago = crearPago();
        pago.aprobar("tx-123");

        // Act y Assert
        assertThrows(PagoNoReintentableException.class, pago::verificarQueSePuedeReintentar);
    }

    @Test
    void unPagoPendienteNoSePuedeReintentar() {

        // Arrange
        Pago pago = crearPago();

        // Act y Assert
        assertThrows(PagoNoReintentableException.class, pago::verificarQueSePuedeReintentar);
    }
    @Test
    void unPagoNuevoDebeGuardarLaFechaEnQueSeCreo() {

        // Arrange
        LocalDateTime antes = LocalDateTime.now();

        // Act
        Pago pago = crearPago();

        // Assert
        assertNotNull(pago.getFechaPago());
        assertFalse(pago.getFechaPago().isBefore(antes));
        assertFalse(pago.getFechaPago().isAfter(LocalDateTime.now()));
    }

    @Test
    void registrarResultadoAprobadoDebeDejarElPagoAprobado() {

        // Arrange
        Pago pago = crearPago();

        // Act
        pago.registrarResultado(true, "tx-1", null);

        // Assert
        assertEquals(EstadoPago.APROBADO, pago.getEstado());
        assertEquals("tx-1", pago.getTransaccionId());
    }

    @Test
    void registrarResultadoRechazadoDebeDejarElPagoRechazadoConSuMotivo() {

        // Arrange
        Pago pago = crearPago();

        // Act
        pago.registrarResultado(false, "tx-2", "Fondos insuficientes");

        // Assert
        assertEquals(EstadoPago.RECHAZADO, pago.getEstado());
        assertEquals("Fondos insuficientes", pago.getMotivoRechazo());
    }

    @Test
    void registrarResultadoRechazadoSinMotivoDebeFallar() {

        // Arrange
        Pago pago = crearPago();

        // Act y Assert
        assertThrows(MotivoRechazoRequeridoException.class, () -> pago.registrarResultado(false, "tx-2", " "));
    }

    @Test
    void medioParaReintentarDebeUsarElMedioElegido() {

        // Arrange
        Pago pago = crearPago();                                         // TARJETA_CREDITO

        // Act y Assert
        assertEquals(MedioPago.NEQUI, pago.medioParaReintentar(MedioPago.NEQUI));
    }

    @Test
    void medioParaReintentarSinElegirDebeUsarElMismoMedio() {

        // Arrange
        Pago pago = crearPago();

        // Act y Assert
        assertEquals(MedioPago.TARJETA_CREDITO, pago.medioParaReintentar(null));
    }
}
