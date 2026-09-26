package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.CantidadInvalidaException;
import com.example.ecommerce.domain.exception.StockNegativoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InventarioTest {
    @Test
    void debeAumentarLaCantidadDisponible() {

        // Arrange
        Inventario inventario = new Inventario(10);

        // Act
        inventario.aumentar(5);

        // Assert
        assertEquals(15, inventario.getCantidadDisponible());
    }
    @Test
    void debeDisminuirLaCantidadDisponible() {

        // Arrange
        Inventario inventario = new Inventario(10);

        // Act
        inventario.disminuir(3);

        // Assert
        assertEquals(7, inventario.getCantidadDisponible());
    }
    @Test
    void noDebePermitirDisminuirPorDebajoDeCero() {

        // Arrange
        Inventario inventario = new Inventario(5);

        // Act y Assert
        assertThrows(StockNegativoException.class, () -> {
            inventario.disminuir(10);
        });
    }
    @Test
    void noDebeModificarElInventarioSiLaDisminucionGeneraStockNegativo() {

        // Arrange
        Inventario inventario = new Inventario(5);

        // Act y Assert
        assertThrows(StockNegativoException.class, () -> {
            inventario.disminuir(10);
        });

        // Assert
        assertEquals(5, inventario.getCantidadDisponible());
    }
    @Test
    void noDebePermitirAumentarUnaCantidadCero() {

        // Arrange
        Inventario inventario = new Inventario(10);

        // Act y Assert
        assertThrows(CantidadInvalidaException.class, () -> {
            inventario.aumentar(0);
        });

        assertEquals(10, inventario.getCantidadDisponible());
    }
    @Test
    void noDebePermitirDisminuirUnaCantidadCero() {

        // Arrange
        Inventario inventario = new Inventario(10);

        // Act y Assert
        assertThrows(CantidadInvalidaException.class, () -> {
            inventario.disminuir(0);
        });

        assertEquals(10, inventario.getCantidadDisponible());
    }
}
