package com.example.ecommerce.domain.valueobject;

import com.example.ecommerce.domain.exception.ContrasenaDebilException;
import com.example.ecommerce.domain.exception.ContrasenaRequeridaException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ContrasenaPlanaTest {

    @Test
    void dosContrasenasConElMismoValorDebenSerIguales() {
        // Arrange y Act
        ContrasenaPlana a = new ContrasenaPlana("Clave123");
        ContrasenaPlana b = new ContrasenaPlana("Clave123");

        // Assert
        assertEquals(a, b);
    }

    @Test
    void debeAceptarUnaContrasenaConOchoCaracteresMayusculaYNumero() {
        // Act y Assert
        assertDoesNotThrow(() -> new ContrasenaPlana("Abcdefg1"));
    }

    @Test
    void noDebeCrearseConValorNulo() {
        // Act y Assert
        assertThrows(ContrasenaRequeridaException.class, () -> new ContrasenaPlana(null));
    }

    @Test
    void noDebeCrearseConValorVacioOEnBlanco() {
        // Act y Assert
        assertThrows(ContrasenaRequeridaException.class, () -> new ContrasenaPlana(""));
        assertThrows(ContrasenaRequeridaException.class, () -> new ContrasenaPlana("   "));
    }

    @Test
    void noDebeAceptarUnaContrasenaDeMenosDeOchoCaracteres() {
        // Act y Assert
        assertThrows(ContrasenaDebilException.class, () -> new ContrasenaPlana("Ab1"));
        assertThrows(ContrasenaDebilException.class, () -> new ContrasenaPlana("Abcdef1"));
    }

    @Test
    void noDebeAceptarUnaContrasenaSinMayuscula() {
        // Act y Assert
        assertThrows(ContrasenaDebilException.class, () -> new ContrasenaPlana("clave1234"));
    }

    @Test
    void noDebeAceptarUnaContrasenaSinNumero() {
        // Act y Assert
        assertThrows(ContrasenaDebilException.class, () -> new ContrasenaPlana("ClaveSegura"));
    }

    @Test
    void suRepresentacionEnTextoNoDebeRevelarLaContrasena() {
        // Arrange
        ContrasenaPlana contrasena = new ContrasenaPlana("Clave123");

        // Act y Assert
        assertFalse(contrasena.toString().contains("Clave123"));
    }
}
