package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.*;
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
class VaciarCarritoUseCaseTest {

    @Mock
    private CarritoRepository carritoRepository;

    @InjectMocks
    private VaciarCarritoUseCase useCase;

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
    void deberiaVaciarElCarritoCuandoElClienteTieneCarrito() {

        // Arrange
        Carrito carrito = Carrito.crear(1L, 100L);
        carrito.agregarArticulo(crearArticuloPublicado(1L, "20000"), 2);
        carrito.agregarArticulo(crearArticuloPublicado(2L, "50000"), 1);
        when(carritoRepository.obtenerPorClienteId(100L)).thenReturn(Optional.of(carrito));

        // Act
        useCase.ejecutar(100L);

        // Assert
        assertTrue(carrito.estaVacio());
        verify(carritoRepository).obtenerPorClienteId(100L);
        verify(carritoRepository).guardar(carrito);
    }

    @Test
    void deberiaLanzarExcepcionCuandoElClienteNoTieneCarrito() {

        // Arrange
        when(carritoRepository.obtenerPorClienteId(100L)).thenReturn(Optional.empty());

        // Act y Assert
        assertThrows(NoSuchElementException.class, () -> {
            useCase.ejecutar(100L);
        });

        verify(carritoRepository, never()).guardar(any());
    }
}
