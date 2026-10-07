package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.ArticuloAgotadoException;
import com.example.ecommerce.domain.exception.ArticuloNoPublicadoParaVentaException;
import com.example.ecommerce.domain.exception.CantidadSuperaStockException;
import com.example.ecommerce.domain.exception.ReglaDominioException;
import com.example.ecommerce.domain.valueobject.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ArticuloTest {


    @Test
    void dosArticulosConElMismoIdDebenSerIguales() {

        // Arrange
        Articulo articulo1 = new Articulo(
                1L,
                new NombreArticulo("Labial"),
                new Precio(new BigDecimal("20000")),
                new Categoria(1L, "Labios"),
                new Marca(1L, "Maybelline"),
                Tono.CLARO,
                List.of(TipoPiel.NORMAL),
                new Inventario(10),
                new FechaVencimiento(LocalDate.of(2027, 12, 31)),
                new Tienda(1L, "Tienda Beauty")
        );

        Articulo articulo2 = new Articulo(
                1L,
                new NombreArticulo("Base"),
                new Precio(new BigDecimal("50000")),
                new Categoria(2L, "Rostro"),
                new Marca(2L, "MAC"),
                Tono.OSCURO,
                List.of(TipoPiel.SECA),
                new Inventario(20),
                new FechaVencimiento(LocalDate.of(2028, 5, 20)),
                new Tienda(2L, "Otra Tienda")
        );

        // Act
        // La comparación se realiza directamente en el Assert.

        // Assert
        assertEquals(articulo1, articulo2);
    }
    @Test
    void noDebePermitirPublicarArticuloVencido() {

        // Arrange
        Articulo articulo = new Articulo(
                2L,
                new NombreArticulo("Labial"),
                new Precio(new BigDecimal("20000")),
                new Categoria(1L, "Labios"),
                new Marca(1L, "Maybelline"),
                Tono.OSCURO,
                List.of(TipoPiel.NORMAL),
                new Inventario(10),
                new FechaVencimiento(LocalDate.of(2025, 1, 1)),
                new Tienda(1L, "Tienda Beauty")
        );

        // Act y Assert
        assertThrows(ReglaDominioException.class, () -> {
            articulo.publicar();
        });

        // Verificamos que el estado no cambió
        assertFalse(articulo.isPublicado());
    }
    @Test
    void noDebePermitirPublicarArticuloSinCategoria() {

        // Arrange
        Articulo articulo = new Articulo(
                3L,
                new NombreArticulo("Labial"),
                new Precio(new BigDecimal("20000")),
                null,
                new Marca(1L, "Maybelline"),
                Tono.OSCURO,
                List.of(TipoPiel.NORMAL),
                new Inventario(10),
                new FechaVencimiento(LocalDate.of(2027, 12, 31)),
                new Tienda(1L, "Tienda Beauty")
        );

        // Act y Assert
        assertThrows(ReglaDominioException.class, () -> {
            articulo.publicar();
        });

        // El estado no debe haber cambiado
        assertFalse(articulo.isPublicado());
    }
    @Test
    void noDebePermitirPublicarArticuloSinPrecio() {

        // Arrange
        Articulo articulo = new Articulo(
                4L,
                new NombreArticulo("Labial"),
                null,
                new Categoria(1L, "Labios"),
                new Marca(1L, "Maybelline"),
                Tono.OSCURO,
                List.of(TipoPiel.NORMAL),
                new Inventario(10),
                new FechaVencimiento(LocalDate.of(2027, 12, 31)),
                new Tienda(1L, "Tienda Beauty")
        );

        // Act y Assert
        assertThrows(ReglaDominioException.class, () -> {
            articulo.publicar();
        });

        // El estado no debe haber cambiado
        assertFalse(articulo.isPublicado());
    }
    @Test
    void debeDisminuirElInventarioDelArticulo() {

        // Arrange
        Articulo articulo = new Articulo(
                6L,
                new NombreArticulo("Base"),
                new Precio(new BigDecimal("50000")),
                new Categoria(2L, "Rostro"),
                new Marca(2L, "MAC"),
                Tono.CLARO,
                List.of(TipoPiel.SECA),
                new Inventario(20),
                new FechaVencimiento(LocalDate.of(2028, 5, 20)),
                new Tienda(2L, "Tienda Beauty")
        );

        // Act
        articulo.disminuirInventario(5);

        // Assert
        assertEquals(15, articulo.getCantidadDisponible());
    }
    @Test
    void debeAumentarElInventarioDelArticulo() {

        // Arrange
        Articulo articulo = new Articulo(
                5L,
                new NombreArticulo("Labial"),
                new Precio(new BigDecimal("20000")),
                new Categoria(1L, "Labios"),
                new Marca(1L, "Maybelline"),
                Tono.OSCURO,
                List.of(TipoPiel.NORMAL),
                new Inventario(10),
                new FechaVencimiento(LocalDate.of(2027, 12, 31)),
                new Tienda(1L, "Tienda Beauty")
        );

        // Act
        articulo.aumentarInventario(5);

        // Assert
        assertEquals(15, articulo.getCantidadDisponible());
    }

    private Articulo crearArticulo(int stock) {
        return new Articulo(
                10L,
                new NombreArticulo("Labial"),
                new Precio(new BigDecimal("20000")),
                new Categoria(1L, "Labios"),
                new Marca(1L, "Maybelline"),
                Tono.CLARO,
                List.of(TipoPiel.NORMAL),
                new Inventario(stock),
                new FechaVencimiento(LocalDate.now().plusYears(1)),
                new Tienda(1L, "Tienda Beauty")
        );
    }

    @Test
    void debeExponerElPrecioDelArticulo() {

        // Arrange
        Articulo articulo = crearArticulo(10);

        // Act
        Precio precio = articulo.getPrecio();

        // Assert
        assertEquals(new BigDecimal("20000"), precio.valor());
    }

    @Test
    void unArticuloPublicadoConStockSuficienteDebeEstarDisponibleParaVenta() {

        // Arrange
        Articulo articulo = crearArticulo(10);
        articulo.publicar();

        // Act y Assert
        assertDoesNotThrow(() -> articulo.validarDisponibleParaVenta(10));
    }

    @Test
    void noDebeVenderseUnArticuloQueNoEstaPublicadoEnElCatalogo() {

        // Arrange
        // La tienda creó el artículo pero aún no lo publica.
        // Aunque no aparezca en el catálogo, una petición directa
        // a la API podría intentar agregarlo al carrito.
        Articulo articulo = crearArticulo(10);

        // Act y Assert
        assertThrows(ArticuloNoPublicadoParaVentaException.class, () -> {
            articulo.validarDisponibleParaVenta(1);
        });
    }

    @Test
    void unArticuloAgotadoNoDebeEstarDisponibleParaVenta() {

        // Arrange
        Articulo articulo = crearArticulo(0);
        articulo.publicar();

        // Act y Assert
        assertThrows(ArticuloAgotadoException.class, () -> {
            articulo.validarDisponibleParaVenta(1);
        });
    }

    @Test
    void noDebeEstarDisponibleParaVentaUnaCantidadMayorAlStock() {

        // Arrange
        Articulo articulo = crearArticulo(3);
        articulo.publicar();

        // Act y Assert
        assertThrows(CantidadSuperaStockException.class, () -> {
            articulo.validarDisponibleParaVenta(4);
        });
    }

    @Test
    void validarDisponibleParaVentaNoDebeDescontarInventario() {

        // Arrange
        Articulo articulo = crearArticulo(10);
        articulo.publicar();

        // Act
        articulo.validarDisponibleParaVenta(4);

        // Assert
        assertEquals(10, articulo.getCantidadDisponible());
    }
}
