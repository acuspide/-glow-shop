package com.example.ecommerce.domain.valueobject;

import com.example.ecommerce.domain.exception.PuntuacionInvalidaException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PuntuacionTest {

    @Test
    void aceptaDe1A5Estrellas() {
        assertEquals(1, new Puntuacion(1).valor());
        assertEquals(5, new Puntuacion(5).valor());
    }

    @Test
    void noAceptaCeroNiMasDe5() {
        assertThrows(PuntuacionInvalidaException.class, () -> new Puntuacion(0));
        assertThrows(PuntuacionInvalidaException.class, () -> new Puntuacion(6));
    }
}
