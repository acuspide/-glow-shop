package com.example.ecommerce.domain.valueobject;

import com.example.ecommerce.domain.exception.ParametroInvalidoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaginacionTest {

    @Test
    void aceptaLaPrimeraPaginaYLosTamanosDelRango() {
        // Arrange y Act
        Paginacion minima = new Paginacion(0, 1);
        Paginacion maxima = new Paginacion(3, Paginacion.TAMANO_MAXIMO);

        // Assert
        assertEquals(0, minima.pagina());
        assertEquals(1, minima.tamano());
        assertEquals(3, maxima.pagina());
        assertEquals(50, maxima.tamano());
    }

    @Test
    void noAceptaUnaPaginaNegativa() {
        // Act y Assert
        ParametroInvalidoException error =
                assertThrows(ParametroInvalidoException.class, () -> new Paginacion(-1, 10));
        assertEquals("La página no puede ser negativa", error.getMessage());
    }

    @Test
    void noAceptaUnTamanoDeCeroNiMayorQueElMaximo() {
        // Act y Assert
        assertThrows(ParametroInvalidoException.class, () -> new Paginacion(0, 0));
        assertThrows(ParametroInvalidoException.class, () -> new Paginacion(0, 51));
    }
}
