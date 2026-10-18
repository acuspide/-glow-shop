package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.*;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
import com.example.ecommerce.domain.exception.CantidadInvalidaException;
import com.example.ecommerce.domain.exception.ReglaDominioException;
import com.example.ecommerce.domain.valueobject.*;
import com.example.ecommerce.infrastructure.persistence.ArticuloRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ActualizarInventarioUseCaseTest {
    @Test
    void debeAumentarElInventarioDeUnArticulo() {

        // Arrange
        ArticuloRepositoryEnMemoria repository =
                new ArticuloRepositoryEnMemoria();

        Articulo articulo = Articulo.crear(
                1L,
                new NombreArticulo("Labial"),
                new Precio(new BigDecimal("20000")),
                Categoria.crear(1L, "Labios"),
                Marca.crear(1L, "Maybelline"),
                Tono.CLARO,
                List.of(TipoPiel.NORMAL),
                Inventario.crear(10),
                new FechaVencimiento(LocalDate.of(2027, 12, 31)),
                Tienda.crear(1L, "Tienda Beauty")
        );

        repository.guardar(articulo);

        ActualizarInventarioUseCase useCase =
                new ActualizarInventarioUseCase(repository);

        // Act
        useCase.aumentar(1L, 5);

        // Assert
        assertEquals(15, articulo.getCantidadDisponible());
    }
    @Test
    void debeDisminuirElInventarioDeUnArticulo() {

        // Arrange
        ArticuloRepositoryEnMemoria repository =
                new ArticuloRepositoryEnMemoria();

        Articulo articulo = Articulo.crear(
                2L,
                new NombreArticulo("Labial"),
                new Precio(new BigDecimal("20000")),
                Categoria.crear(1L, "Labios"),
                Marca.crear(1L, "Maybelline"),
                Tono.CLARO,
                List.of(TipoPiel.NORMAL),
                Inventario.crear(10),
                new FechaVencimiento(LocalDate.of(2027, 12, 31)),
                Tienda.crear(1L, "Tienda Beauty")
        );

        repository.guardar(articulo);

        ActualizarInventarioUseCase useCase =
                new ActualizarInventarioUseCase(repository);

        // Act
        useCase.disminuir(2L, 3);

        // Assert
        assertEquals(7, articulo.getCantidadDisponible());
    }
    @Test
    void noDebeActualizarInventarioSiElArticuloNoExiste() {

        // Arrange
        ArticuloRepositoryEnMemoria repository =
                new ArticuloRepositoryEnMemoria();

        ActualizarInventarioUseCase useCase =
                new ActualizarInventarioUseCase(repository);

        // Act y Assert
        assertThrows(ArticuloNoEncontradoException.class, () -> {
            useCase.aumentar(999L, 5);
        });
    }
    @Test
    void noDebePermitirDisminuirInventarioPorDebajoDeCero() {

        // Arrange
        ArticuloRepositoryEnMemoria repository =
                new ArticuloRepositoryEnMemoria();

        Articulo articulo = Articulo.crear(
                3L,
                new NombreArticulo("Labial"),
                new Precio(new BigDecimal("20000")),
                Categoria.crear(1L, "Labios"),
                Marca.crear(1L, "Maybelline"),
                Tono.CLARO,
                List.of(TipoPiel.NORMAL),
                Inventario.crear(5),
                new FechaVencimiento(LocalDate.of(2027, 12, 31)),
                Tienda.crear(1L, "Tienda Beauty")
        );

        repository.guardar(articulo);

        ActualizarInventarioUseCase useCase =
                new ActualizarInventarioUseCase(repository);

        // Act y Assert
        assertThrows(ReglaDominioException.class, () -> {
            useCase.disminuir(3L, 10);
        });

        // El inventario debe conservar su valor original
        assertEquals(5, articulo.getCantidadDisponible());
    }
    @Test
    void noDebePermitirActualizarInventarioConCantidadCero() {

        // Arrange
        ArticuloRepositoryEnMemoria repository =
                new ArticuloRepositoryEnMemoria();

        Articulo articulo = Articulo.crear(
                4L,
                new NombreArticulo("Labial"),
                new Precio(new BigDecimal("20000")),
                Categoria.crear(1L, "Labios"),
                Marca.crear(1L, "Maybelline"),
                Tono.CLARO,
                List.of(TipoPiel.NORMAL),
                Inventario.crear(10),
                new FechaVencimiento(LocalDate.of(2027, 12, 31)),
                Tienda.crear(1L, "Tienda Beauty")
        );

        repository.guardar(articulo);

        ActualizarInventarioUseCase useCase =
                new ActualizarInventarioUseCase(repository);

        // Act y Assert
        assertThrows(CantidadInvalidaException.class, () -> {
            useCase.aumentar(4L, 0);
        });

        // El inventario debe conservar su valor original
        assertEquals(10, articulo.getCantidadDisponible());
    }
}

