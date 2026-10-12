package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.*;
import com.example.ecommerce.domain.exception.ArticuloAgotadoException;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
import com.example.ecommerce.domain.exception.ArticuloNoPublicadoParaVentaException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.valueobject.*;
import com.example.ecommerce.infrastructure.persistence.ArticuloRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MostrarDetalleArticuloUseCaseTest {

    @Test
    void debeMostrarDetalleCuandoElArticuloExiste() {

        // Arrange
        ArticuloRepository repository =
                new ArticuloRepositoryEnMemoria();

        Articulo articulo = new Articulo(
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
        articulo.publicar();

        repository.guardar(articulo);

        MostrarDetalleArticuloUseCase useCase =
                new MostrarDetalleArticuloUseCase(repository);

        // Act
        Articulo resultado = useCase.ejecutar(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(articulo, resultado);
        assertEquals("Labial", resultado.getNombre().valor());
    }

    @Test
    void debeLanzarExcepcionCuandoElArticuloNoExiste() {

        // Arrange
        ArticuloRepository repository =
                new ArticuloRepositoryEnMemoria();

        MostrarDetalleArticuloUseCase useCase =
                new MostrarDetalleArticuloUseCase(repository);

        // Act y Assert
        assertThrows(
                ArticuloNoEncontradoException.class,
                () -> useCase.ejecutar(999L)
        );
    }

    @Test
    void debeLanzarExcepcionCuandoElArticuloNoEstaPublicado() {

        // Arrange
        ArticuloRepository repository =
                new ArticuloRepositoryEnMemoria();

        Articulo articulo = new Articulo(
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

        // No llamamos a articulo.publicar()
        repository.guardar(articulo);

        MostrarDetalleArticuloUseCase useCase =
                new MostrarDetalleArticuloUseCase(repository);

        // Act y Assert
        assertThrows(
                ArticuloNoPublicadoParaVentaException.class,
                () -> useCase.ejecutar(1L)
        );
    }

    @Test
    void debeLanzarExcepcionCuandoElArticuloEstaAgotado() {

        // Arrange
        ArticuloRepository repository =
                new ArticuloRepositoryEnMemoria();

        Articulo articulo = new Articulo(
                3L,
                new NombreArticulo("Labial agotado"),
                new Precio(new BigDecimal("20000")),
                new Categoria(1L, "Labios"),
                new Marca(1L, "Maybelline"),
                Tono.CLARO,
                List.of(TipoPiel.NORMAL),
                new Inventario(0),
                new FechaVencimiento(LocalDate.of(2027, 12, 31)),
                new Tienda(1L, "Tienda Beauty")
        );

        articulo.publicar();
        repository.guardar(articulo);

        MostrarDetalleArticuloUseCase useCase =
                new MostrarDetalleArticuloUseCase(repository);

        // Act y Assert
        assertThrows(
                ArticuloAgotadoException.class,
                () -> useCase.ejecutar(3L)
        );
    }




}
