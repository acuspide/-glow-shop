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
import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuitarArticuloDelCarritoUseCaseTest {

    @Mock
    private CarritoRepository carritoRepository;

    @InjectMocks
    private QuitarArticuloDelCarritoUseCase useCase;

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
                Tienda.reconstruir("t-1", "Tienda Beauty", "", "v-1", EstadoTienda.ACTIVA)
        );
        articulo.publicar();
        return articulo;
    }

    @Test
    void deberiaQuitarElArticuloCuandoEstaEnElCarrito() {

        // Arrange
        Carrito carrito = Carrito.crear("c-1", "u-100");
        carrito.agregarArticulo(crearArticuloPublicado("a-1", "20000"), 2);
        carrito.agregarArticulo(crearArticuloPublicado("a-2", "50000"), 1);
        when(carritoRepository.obtenerPorClienteId("u-100")).thenReturn(Optional.of(carrito));

        // Act
        useCase.ejecutar("u-100", "a-1");

        // Assert
        assertEquals(1, carrito.getItems().size());
        assertEquals("a-2", carrito.getItems().get(0).getArticuloId());
        verify(carritoRepository).guardar(carrito);
    }

    @Test
    void deberiaLanzarExcepcionCuandoElClienteNoTieneCarrito() {

        // Arrange
        when(carritoRepository.obtenerPorClienteId("u-100")).thenReturn(Optional.empty());

        // Act y Assert
        assertThrows(RecursoNoEncontradoException.class, () -> {
            useCase.ejecutar("u-100", "a-1");
        });

        verify(carritoRepository, never()).guardar(any());
    }

    @Test
    void deberiaLanzarExcepcionCuandoElArticuloNoEstaEnElCarrito() {

        // Arrange
        Carrito carrito = Carrito.crear("c-1", "u-100");
        carrito.agregarArticulo(crearArticuloPublicado("a-1", "20000"), 2);
        when(carritoRepository.obtenerPorClienteId("u-100")).thenReturn(Optional.of(carrito));

        // Act y Assert
        assertThrows(ArticuloNoEncontradoEnCarritoException.class, () -> {
            useCase.ejecutar("u-100", "a-99");
        });

        assertEquals(1, carrito.getItems().size());
        verify(carritoRepository, never()).guardar(any());
    }
}