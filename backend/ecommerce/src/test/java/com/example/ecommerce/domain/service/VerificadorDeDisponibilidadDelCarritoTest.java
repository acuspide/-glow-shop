package com.example.ecommerce.domain.service;

import com.example.ecommerce.domain.entity.*;
import com.example.ecommerce.domain.exception.ArticuloAgotadoException;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
import com.example.ecommerce.domain.exception.CantidadSuperaStockException;
import com.example.ecommerce.domain.exception.CarritoVacioException;
import com.example.ecommerce.domain.exception.PrecioDelArticuloCambioException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.valueobject.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VerificadorDeDisponibilidadDelCarritoTest {

    private final ArticuloRepository articuloRepository = mock(ArticuloRepository.class);
    private final VerificadorDeDisponibilidadDelCarrito verificador =
            new VerificadorDeDisponibilidadDelCarrito(articuloRepository);

    private Articulo articulo(String id, String precio, int stock) {
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

    // Carrito de Ana con 3 labiales agregados a $20.000.
    private Carrito carritoConTresLabiales() {
        Carrito carrito = Carrito.crear("c-1", "u-100");
        carrito.agregarArticulo(articulo("a-1", "20000", 10), 3);
        return carrito;
    }

    @Test
    void unCarritoConArticulosDisponiblesYPreciosVigentesPuedePagarse() {
        when(articuloRepository.obtenerPorId("a-1")).thenReturn(Optional.of(articulo("a-1", "20000", 10)));

        assertDoesNotThrow(() -> verificador.verificarQuePuedePagarse(carritoConTresLabiales()));
    }

    @Test
    void unCarritoVacioNoPuedePagarse() {
        Carrito vacio = Carrito.crear("c-1", "u-100");

        assertThrows(CarritoVacioException.class, () -> verificador.verificarQuePuedePagarse(vacio));
    }

    @Test
    void noPuedePagarseSiElArticuloSeAgoto() {
        when(articuloRepository.obtenerPorId("a-1")).thenReturn(Optional.of(articulo("a-1", "20000", 0)));

        assertThrows(ArticuloAgotadoException.class,
                () -> verificador.verificarQuePuedePagarse(carritoConTresLabiales()));
    }

    @Test
    void noPuedePagarseSiYaNoHayStockSuficiente() {
        when(articuloRepository.obtenerPorId("a-1")).thenReturn(Optional.of(articulo("a-1", "20000", 2)));

        assertThrows(CantidadSuperaStockException.class,
                () -> verificador.verificarQuePuedePagarse(carritoConTresLabiales()));
    }

    @Test
    void noPuedePagarseSiElPrecioCambio() {
        when(articuloRepository.obtenerPorId("a-1")).thenReturn(Optional.of(articulo("a-1", "25000", 10)));

        assertThrows(PrecioDelArticuloCambioException.class,
                () -> verificador.verificarQuePuedePagarse(carritoConTresLabiales()));
    }

    @Test
    void noPuedePagarseSiElArticuloYaNoExiste() {
        when(articuloRepository.obtenerPorId("a-1")).thenReturn(Optional.empty());

        assertThrows(ArticuloNoEncontradoException.class,
                () -> verificador.verificarQuePuedePagarse(carritoConTresLabiales()));
    }
}
