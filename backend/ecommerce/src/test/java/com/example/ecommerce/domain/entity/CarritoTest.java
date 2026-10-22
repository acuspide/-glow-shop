package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.ArticuloAgotadoException;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoEnCarritoException;
import com.example.ecommerce.domain.exception.ArticuloNoPublicadoParaVentaException;
import com.example.ecommerce.domain.exception.CantidadInvalidaException;
import com.example.ecommerce.domain.exception.CantidadSuperaStockException;
import com.example.ecommerce.domain.valueobject.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CarritoTest {

    private Articulo crearArticulo(String id, String precio, int stock) {
        return Articulo.crear(
                id,
                new NombreArticulo("Labial"),
                new Precio(new BigDecimal(precio)),
                Categoria.crear("cat-1", "Labios"),
                Marca.crear("m-1", "Bloom"),
                Tono.CLARO,
                List.of(TipoPiel.NORMAL),
                Inventario.crear(stock),
                new FechaVencimiento(LocalDate.now().plusYears(1)),
                Tienda.crear("t-1", "Tienda Beauty")
        );
    }

    private Articulo crearArticuloPublicado(String id, String precio, int stock) {
        Articulo articulo = crearArticulo(id, precio, stock);
        articulo.publicar();
        return articulo;
    }

    @Test
    void unCarritoNuevoDebeEstarVacioYPertenecerAlCliente() {

        // Arrange y Act
        Carrito carrito = Carrito.crear("c-1", "u-100");

        // Assert
        assertTrue(carrito.estaVacio());
        assertEquals("u-100", carrito.getClienteId());
    }

    @Test
    void agregarUnArticuloDebeSumarloAlTotalConSuPrecioVigente() {

        // Arrange
        Carrito carrito = Carrito.crear("c-1", "u-100");
        Articulo labial = crearArticuloPublicado("a-1", "10000", 5);

        // Act
        carrito.agregarArticulo(labial, 2);

        // Assert
        assertEquals(new BigDecimal("20000"), carrito.calcularTotal());
        assertEquals(new BigDecimal("10000"), carrito.getItems().get(0).getPrecioUnitario().valor());
    }

    @Test
    void agregarElMismoArticuloDosVecesDebeAcumularLaCantidad() {

        // Arrange
        Carrito carrito = Carrito.crear("c-1", "u-100");
        Articulo labial = crearArticuloPublicado("a-1", "10000", 5);

        // Act
        carrito.agregarArticulo(labial, 2);
        carrito.agregarArticulo(labial, 2);

        // Assert
        assertEquals(1, carrito.getItems().size());
        assertEquals(4, carrito.getItems().get(0).getCantidad());
    }

    @Test
    void noDebePermitirQueLaCantidadAcumuladaSupereElStock() {

        // Arrange
        Carrito carrito = Carrito.crear("c-1", "u-100");
        Articulo labial = crearArticuloPublicado("a-1", "10000", 5);
        carrito.agregarArticulo(labial, 3);

        // Act y Assert
        assertThrows(CantidadSuperaStockException.class, () -> {
            carrito.agregarArticulo(labial, 3);
        });
        assertEquals(3, carrito.getItems().get(0).getCantidad());
    }

    @Test
    void noDebePermitirAgregarUnaCantidadCero() {

        // Arrange
        Carrito carrito = Carrito.crear("c-1", "u-100");
        Articulo labial = crearArticuloPublicado("a-1", "10000", 5);

        // Act y Assert
        assertThrows(CantidadInvalidaException.class, () -> {
            carrito.agregarArticulo(labial, 0);
        });
    }

    @Test
    void noDebePermitirAgregarUnArticuloNoPublicado() {

        // Arrange
        Carrito carrito = Carrito.crear("c-1", "u-100");
        Articulo labialSinPublicar = crearArticulo("a-1", "10000", 5);

        // Act y Assert
        assertThrows(ArticuloNoPublicadoParaVentaException.class, () -> {
            carrito.agregarArticulo(labialSinPublicar, 1);
        });
        assertTrue(carrito.estaVacio());
    }

    @Test
    void noDebePermitirAgregarUnArticuloAgotado() {

        // Arrange
        Carrito carrito = Carrito.crear("c-1", "u-100");
        Articulo labialAgotado = crearArticuloPublicado("a-1", "10000", 0);

        // Act y Assert
        assertThrows(ArticuloAgotadoException.class, () -> {
            carrito.agregarArticulo(labialAgotado, 1);
        });
    }

    @Test
    void agregarAlCarritoNoDebeDescontarElInventarioDelArticulo() {

        // Arrange
        Carrito carrito = Carrito.crear("c-1", "u-100");
        Articulo labial = crearArticuloPublicado("a-1", "10000", 5);

        // Act
        carrito.agregarArticulo(labial, 2);

        // Assert
        assertEquals(5, labial.getCantidadDisponible());
    }
    @Test
    void quitarUnArticuloDebeSacarloDelCarritoYActualizarElTotal() {

        // Arrange
        Carrito carrito = Carrito.crear("c-1", "u-100");
        carrito.agregarArticulo(crearArticuloPublicado("a-1", "10000", 5), 2);
        carrito.agregarArticulo(crearArticuloPublicado("a-2", "30000", 5), 1);

        // Act
        carrito.quitarArticulo("a-1");

        // Assert
        assertEquals(1, carrito.getItems().size());
        assertEquals("a-2", carrito.getItems().get(0).getArticuloId());
        assertEquals(new BigDecimal("30000"), carrito.calcularTotal());
    }
    @Test
    void noDebePermitirQuitarUnArticuloQueNoEstaEnElCarrito() {

        // Arrange
        Carrito carrito = Carrito.crear("c-1", "u-100");

        // Act y Assert
        assertThrows(ArticuloNoEncontradoEnCarritoException.class, () -> {
            carrito.quitarArticulo("a-99");
        });
    }
    @Test
    void vaciarElCarritoDebeDejarloSinItems() {

        // Arrange
        Carrito carrito = Carrito.crear("c-1", "u-100");
        carrito.agregarArticulo(crearArticuloPublicado("a-1", "10000", 5), 2);

        // Act
        carrito.vaciar();

        // Assert
        assertTrue(carrito.estaVacio());
    }
}