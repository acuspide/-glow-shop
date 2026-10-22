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

        repository.guardar(articulo);

        ActualizarInventarioUseCase useCase =
                new ActualizarInventarioUseCase(repository);

        // Act
        useCase.aumentar("a-1", 5);

        // Assert
        assertEquals(15, articulo.getCantidadDisponible());
    }
    @Test
    void debeDisminuirElInventarioDeUnArticulo() {

        // Arrange
        ArticuloRepositoryEnMemoria repository =
                new ArticuloRepositoryEnMemoria();

        Articulo articulo = Articulo.crear(
                "a-2",
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

        repository.guardar(articulo);

        ActualizarInventarioUseCase useCase =
                new ActualizarInventarioUseCase(repository);

        // Act
        useCase.disminuir("a-2", 3);

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
            useCase.aumentar("a-999", 5);
        });
    }
    @Test
    void noDebePermitirDisminuirInventarioPorDebajoDeCero() {

        // Arrange
        ArticuloRepositoryEnMemoria repository =
                new ArticuloRepositoryEnMemoria();

        Articulo articulo = Articulo.crear(
                "a-3",
                new NombreArticulo("Labial"),
                new Precio(new BigDecimal("20000")),
                Categoria.crear("cat-1", "Labios"),
                Marca.crear("m-1", "Maybelline"),
                Tono.CLARO,
                List.of(TipoPiel.NORMAL),
                Inventario.crear(5),
                new FechaVencimiento(LocalDate.of(2027, 12, 31)),
                Tienda.crear("t-1", "Tienda Beauty")
        );

        repository.guardar(articulo);

        ActualizarInventarioUseCase useCase =
                new ActualizarInventarioUseCase(repository);

        // Act y Assert
        assertThrows(ReglaDominioException.class, () -> {
            useCase.disminuir("a-3", 10);
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
                "a-4",
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

        repository.guardar(articulo);

        ActualizarInventarioUseCase useCase =
                new ActualizarInventarioUseCase(repository);

        // Act y Assert
        assertThrows(CantidadInvalidaException.class, () -> {
            useCase.aumentar("a-4", 0);
        });

        // El inventario debe conservar su valor original
        assertEquals(10, articulo.getCantidadDisponible());
    }
}

