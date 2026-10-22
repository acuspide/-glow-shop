package com.example.ecommerce.infrastructure.pagos;

import com.example.ecommerce.application.ResultadoCobro;
import com.example.ecommerce.domain.valueobject.MedioPago;
import com.example.ecommerce.domain.valueobject.Precio;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PasarelaDePagosSimuladaTest {

    private final PasarelaDePagosSimulada pasarela = new PasarelaDePagosSimulada();

    private ResultadoCobro cobrar(String monto, MedioPago medio) {
        return pasarela.cobrar(new Precio(new BigDecimal(monto)), medio);
    }

    @Test
    void deberiaAprobarUnCobroCuandoAlcanzanLosFondos() {

        // Act
        ResultadoCobro resultado = cobrar("40000", MedioPago.NEQUI);

        // Assert
        assertTrue(resultado.aprobado());
        assertNull(resultado.motivoRechazo());
    }

    @Test
    void deberiaAprobarUnCobroIgualAlSaldoDisponible() {

        // Act
        ResultadoCobro resultado = cobrar("1000000", MedioPago.NEQUI);

        // Assert
        assertTrue(resultado.aprobado());
    }

    @Test
    void deberiaRechazarPorFondosInsuficientesCuandoElMontoSuperaElSaldo() {

        // Act
        ResultadoCobro resultado = cobrar("1000001", MedioPago.NEQUI);

        // Assert
        assertFalse(resultado.aprobado());
        assertEquals("Fondos insuficientes", resultado.motivoRechazo());
    }

    @Test
    void elMismoMontoRechazadoConNequiPuedeAprobarseConTarjetaDeCredito() {

        // Act
        ResultadoCobro conNequi = cobrar("1200000", MedioPago.NEQUI);
        ResultadoCobro conTarjeta = cobrar("1200000", MedioPago.TARJETA_CREDITO);

        // Assert
        assertFalse(conNequi.aprobado());
        assertTrue(conTarjeta.aprobado());
    }

    @Test
    void cadaCobroDebeTenerUnIdentificadorDeTransaccionUnico() {

        // Act
        ResultadoCobro primero = cobrar("40000", MedioPago.NEQUI);
        ResultadoCobro segundo = cobrar("40000", MedioPago.NEQUI);

        // Assert
        assertFalse(primero.transaccionId().isBlank());
        assertNotEquals(primero.transaccionId(), segundo.transaccionId());
    }
}