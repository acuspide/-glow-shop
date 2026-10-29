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
import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VaciarCarritoUseCaseTest {

    @Mock
    private CarritoRepository carritoRepository;

    @InjectMocks
    private VaciarCarritoUseCase useCase;

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
    void deberiaVaciarElCarritoCuandoElClienteTieneCarrito() {

        // Arrange
        Carrito carrito = Carrito.crear("c-1", "u-100");
        carrito.agregarArticulo(crearArticuloPublicado("a-1", "20000"), 2);
        carrito.agregarArticulo(crearArticuloPublicado("a-2", "50000"), 1);
        when(carritoRepository.obtenerPorClienteId("u-100")).thenReturn(Optional.of(carrito));

        // Act
        useCase.ejecutar("u-100");

        // Assert
        assertTrue(carrito.estaVacio());
        verify(carritoRepository).obtenerPorClienteId("u-100");
        verify(carritoRepository).guardar(carrito);
    }

    @Test
    void deberiaLanzarExcepcionCuandoElClienteNoTieneCarrito() {

        // Arrange
        when(carritoRepository.obtenerPorClienteId("u-100")).thenReturn(Optional.empty());

        // Act y Assert
        assertThrows(RecursoNoEncontradoException.class, () -> {
            useCase.ejecutar("u-100");
        });

        verify(carritoRepository, never()).guardar(any());
    }
}
