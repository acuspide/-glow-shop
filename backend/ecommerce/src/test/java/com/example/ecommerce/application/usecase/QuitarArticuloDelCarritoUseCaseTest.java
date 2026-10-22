package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.*;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoEnCarritoException;
import com.example.ecommerce.domain.repository.CarritoRepository;
import com.example.ecommerce.domain.valueobject.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuitarArticuloDelCarritoUseCaseTest {

    @Mock
    private CarritoRepository carritoRepository;

    @InjectMocks
    private QuitarArticuloDelCarritoUseCase useCase;

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
    void deberiaQuitarElArticuloCuandoEstaEnElCarrito() {

        // Arrange
        Carrito carrito = Carrito.crear(1L, "u-100");
        carrito.agregarArticulo(crearArticuloPublicado(1L, "20000"), 2);
        carrito.agregarArticulo(crearArticuloPublicado(2L, "50000"), 1);
        when(carritoRepository.obtenerPorClienteId("u-100")).thenReturn(Optional.of(carrito));

        // Act
        useCase.ejecutar("u-100", 1L);

        // Assert
        assertEquals(1, carrito.getItems().size());
        assertEquals(2L, carrito.getItems().get(0).getArticuloId());
        verify(carritoRepository).guardar(carrito);
    }

    @Test
    void deberiaLanzarExcepcionCuandoElClienteNoTieneCarrito() {

        // Arrange
        when(carritoRepository.obtenerPorClienteId("u-100")).thenReturn(Optional.empty());

        // Act y Assert
        assertThrows(NoSuchElementException.class, () -> {
            useCase.ejecutar("u-100", 1L);
        });

        verify(carritoRepository, never()).guardar(any());
    }

    @Test
    void deberiaLanzarExcepcionCuandoElArticuloNoEstaEnElCarrito() {

        // Arrange
        Carrito carrito = Carrito.crear(1L, "u-100");
        carrito.agregarArticulo(crearArticuloPublicado(1L, "20000"), 2);
        when(carritoRepository.obtenerPorClienteId("u-100")).thenReturn(Optional.of(carrito));

        // Act y Assert
        assertThrows(ArticuloNoEncontradoEnCarritoException.class, () -> {
            useCase.ejecutar("u-100", 99L);
        });

        assertEquals(1, carrito.getItems().size());
        verify(carritoRepository, never()).guardar(any());
    }
}