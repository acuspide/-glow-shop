package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.*;
import com.example.ecommerce.domain.exception.CantidadSuperaStockException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
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
import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModificarCantidadEnCarritoUseCaseTest {

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private ArticuloRepository articuloRepository;

    @InjectMocks
    private ModificarCantidadEnCarritoUseCase useCase;

    private Articulo crearArticuloPublicado(String id, int stock) {
        Articulo articulo = Articulo.crear(
                id,
                new NombreArticulo("Labial"),
                new Precio(new BigDecimal("20000")),
                Categoria.crear("cat-1", "Labios"),
                Marca.crear("m-1", "Bloom"),
                Tono.CLARO,
                List.of(TipoPiel.NORMAL),
                Inventario.crear(stock),
                new FechaVencimiento(LocalDate.now().plusYears(1)),
                Tienda.reconstruir("t-1", "Tienda Beauty", "", "v-1", EstadoTienda.ACTIVA)
        );
        articulo.publicar();
        return articulo;
    }

    @Test
    void deberiaModificarLaCantidadCuandoElArticuloEstaEnElCarrito() {

        // Arrange
        Articulo labial = crearArticuloPublicado("a-1", 10);
        Carrito carrito = Carrito.crear("c-1", "u-100");
        carrito.agregarArticulo(labial, 2);
        when(carritoRepository.obtenerPorClienteId("u-100")).thenReturn(Optional.of(carrito));
        when(articuloRepository.obtenerPorId("a-1")).thenReturn(Optional.of(labial));

        // Act
        useCase.ejecutar("u-100", "a-1", 5);

        // Assert
        assertEquals(5, carrito.getItems().get(0).getCantidad());
        verify(carritoRepository).guardar(carrito);
    }

    @Test
    void deberiaLanzarExcepcionCuandoElClienteNoTieneCarrito() {

        // Arrange
        when(carritoRepository.obtenerPorClienteId("u-100")).thenReturn(Optional.empty());

        // Act y Assert
        assertThrows(RecursoNoEncontradoException.class, () -> {
            useCase.ejecutar("u-100", "a-1", 5);
        });

        verify(carritoRepository, never()).guardar(any());
    }

    @Test
    void deberiaLanzarExcepcionCuandoElArticuloNoExiste() {

        // Arrange
        when(carritoRepository.obtenerPorClienteId("u-100")).thenReturn(Optional.of(Carrito.crear("c-1", "u-100")));
        when(articuloRepository.obtenerPorId("a-99")).thenReturn(Optional.empty());

        // Act y Assert
        assertThrows(RecursoNoEncontradoException.class, () -> {
            useCase.ejecutar("u-100", "a-99", 5);
        });

        verify(carritoRepository, never()).guardar(any());
    }

    @Test
    void deberiaLanzarExcepcionCuandoLaNuevaCantidadSuperaElStock() {

        // Arrange
        Articulo labial = crearArticuloPublicado("a-1", 10);
        Carrito carrito = Carrito.crear("c-1", "u-100");
        carrito.agregarArticulo(labial, 2);
        when(carritoRepository.obtenerPorClienteId("u-100")).thenReturn(Optional.of(carrito));
        when(articuloRepository.obtenerPorId("a-1")).thenReturn(Optional.of(labial));

        // Act y Assert
        assertThrows(CantidadSuperaStockException.class, () -> {
            useCase.ejecutar("u-100", "a-1", 11);
        });

        assertEquals(2, carrito.getItems().get(0).getCantidad());
        verify(carritoRepository, never()).guardar(any());
    }
}
