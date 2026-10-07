package com.example.ecommerce.domain.valueobject;

import com.example.ecommerce.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ContrasenaPlanaTest {

    @Test
    void dosContrasenasConElMismoValorDebenSerIguales() {
        // Arrange y Act
        ContrasenaPlana a = new ContrasenaPlana("clave123");
        ContrasenaPlana b = new ContrasenaPlana("clave123");

        // Assert
        assertEquals(a, b);
    }

    @Test
    void noDebeCrearseConValorNulo() {
        // Act y Assert
        assertThrows(ReglaDominioException.class, () -> new ContrasenaPlana(null));
    }

    @Test
    void noDebeCrearseConValorVacioOEnBlanco() {
        // Act y Assert
        assertThrows(ReglaDominioException.class, () -> new ContrasenaPlana(""));
        assertThrows(ReglaDominioException.class, () -> new ContrasenaPlana("   "));
    }

    @Test
    void suRepresentacionEnTextoNoDebeRevelarLaContrasena() {
        // Arrange
        ContrasenaPlana contrasena = new ContrasenaPlana("clave123");

        // Act y Assert
        assertFalse(contrasena.toString().contains("clave123"));
    }
}
