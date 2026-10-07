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

    private Articulo crearArticulo(long id, String precio, int stock) {
        return new Articulo(
                id,
                new NombreArticulo("Labial"),
                new Precio(new BigDecimal(precio)),
                new Categoria(1L, "Labios"),
                new Marca(1L, "Bloom"),
                Tono.CLARO,
                List.of(TipoPiel.NORMAL),
                new Inventario(stock),
                new FechaVencimiento(LocalDate.now().plusYears(1)),
                new Tienda(1L, "Tienda Beauty")
        );
    }

    private Articulo crearArticuloPublicado(long id, String precio, int stock) {
        Articulo articulo = crearArticulo(id, precio, stock);
        articulo.publicar();
        return articulo;
    }

    @Test
    void unCarritoNuevoDebeEstarVacioYPertenecerAlCliente() {

        // Arrange y Act
        Carrito carrito = Carrito.crear(1L, 100L);

        // Assert
        assertTrue(carrito.estaVacio());
        assertEquals(100L, carrito.getClienteId());
    }

    @Test
    void agregarUnArticuloDebeSumarloAlTotalConSuPrecioVigente() {

        // Arrange
        Carrito carrito = Carrito.crear(1L, 100L);
        Articulo labial = crearArticuloPublicado(1L, "10000", 5);

        // Act
        carrito.agregarArticulo(labial, 2);

        // Assert
        assertEquals(new BigDecimal("20000"), carrito.calcularTotal());
        assertEquals(new BigDecimal("10000"), carrito.getItems().get(0).getPrecioUnitario().valor());
    }

    @Test
    void agregarElMismoArticuloDosVecesDebeAcumularLaCantidad() {

        // Arrange
        Carrito carrito = Carrito.crear(1L, 100L);
        Articulo labial = crearArticuloPublicado(1L, "10000", 5);

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
        Carrito carrito = Carrito.crear(1L, 100L);
        Articulo labial = crearArticuloPublicado(1L, "10000", 5);
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
        Carrito carrito = Carrito.crear(1L, 100L);
        Articulo labial = crearArticuloPublicado(1L, "10000", 5);

        // Act y Assert
        assertThrows(CantidadInvalidaException.class, () -> {
            carrito.agregarArticulo(labial, 0);
        });
    }

    @Test
    void noDebePermitirAgregarUnArticuloNoPublicado() {

        // Arrange
        Carrito carrito = Carrito.crear(1L, 100L);
        Articulo labialSinPublicar = crearArticulo(1L, "10000", 5);

        // Act y Assert
        assertThrows(ArticuloNoPublicadoParaVentaException.class, () -> {
            carrito.agregarArticulo(labialSinPublicar, 1);
        });
        assertTrue(carrito.estaVacio());
    }

    @Test
    void noDebePermitirAgregarUnArticuloAgotado() {

        // Arrange
        Carrito carrito = Carrito.crear(1L, 100L);
        Articulo labialAgotado = crearArticuloPublicado(1L, "10000", 0);

        // Act y Assert
        assertThrows(ArticuloAgotadoException.class, () -> {
            carrito.agregarArticulo(labialAgotado, 1);
        });
    }

    @Test
    void agregarAlCarritoNoDebeDescontarElInventarioDelArticulo() {

        // Arrange
        Carrito carrito = Carrito.crear(1L, 100L);
        Articulo labial = crearArticuloPublicado(1L, "10000", 5);

        // Act
        carrito.agregarArticulo(labial, 2);

        // Assert
        assertEquals(5, labial.getCantidadDisponible());
    }

    @Test
    void noDebePermitirEliminarUnArticuloQueNoEstaEnElCarrito() {

        // Arrange
        Carrito carrito = Carrito.crear(1L, 100L);

        // Act y Assert
        assertThrows(ArticuloNoEncontradoEnCarritoException.class, () -> {
            carrito.eliminarItem(99L);
        });
    }

    @Test
    void vaciarElCarritoDebeDejarloSinItems() {

        // Arrange
        Carrito carrito = Carrito.crear(1L, 100L);
        carrito.agregarArticulo(crearArticuloPublicado(1L, "10000", 5), 2);

        // Act
        carrito.vaciar();

        // Assert
        assertTrue(carrito.estaVacio());
    }
}