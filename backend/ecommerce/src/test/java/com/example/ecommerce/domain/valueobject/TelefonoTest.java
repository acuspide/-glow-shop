package com.example.ecommerce.domain.valueobject;

import com.example.ecommerce.domain.exception.TelefonoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TelefonoTest {

    @Test
    void debeAceptarUnTelefonoConSoloDigitos() {
        // Arrange y Act
        Telefono telefono = new Telefono("3001234567");

        // Assert
        assertEquals("3001234567", telefono.valor());
    }

    @Test
    void debeAceptarUnTelefonoConPrefijoInternacional() {
        // Arrange y Act
        Telefono telefono = new Telefono("+573001234567");

        // Assert
        assertEquals("+573001234567", telefono.valor());
    }

    @Test
    void debeQuitarLosEspaciosDeLosExtremos() {
        // Arrange y Act
        Telefono telefono = new Telefono("  3001234567  ");

        // Assert
        assertEquals("3001234567", telefono.valor());
    }

    @Test
    void noDebeAceptarUnTelefonoNuloNiVacio() {
        // Act y Assert
        assertThrows(TelefonoInvalidoException.class, () -> new Telefono(null));
        assertThrows(TelefonoInvalidoException.class, () -> new Telefono("   "));
    }

    @Test
    void noDebeAceptarUnTelefonoConLetrasOMuyCortoOMuyLargo() {
        // Act y Assert
        assertThrows(TelefonoInvalidoException.class, () -> new Telefono("300abc4567"));
        assertThrows(TelefonoInvalidoException.class, () -> new Telefono("123456"));
        assertThrows(TelefonoInvalidoException.class, () -> new Telefono("1234567890123456"));
    }

    @Test
    void dosTelefonosConElMismoValorDebenSerIguales() {
        // Arrange
        Telefono uno = new Telefono("3001234567");
        Telefono otro = new Telefono("3001234567");

        // Act y Assert
        assertEquals(uno, otro);
        assertEquals(uno.hashCode(), otro.hashCode());
    }
}
