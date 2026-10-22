package com.example.ecommerce.infrastructure.pagos;

import com.example.ecommerce.application.ResultadoCobro;
import com.example.ecommerce.domain.valueobject.MedioPago;
import com.example.ecommerce.domain.valueobject.Precio;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PasarelaDePagosSimuladaTest {

    private final PasarelaDePagosSimulada pasarela = new PasarelaDePagosSimulada();

    @Test
    void deberiaAprobarUnCobroCuandoAlcanzanLosFondos() {

        // Act
        ResultadoCobro resultado = pasarela.cobrar(new Precio(new BigDecimal("40000")), MedioPago.NEQUI);

        // Assert
        assertTrue(resultado.aprobado());
        assertNull(resultado.motivoRechazo());
    }

    @Test
    void deberiaAprobarUnCobroIgualAlSaldoDisponible() {

        // Act
        ResultadoCobro resultado = pasarela.cobrar(new Precio(new BigDecimal("1000000")), MedioPago.PSE);

        // Assert
        assertTrue(resultado.aprobado());
    }

    @Test
    void deberiaRechazarPorFondosInsuficientesCuandoElMontoSuperaElSaldo() {

        // Act
        ResultadoCobro resultado = pasarela.cobrar(new Precio(new BigDecimal("1000001")), MedioPago.TARJETA_CREDITO);

        // Assert
        assertFalse(resultado.aprobado());
        assertEquals("Fondos insuficientes", resultado.motivoRechazo());
    }

    @Test
    void cadaCobroDebeTenerUnIdentificadorDeTransaccionUnico() {

        // Act
        ResultadoCobro primero = pasarela.cobrar(new Precio(new BigDecimal("40000")), MedioPago.NEQUI);
        ResultadoCobro segundo = pasarela.cobrar(new Precio(new BigDecimal("40000")), MedioPago.NEQUI);

        // Assert
        assertFalse(primero.transaccionId().isBlank());
        assertNotEquals(primero.transaccionId(), segundo.transaccionId());
    }
}
