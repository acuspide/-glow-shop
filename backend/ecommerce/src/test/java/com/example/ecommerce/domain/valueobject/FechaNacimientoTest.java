package com.example.ecommerce.domain.valueobject;

import com.example.ecommerce.domain.exception.FechaNacimientoInvalidaException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FechaNacimientoTest {

    @Test
    void debeAceptarUnaFechaPasada() {
        // Arrange y Act
        FechaNacimiento fecha = new FechaNacimiento(LocalDate.of(2000, 5, 20));

        // Assert
        assertEquals(LocalDate.of(2000, 5, 20), fecha.valor());
    }

    @Test
    void debeAceptarLaFechaDeHoy() {
        // Arrange
        LocalDate hoy = LocalDate.now();

        // Act
        FechaNacimiento fecha = new FechaNacimiento(hoy);

        // Assert
        assertEquals(hoy, fecha.valor());
    }

    @Test
    void noDebeAceptarUnaFechaNula() {
        // Act y Assert
        assertThrows(FechaNacimientoInvalidaException.class, () -> new FechaNacimiento(null));
    }

    @Test
    void noDebeAceptarUnaFechaFutura() {
        // Act y Assert
        assertThrows(FechaNacimientoInvalidaException.class, () ->
                new FechaNacimiento(LocalDate.now().plusDays(1)));
    }

    @Test
    void noDebeAceptarUnaFechaAnteriorA1900() {
        // Act y Assert
        assertThrows(FechaNacimientoInvalidaException.class, () ->
                new FechaNacimiento(LocalDate.of(1899, 12, 31)));
    }

    @Test
    void dosFechasConElMismoValorDebenSerIguales() {
        // Arrange
        FechaNacimiento una = new FechaNacimiento(LocalDate.of(2000, 1, 1));
        FechaNacimiento otra = new FechaNacimiento(LocalDate.of(2000, 1, 1));

        // Act y Assert
        assertEquals(una, otra);
        assertEquals(una.hashCode(), otra.hashCode());
    }
}
