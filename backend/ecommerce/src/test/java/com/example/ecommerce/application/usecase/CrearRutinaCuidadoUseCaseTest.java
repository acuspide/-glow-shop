package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.*;
import com.example.ecommerce.domain.repository.RutinaCuidadoRepository;
import com.example.ecommerce.domain.valueobject.FechaVencimiento;
import com.example.ecommerce.domain.valueobject.NombreArticulo;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.domain.valueobject.TipoPiel;
import com.example.ecommerce.domain.valueobject.Tono;
import com.example.ecommerce.infrastructure.persistence.RutinaCuidadoRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


public class CrearRutinaCuidadoUseCaseTest {
    private Articulo crearArticulo(long id, int cantidad) {
        return new Articulo(
                id,
                new NombreArticulo("Shampoo"),
                new Precio(new BigDecimal("25000")),
                new Categoria(1L, "Cuidado capilar"),
                new Marca(1L, "Marca prueba"),
                Tono.MEDIO,
                List.of(TipoPiel.NORMAL),
                new Inventario(cantidad),
                new FechaVencimiento(LocalDate.of(2030, 12, 31)),
                new Tienda(1L, "Tienda prueba")
        );
    }

    @Test
    void debeCrearYGuardarRutinaDeCuidado() {

        // Arrange
        RutinaCuidadoRepository repository =
                new RutinaCuidadoRepositoryEnMemoria();

        CrearRutinaCuidadoUseCase useCase =
                new CrearRutinaCuidadoUseCase(repository);

        Articulo articulo1 = crearArticulo(1L, 10);
        Articulo articulo2 = crearArticulo(2L, 5);

        articulo1.publicar();
        articulo2.publicar();

        // Act
        useCase.ejecutar(
                1L,
                "Rutina de cuidado diario",
                List.of(articulo1, articulo2)
        );

        // Assert
        Optional<RutinaCuidado> rutina =
                repository.obtenerPorId(1L);

        assertTrue(rutina.isPresent());
        assertEquals(1L, rutina.get().getId());
        assertEquals("Rutina de cuidado diario", rutina.get().getNombre());
        assertEquals(2, rutina.get().getArticulos().size());
    }
}

