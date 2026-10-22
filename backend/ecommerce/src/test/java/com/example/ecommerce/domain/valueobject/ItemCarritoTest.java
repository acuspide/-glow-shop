package com.example.ecommerce.domain.valueobject;

import com.example.ecommerce.domain.exception.CantidadInvalidaException;
import com.example.ecommerce.domain.exception.PrecioRequeridoException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ItemCarritoTest {

    @Test
    void noDebePermitirUnItemConCantidadCero() {

        // Arrange
        Precio precio = new Precio(new BigDecimal("10000"));

        // Act y Assert
        assertThrows(CantidadInvalidaException.class, () -> {
            new ItemCarrito("a-1", 0, precio);
        });
    }

    @Test
    void noDebePermitirUnItemConCantidadNegativa() {

        // Arrange
        Precio precio = new Precio(new BigDecimal("10000"));

        // Act y Assert
        assertThrows(CantidadInvalidaException.class, () -> {
            new ItemCarrito("a-1", -2, precio);
        });
    }

    @Test
    void noDebePermitirUnItemSinPrecio() {

        // Act y Assert
        assertThrows(PrecioRequeridoException.class, () -> {
            new ItemCarrito("a-1", 2, null);
        });
    }

    @Test
    void elSubtotalDebeSerElPrecioMultiplicadoPorLaCantidad() {

        // Arrange
        Precio precio = new Precio(new BigDecimal("10000"));
        ItemCarrito item = new ItemCarrito("a-1", 3, precio);

        // Act
        BigDecimal subtotal = item.subtotal();

        // Assert
        assertEquals(new BigDecimal("30000"), subtotal);
    }

    @Test
    void dosItemsConElMismoArticuloDebenSerIguales() {

        // Arrange
        Precio precio = new Precio(new BigDecimal("10000"));
        ItemCarrito item1 = new ItemCarrito("a-1", 2, precio);
        ItemCarrito item2 = new ItemCarrito("a-1", 4, precio);

        // Act
        // No necesitamos una accion adicional porque equals()
        // es la operacion que queremos probar.

        // Assert
        assertEquals(item1, item2);
    }
}