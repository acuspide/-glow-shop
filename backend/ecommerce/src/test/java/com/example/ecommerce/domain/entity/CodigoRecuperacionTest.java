package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.CodigoRecuperacionInvalidoException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.function.BiPredicate;

import static org.junit.jupiter.api.Assertions.*;

public class CodigoRecuperacionTest {

    private final LocalDateTime ahora = LocalDateTime.of(2026, 10, 20, 10, 0);

    // Comparador de prueba: el "hash" es el código al revés.
    private final BiPredicate<String, String> coincide = (codigo, hash) ->
            new StringBuilder(codigo).reverse().toString().equals(hash);

    private CodigoRecuperacion codigoNuevo() {
        return CodigoRecuperacion.generar("u-1", "654321", ahora); // corresponde al código 123456
    }

    @Test
    void elCodigoCorrectoDentroDeLos15MinutosQuedaUsado() {
        CodigoRecuperacion codigo = codigoNuevo();

        codigo.usar("123456", coincide, ahora.plusMinutes(14));

        assertTrue(codigo.isUsado());
    }

    @Test
    void unCodigoVencidoNoSePuedeUsar() {
        CodigoRecuperacion codigo = codigoNuevo();

        assertThrows(CodigoRecuperacionInvalidoException.class,
                () -> codigo.usar("123456", coincide, ahora.plusMinutes(15)));
        assertFalse(codigo.isUsado());
    }

    @Test
    void unCodigoUsadoNoSePuedeVolverAUsar() {
        CodigoRecuperacion codigo = codigoNuevo();
        codigo.usar("123456", coincide, ahora);

        assertThrows(CodigoRecuperacionInvalidoException.class, () -> codigo.usar("123456", coincide, ahora));
    }

    @Test
    void unCodigoIncorrectoSumaUnIntentoFallido() {
        CodigoRecuperacion codigo = codigoNuevo();

        assertThrows(CodigoRecuperacionInvalidoException.class, () -> codigo.usar("000000", coincide, ahora));
        assertEquals(1, codigo.getIntentosFallidos());
        assertFalse(codigo.isUsado());
    }

    @Test
    void despuesDe5IntentosFallidosNiElCodigoCorrectoSirve() {
        CodigoRecuperacion codigo = codigoNuevo();
        for (int i = 0; i < CodigoRecuperacion.MAXIMO_INTENTOS; i++) {
            assertThrows(CodigoRecuperacionInvalidoException.class, () -> codigo.usar("000000", coincide, ahora));
        }

        assertThrows(CodigoRecuperacionInvalidoException.class, () -> codigo.usar("123456", coincide, ahora));
    }
}
