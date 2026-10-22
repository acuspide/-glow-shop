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
        Articulo articulo1 = Articulo.crear(
                "a-1",
                new NombreArticulo("Labial"),
                new Precio(new BigDecimal("20000")),
                Categoria.crear("cat-1", "Labios"),
                Marca.crear("m-1", "Maybelline"),
                Tono.CLARO,
                List.of(TipoPiel.NORMAL),
                Inventario.crear(10),
                new FechaVencimiento(LocalDate.of(2027, 12, 31)),
                Tienda.crear("t-1", "Tienda Beauty")
        );

        Articulo articulo2 = Articulo.crear(
                "a-1",
                new NombreArticulo("Base"),
                new Precio(new BigDecimal("50000")),
                Categoria.crear("cat-2", "Rostro"),
                Marca.crear("m-2", "MAC"),
                Tono.OSCURO,
                List.of(TipoPiel.SECA),
                Inventario.crear(20),
                new FechaVencimiento(LocalDate.of(2028, 5, 20)),
                Tienda.crear("t-2", "Otra Tienda")
        );

        // Act
        // La comparación se realiza directamente en el Assert.

        // Assert
        assertEquals(articulo1, articulo2);
    }
    @Test
    void noDebePermitirPublicarArticuloVencido() {

        // Arrange
        Articulo articulo = Articulo.crear(
                "a-2",
                new NombreArticulo("Labial"),
                new Precio(new BigDecimal("20000")),
                Categoria.crear("cat-1", "Labios"),
                Marca.crear("m-1", "Maybelline"),
                Tono.OSCURO,
                List.of(TipoPiel.NORMAL),
                Inventario.crear(10),
                new FechaVencimiento(LocalDate.of(2025, 1, 1)),
                Tienda.crear("t-1", "Tienda Beauty")
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
        Articulo articulo = Articulo.crear(
                "a-3",
                new NombreArticulo("Labial"),
                new Precio(new BigDecimal("20000")),
                null,
                Marca.crear("m-1", "Maybelline"),
                Tono.OSCURO,
                List.of(TipoPiel.NORMAL),
                Inventario.crear(10),
                new FechaVencimiento(LocalDate.of(2027, 12, 31)),
                Tienda.crear("t-1", "Tienda Beauty")
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
        Articulo articulo = Articulo.crear(
                "a-4",
                new NombreArticulo("Labial"),
                null,
                Categoria.crear("cat-1", "Labios"),
                Marca.crear("m-1", "Maybelline"),
                Tono.OSCURO,
                List.of(TipoPiel.NORMAL),
                Inventario.crear(10),
                new FechaVencimiento(LocalDate.of(2027, 12, 31)),
                Tienda.crear("t-1", "Tienda Beauty")
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
        Articulo articulo = Articulo.crear(
                "a-6",
                new NombreArticulo("Base"),
                new Precio(new BigDecimal("50000")),
                Categoria.crear("cat-2", "Rostro"),
                Marca.crear("m-2", "MAC"),
                Tono.CLARO,
                List.of(TipoPiel.SECA),
                Inventario.crear(20),
                new FechaVencimiento(LocalDate.of(2028, 5, 20)),
                Tienda.crear("t-2", "Tienda Beauty")
        );

        // Act
        articulo.disminuirInventario(5);

        // Assert
        assertEquals(15, articulo.getCantidadDisponible());
    }
    @Test
    void debeAumentarElInventarioDelArticulo() {

        // Arrange
        Articulo articulo = Articulo.crear(
                "a-5",
                new NombreArticulo("Labial"),
                new Precio(new BigDecimal("20000")),
                Categoria.crear("cat-1", "Labios"),
                Marca.crear("m-1", "Maybelline"),
                Tono.OSCURO,
                List.of(TipoPiel.NORMAL),
                Inventario.crear(10),
                new FechaVencimiento(LocalDate.of(2027, 12, 31)),
                Tienda.crear("t-1", "Tienda Beauty")
        );

        // Act
        articulo.aumentarInventario(5);

        // Assert
        assertEquals(15, articulo.getCantidadDisponible());
    }

    private Articulo crearArticulo(int stock) {
        return  Articulo.crear(
                "a-10",
                new NombreArticulo("Labial"),
                new Precio(new BigDecimal("20000")),
                Categoria.crear("cat-1", "Labios"),
                Marca.crear("m-1", "Maybelline"),
                Tono.CLARO,
                List.of(TipoPiel.NORMAL),
                Inventario.crear(stock),
                new FechaVencimiento(LocalDate.now().plusYears(1)),
                Tienda.crear("t-1", "Tienda Beauty")
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
