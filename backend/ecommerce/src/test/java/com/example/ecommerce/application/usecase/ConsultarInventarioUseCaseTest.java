package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.*;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
import com.example.ecommerce.domain.valueobject.*;
import com.example.ecommerce.infrastructure.persistence.ArticuloRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConsultarInventarioUseCaseTest {
    @Test
    void debeConsultarLaCantidadDisponibleDeUnArticulo() {

        // Arrange
        ArticuloRepositoryEnMemoria repository =
                new ArticuloRepositoryEnMemoria();

        Articulo articulo = crearArticulo("a-1", 10);
        repository.guardar(articulo);

        ConsultarInventarioUseCase useCase =
                new ConsultarInventarioUseCase(repository);

        // Act
        Articulo resultado = useCase.ejecutar("a-1");

        // Assert
        assertNotNull(resultado);
        assertEquals("a-1", resultado.getId());
        assertEquals("Labial", resultado.getNombre().valor());
        assertEquals(10, resultado.getCantidadDisponible());
    }

    @Test
    void debeConsultarUnInventarioAgotado() {

        // Arrange
        ArticuloRepositoryEnMemoria repository =
                new ArticuloRepositoryEnMemoria();

        Articulo articulo = crearArticulo("a-2", 0);
        repository.guardar(articulo);

        ConsultarInventarioUseCase useCase =
                new ConsultarInventarioUseCase(repository);

        // Act
        Articulo resultado = useCase.ejecutar("a-2");

        // Assert
        assertEquals("a-2", resultado.getId());
        assertEquals("Labial", resultado.getNombre().valor());
        assertEquals(0, resultado.getCantidadDisponible());
    }

    @Test
    void debeLanzarExcepcionSiElArticuloNoExiste() {

        // Arrange
        ArticuloRepositoryEnMemoria repository =
                new ArticuloRepositoryEnMemoria();

        ConsultarInventarioUseCase useCase =
                new ConsultarInventarioUseCase(repository);

        // Act y Assert
        assertThrows(
                ArticuloNoEncontradoException.class,
                () -> useCase.ejecutar("a-999")
        );
    }

    private Articulo crearArticulo(String id, int cantidadDisponible) {
        return Articulo.crear(
                id,
                new NombreArticulo("Labial"),
                new Precio(new BigDecimal("20000")),
                Categoria.crear(1L, "Labios"),
                Marca.crear(1L, "Maybelline"),
                Tono.CLARO,
                List.of(TipoPiel.NORMAL),
                Inventario.crear(cantidadDisponible),
                new FechaVencimiento(LocalDate.of(2027, 12, 31)),
                Tienda.crear(1L, "Tienda Beauty")
        );
    }
}
