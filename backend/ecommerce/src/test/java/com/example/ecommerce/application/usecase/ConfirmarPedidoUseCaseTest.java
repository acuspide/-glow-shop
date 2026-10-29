package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.*;
import com.example.ecommerce.domain.exception.ArticuloAgotadoException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.CarritoRepository;
import com.example.ecommerce.domain.repository.PedidoRepository;
import com.example.ecommerce.domain.service.VerificadorDeDisponibilidadDelCarrito;
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
class ConfirmarPedidoUseCaseTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private ArticuloRepository articuloRepository;

    @Mock
    private VerificadorDeDisponibilidadDelCarrito verificador;

    @InjectMocks
    private ConfirmarPedidoUseCase useCase;

    private Articulo crearArticuloPublicado(String id, String precio, int stock) {
        Articulo articulo = Articulo.crear(
                id,
                new NombreArticulo("Labial"),
                new Precio(new BigDecimal(precio)),
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
    void deberiaCrearYConfirmarElPedidoConLosArticulosDelCarrito() {

        // Arrange
        Articulo labial = crearArticuloPublicado("a-1", "20000", 10);
        Carrito carrito = Carrito.crear("c-1", "u-100");
        carrito.agregarArticulo(labial, 2);
        when(carritoRepository.obtenerPorClienteId("u-100")).thenReturn(Optional.of(carrito));
        when(articuloRepository.obtenerPorId("a-1")).thenReturn(Optional.of(labial));

        // Act
        Pedido pedido = useCase.ejecutar("p-1", "u-100");

        // Assert
        assertEquals("p-1", pedido.getId());
        assertEquals("u-100", pedido.getClienteId());
        assertEquals(EstadoPedido.CONFIRMADO, pedido.getEstado());
        assertEquals(1, pedido.getDetalles().size());
        assertEquals(new BigDecimal("40000"), pedido.calcularTotal());
        verify(verificador).verificarQuePuedePagarse(carrito);
        verify(pedidoRepository).guardar(pedido);
    }

    @Test
    void deberiaDescontarElInventarioDeCadaArticulo() {

        // Arrange
        Articulo labial = crearArticuloPublicado("a-1", "20000", 10);
        Articulo base = crearArticuloPublicado("a-2", "50000", 5);
        Carrito carrito = Carrito.crear("c-1", "u-100");
        carrito.agregarArticulo(labial, 2);
        carrito.agregarArticulo(base, 1);
        when(carritoRepository.obtenerPorClienteId("u-100")).thenReturn(Optional.of(carrito));
        when(articuloRepository.obtenerPorId("a-1")).thenReturn(Optional.of(labial));
        when(articuloRepository.obtenerPorId("a-2")).thenReturn(Optional.of(base));

        // Act
        useCase.ejecutar("p-1", "u-100");

        // Assert
        assertEquals(8, labial.getCantidadDisponible());
        assertEquals(4, base.getCantidadDisponible());
        verify(articuloRepository).guardar(labial);
        verify(articuloRepository).guardar(base);
    }

    @Test
    void deberiaVaciarElCarritoDespuesDeConfirmar() {

        // Arrange
        Articulo labial = crearArticuloPublicado("a-1", "20000", 10);
        Carrito carrito = Carrito.crear("c-1", "u-100");
        carrito.agregarArticulo(labial, 2);
        when(carritoRepository.obtenerPorClienteId("u-100")).thenReturn(Optional.of(carrito));
        when(articuloRepository.obtenerPorId("a-1")).thenReturn(Optional.of(labial));

        // Act
        useCase.ejecutar("p-1", "u-100");

        // Assert
        assertTrue(carrito.estaVacio());
        verify(carritoRepository).guardar(carrito);
    }

    @Test
    void deberiaLanzarExcepcionCuandoElClienteNoTieneCarrito() {

        // Arrange
        when(carritoRepository.obtenerPorClienteId("u-100")).thenReturn(Optional.empty());

        // Act y Assert
        assertThrows(RecursoNoEncontradoException.class, () -> {
            useCase.ejecutar("p-1", "u-100");
        });

        verify(verificador, never()).verificarQuePuedePagarse(any());
        verifyNoInteractions(pedidoRepository, articuloRepository);
    }

    @Test
    void noDebeModificarNadaSiElCarritoNoEstaDisponible() {

        // Arrange
        Articulo labial = crearArticuloPublicado("a-1", "20000", 10);
        Carrito carrito = Carrito.crear("c-1", "u-100");
        carrito.agregarArticulo(labial, 2);
        when(carritoRepository.obtenerPorClienteId("u-100")).thenReturn(Optional.of(carrito));
        doThrow(new ArticuloAgotadoException())
                .when(verificador).verificarQuePuedePagarse(carrito);

        // Act y Assert
        assertThrows(ArticuloAgotadoException.class, () -> {
            useCase.ejecutar("p-1", "u-100");
        });

        assertFalse(carrito.estaVacio());
        assertEquals(10, labial.getCantidadDisponible());
        verifyNoInteractions(pedidoRepository, articuloRepository);
        verify(carritoRepository, never()).guardar(any());
    }
}