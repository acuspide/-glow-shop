package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.*;
import com.example.ecommerce.domain.repository.CarritoRepository;
import com.example.ecommerce.domain.valueobject.*;
import com.example.ecommerce.infrastructure.persistence.CarritoRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VerCarritoUseCaseTest {

    private Articulo crearArticuloPublicado(String id, String precio) {
        Articulo articulo = Articulo.crear(
                id,
                new NombreArticulo("Labial"),
                new Precio(new BigDecimal(precio)),
                Categoria.crear("cat-1", "Labios"),
                Marca.crear("m-1", "Bloom"),
                Tono.CLARO,
                List.of(TipoPiel.NORMAL),
                Inventario.crear(10),
                new FechaVencimiento(LocalDate.now().plusYears(1)),
                Tienda.crear("t-1", "Tienda Beauty")
        );
        articulo.publicar();
        return articulo;
    }

    @Test
    void debeMostrarLosArticulosYElTotalDelCarritoDelCliente() {

        // Arrange
        CarritoRepository carritoRepository = new CarritoRepositoryEnMemoria();
        VerCarritoUseCase useCase = new VerCarritoUseCase(carritoRepository);

        Carrito carritoDeAna = Carrito.crear("c-1", "u-100");
        carritoDeAna.agregarArticulo(crearArticuloPublicado("a-1", "20000"), 2);
        carritoDeAna.agregarArticulo(crearArticuloPublicado("a-2", "50000"), 1);
        carritoRepository.guardar(carritoDeAna);

        // Act
        Carrito carrito = useCase.ejecutar("u-100");

        // Assert
        assertEquals(2, carrito.getItems().size());
        assertEquals(new BigDecimal("90000"), carrito.calcularTotal());
    }

    @Test
    void debeMostrarSoloElCarritoDelClienteIndicado() {

        // Arrange
        CarritoRepository carritoRepository = new CarritoRepositoryEnMemoria();
        VerCarritoUseCase useCase = new VerCarritoUseCase(carritoRepository);
        carritoRepository.guardar(Carrito.crear("c-1", "u-100"));
        carritoRepository.guardar(Carrito.crear("c-2", "u-200"));

        // Act
        Carrito carrito = useCase.ejecutar("u-200");

        // Assert
        assertEquals("u-200", carrito.getClienteId());
    }

    @Test
    void debeLanzarErrorSiElClienteNoTieneCarrito() {

        // Arrange
        CarritoRepository carritoRepository = new CarritoRepositoryEnMemoria();
        VerCarritoUseCase useCase = new VerCarritoUseCase(carritoRepository);

        // Act y Assert
        assertThrows(NoSuchElementException.class, () -> {
            useCase.ejecutar("u-100");
        });
    }
}
