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

    private Articulo crearArticuloPublicado(long id, String precio) {
        Articulo articulo = Articulo.crear(
                id,
                new NombreArticulo("Labial"),
                new Precio(new BigDecimal(precio)),
                Categoria.crear(1L, "Labios"),
                Marca.crear(1L, "Bloom"),
                Tono.CLARO,
                List.of(TipoPiel.NORMAL),
                Inventario.crear(10),
                new FechaVencimiento(LocalDate.now().plusYears(1)),
                Tienda.crear(1L, "Tienda Beauty")
        );
        articulo.publicar();
        return articulo;
    }

    @Test
    void debeMostrarLosArticulosYElTotalDelCarritoDelCliente() {

        // Arrange
        CarritoRepository carritoRepository = new CarritoRepositoryEnMemoria();
        VerCarritoUseCase useCase = new VerCarritoUseCase(carritoRepository);

        Carrito carritoDeAna = Carrito.crear(1L, 100L);
        carritoDeAna.agregarArticulo(crearArticuloPublicado(1L, "20000"), 2);
        carritoDeAna.agregarArticulo(crearArticuloPublicado(2L, "50000"), 1);
        carritoRepository.guardar(carritoDeAna);

        // Act
        Carrito carrito = useCase.ejecutar(100L);

        // Assert
        assertEquals(2, carrito.getItems().size());
        assertEquals(new BigDecimal("90000"), carrito.calcularTotal());
    }

    @Test
    void debeMostrarSoloElCarritoDelClienteIndicado() {

        // Arrange
        CarritoRepository carritoRepository = new CarritoRepositoryEnMemoria();
        VerCarritoUseCase useCase = new VerCarritoUseCase(carritoRepository);
        carritoRepository.guardar(Carrito.crear(1L, 100L));
        carritoRepository.guardar(Carrito.crear(2L, 200L));

        // Act
        Carrito carrito = useCase.ejecutar(200L);

        // Assert
        assertEquals(200L, carrito.getClienteId());
    }

    @Test
    void debeLanzarErrorSiElClienteNoTieneCarrito() {

        // Arrange
        CarritoRepository carritoRepository = new CarritoRepositoryEnMemoria();
        VerCarritoUseCase useCase = new VerCarritoUseCase(carritoRepository);

        // Act y Assert
        assertThrows(NoSuchElementException.class, () -> {
            useCase.ejecutar(100L);
        });
    }
}
