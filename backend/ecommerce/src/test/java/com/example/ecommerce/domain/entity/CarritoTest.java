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
    @Test
    void modificarCantidadDebeReemplazarLaCantidadNoSumarla() {

        // Arrange
        Carrito carrito = Carrito.crear("c-1", "u-100");
        Articulo labial = crearArticuloPublicado("a-1", "10000", 10);
        carrito.agregarArticulo(labial, 2);

        // Act
        carrito.modificarCantidad(labial, 5);

        // Assert
        assertEquals(1, carrito.getItems().size());
        assertEquals(5, carrito.getItems().get(0).getCantidad());
        assertEquals(new BigDecimal("50000"), carrito.calcularTotal());
    }

    @Test
    void modificarCantidadDebePermitirDisminuirLaCantidad() {

        // Arrange
        Carrito carrito = Carrito.crear("c-1", "u-100");
        Articulo labial = crearArticuloPublicado("a-1", "10000", 10);
        carrito.agregarArticulo(labial, 5);

        // Act
        carrito.modificarCantidad(labial, 1);

        // Assert
        assertEquals(1, carrito.getItems().get(0).getCantidad());
        assertEquals(new BigDecimal("10000"), carrito.calcularTotal());
    }

    @Test
    void modificarCantidadDebeConservarElOrdenDeLosArticulos() {

        // Arrange
        Carrito carrito = Carrito.crear("c-1", "u-100");
        Articulo labial = crearArticuloPublicado("a-1", "10000", 10);
        Articulo base = crearArticuloPublicado("a-2", "30000", 10);
        carrito.agregarArticulo(labial, 1);
        carrito.agregarArticulo(base, 1);

        // Act
        carrito.modificarCantidad(labial, 3);

        // Assert
        assertEquals("a-1", carrito.getItems().get(0).getArticuloId());
        assertEquals("a-2", carrito.getItems().get(1).getArticuloId());
    }

    @Test
    void noDebePermitirModificarAUnaCantidadCero() {

        // Arrange
        Carrito carrito = Carrito.crear("c-1", "u-100");
        Articulo labial = crearArticuloPublicado("a-1", "10000", 10);
        carrito.agregarArticulo(labial, 2);

        // Act y Assert
        assertThrows(CantidadInvalidaException.class, () -> {
            carrito.modificarCantidad(labial, 0);
        });
        assertEquals(2, carrito.getItems().get(0).getCantidad());
    }

    @Test
    void noDebePermitirQueLaNuevaCantidadSupereElStock() {

        // Arrange
        Carrito carrito = Carrito.crear("c-1", "u-100");
        Articulo labial = crearArticuloPublicado("a-1", "10000", 5);
        carrito.agregarArticulo(labial, 2);

        // Act y Assert
        assertThrows(CantidadSuperaStockException.class, () -> {
            carrito.modificarCantidad(labial, 6);
        });
        assertEquals(2, carrito.getItems().get(0).getCantidad());
    }

    @Test
    void noDebePermitirModificarUnArticuloQueNoEstaEnElCarrito() {

        // Arrange
        Carrito carrito = Carrito.crear("c-1", "u-100");
        Articulo labial = crearArticuloPublicado("a-1", "10000", 10);

        // Act y Assert
        assertThrows(ArticuloNoEncontradoEnCarritoException.class, () -> {
            carrito.modificarCantidad(labial, 2);
        });
    }
}