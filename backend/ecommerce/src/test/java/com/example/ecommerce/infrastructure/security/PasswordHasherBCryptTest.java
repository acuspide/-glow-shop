package com.example.ecommerce.infrastructure.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordHasherBCryptTest {

    private final PasswordHasherBCrypt hasher = new PasswordHasherBCrypt();

    @Test
    void elHashEsBCryptYNoContieneLaContrasena() {
        // Act
        String hash = hasher.hash("Clave12345");

        // Assert
        assertTrue(hash.startsWith("$2a$"));
        assertFalse(hash.contains("Clave12345"));
    }

    @Test
    void laMismaContrasenaDaHashesDistintosPorLaSal() {
        // Act
        String primero = hasher.hash("Clave12345");
        String segundo = hasher.hash("Clave12345");

        // Assert
        assertNotEquals(primero, segundo);
        assertTrue(hasher.verificar("Clave12345", primero));
        assertTrue(hasher.verificar("Clave12345", segundo));
    }

    @Test
    void unaContrasenaEquivocadaNoSeVerifica() {
        // Arrange
        String hash = hasher.hash("Clave12345");

        // Act y Assert
        assertFalse(hasher.verificar("OtraClave99", hash));
    }
}
