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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
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

    // Entrega ids "p-1", "p-2"... como lo haría el controller con UUID.
    private Supplier<String> ids() {
        AtomicInteger contador = new AtomicInteger();
        return () -> "p-" + contador.incrementAndGet();
    }

    private Articulo crearArticuloPublicado(String id, String precio, int stock) {
        return crearArticuloPublicado(id, precio, stock, "t-1");
    }

    private Articulo crearArticuloPublicado(String id, String precio, int stock, String tiendaId) {
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
                Tienda.reconstruir(tiendaId, "Tienda " + tiendaId, "", "v-" + tiendaId, EstadoTienda.ACTIVA)
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
        List<Pedido> pedidos = useCase.ejecutar("u-100", ids());

        // Assert
        assertEquals(1, pedidos.size());
        Pedido pedido = pedidos.get(0);
        assertEquals("p-1", pedido.getId());
        assertEquals("t-1", pedido.getTiendaId());
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
        useCase.ejecutar("u-100", ids());

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
        useCase.ejecutar("u-100", ids());

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
            useCase.ejecutar("u-100", ids());
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
            useCase.ejecutar("u-100", ids());
        });

        assertFalse(carrito.estaVacio());
        assertEquals(10, labial.getCantidadDisponible());
        verifyNoInteractions(pedidoRepository, articuloRepository);
        verify(carritoRepository, never()).guardar(any());
    }

    @Test
    void deberiaCrearUnPedidoPorCadaTiendaDelCarrito() {

        // Arrange: dos artículos de la tienda t-1 y uno de la tienda t-2
        Articulo labial = crearArticuloPublicado("a-1", "20000", 10, "t-1");
        Articulo base = crearArticuloPublicado("a-2", "50000", 5, "t-2");
        Articulo rubor = crearArticuloPublicado("a-3", "15000", 5, "t-1");
        Carrito carrito = Carrito.crear("c-1", "u-100");
        carrito.agregarArticulo(labial, 2);
        carrito.agregarArticulo(base, 1);
        carrito.agregarArticulo(rubor, 1);
        when(carritoRepository.obtenerPorClienteId("u-100")).thenReturn(Optional.of(carrito));
        when(articuloRepository.obtenerPorId("a-1")).thenReturn(Optional.of(labial));
        when(articuloRepository.obtenerPorId("a-2")).thenReturn(Optional.of(base));
        when(articuloRepository.obtenerPorId("a-3")).thenReturn(Optional.of(rubor));

        // Act
        List<Pedido> pedidos = useCase.ejecutar("u-100", ids());

        // Assert
        assertEquals(2, pedidos.size());
        Pedido deT1 = pedidos.get(0);
        Pedido deT2 = pedidos.get(1);
        assertEquals("t-1", deT1.getTiendaId());
        assertEquals(List.of("a-1", "a-3"), deT1.getDetalles().stream().map(DetallePedido::getArticuloId).toList());
        assertEquals(new BigDecimal("55000"), deT1.calcularTotal());
        assertEquals("t-2", deT2.getTiendaId());
        assertEquals(new BigDecimal("50000"), deT2.calcularTotal());
        assertNotEquals(deT1.getId(), deT2.getId());
        assertTrue(pedidos.stream().allMatch(p -> p.getEstado() == EstadoPedido.CONFIRMADO));
        verify(pedidoRepository).guardar(deT1);
        verify(pedidoRepository).guardar(deT2);
        assertTrue(carrito.estaVacio());
    }
}
