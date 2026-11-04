package com.example.ecommerce.domain.valueobject;

import com.example.ecommerce.domain.exception.ParametroInvalidoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LimiteRankingTest {

    @Test
    void aceptaLimitesDe1A20() {
        // Act y Assert
        assertEquals(1, new LimiteRanking(1).valor());
        assertEquals(20, new LimiteRanking(LimiteRanking.LIMITE_MAXIMO).valor());
    }

    @Test
    void noAceptaCeroNiMasDe20() {
        // Act y Assert
        ParametroInvalidoException error =
                assertThrows(ParametroInvalidoException.class, () -> new LimiteRanking(0));
        assertEquals("El límite debe estar entre 1 y 20", error.getMessage());
        assertThrows(ParametroInvalidoException.class, () -> new LimiteRanking(21));
    }
}
