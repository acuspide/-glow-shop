package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.*;
import com.example.ecommerce.domain.exception.ArticuloNoPublicadoParaVentaException;
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
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgregarAlCarritoUseCaseTest {

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private ArticuloRepository articuloRepository;

    @InjectMocks
    private AgregarAlCarritoUseCase useCase;

    private Articulo crearArticulo(String id, boolean publicado) {
        Articulo articulo = Articulo.crear(
                id,
                new NombreArticulo("Labial"),
                new Precio(new BigDecimal("20000")),
                Categoria.crear("cat-1", "Labios"),
                Marca.crear("m-1", "Bloom"),
                Tono.OSCURO,
                List.of(TipoPiel.NORMAL),
                Inventario.crear(10),
                new FechaVencimiento(LocalDate.now().plusYears(1)),
                Tienda.crear("t-1", "Tienda Beauty")
        );
        if (publicado) {
            articulo.publicar();
        }
        return articulo;
    }

    @Test
    void deberiaCrearUnCarritoNuevoCuandoElClienteNoTieneCarrito() {

        // Arrange
        when(articuloRepository.obtenerPorId("a-1")).thenReturn(Optional.of(crearArticulo("a-1", true)));
        when(carritoRepository.obtenerPorClienteId("u-100")).thenReturn(Optional.empty());

        // Act
        Carrito carrito = useCase.ejecutar("c-1", "u-100", "a-1", 2);

        // Assert
        assertEquals("c-1", carrito.getId());
        assertEquals("u-100", carrito.getClienteId());
        assertEquals(new BigDecimal("40000"), carrito.calcularTotal());
        verify(carritoRepository).guardar(carrito);
    }

    @Test
    void deberiaUsarElCarritoExistenteDelCliente() {

        // Arrange
        Carrito carritoExistente = Carrito.crear("c-1", "u-100");
        carritoExistente.agregarArticulo(crearArticulo("a-1", true), 1);
        when(articuloRepository.obtenerPorId("a-2")).thenReturn(Optional.of(crearArticulo("a-2", true)));
        when(carritoRepository.obtenerPorClienteId("u-100")).thenReturn(Optional.of(carritoExistente));

        // Act
        Carrito carrito = useCase.ejecutar("c-2", "u-100", "a-2", 1);

        // Assert
        assertEquals("c-1", carrito.getId());
        assertEquals(2, carrito.getItems().size());
        verify(carritoRepository).guardar(carritoExistente);
    }

    @Test
    void deberiaLanzarExcepcionCuandoElArticuloNoExiste() {

        // Arrange
        when(articuloRepository.obtenerPorId("a-99")).thenReturn(Optional.empty());

        // Act y Assert
        assertThrows(NoSuchElementException.class, () -> {
            useCase.ejecutar("c-1", "u-100", "a-99", 1);
        });

        verify(carritoRepository, never()).guardar(any());
    }

    @Test
    void deberiaLanzarExcepcionCuandoElArticuloNoEstaPublicado() {

        // Arrange
        when(articuloRepository.obtenerPorId("a-1")).thenReturn(Optional.of(crearArticulo("a-1", false)));
        when(carritoRepository.obtenerPorClienteId("u-100")).thenReturn(Optional.empty());

        // Act y Assert
        assertThrows(ArticuloNoPublicadoParaVentaException.class, () -> {
            useCase.ejecutar("c-1", "u-100", "a-1", 1);
        });

        verify(carritoRepository, never()).guardar(any());
    }
}