package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.Categoria;
import com.example.ecommerce.domain.entity.Marca;
import com.example.ecommerce.domain.entity.Inventario;
import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.domain.valueobject.EstadoTienda;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.valueobject.FechaVencimiento;
import com.example.ecommerce.domain.valueobject.NombreArticulo;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.domain.valueobject.TipoPiel;
import com.example.ecommerce.domain.valueobject.Tono;
import com.example.ecommerce.infrastructure.persistence.ArticuloRepositoryEnMemoria;
import com.example.ecommerce.infrastructure.persistence.TiendaRepositoryEnMemoria;
import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AlertarStockBajoUseCaseTest {
    private static final Tienda TIENDA_DE_VERA = Tienda.reconstruir("t-1", "Tienda prueba", "", "v-1", EstadoTienda.ACTIVA);
    private static final Tienda TIENDA_DE_LINA = Tienda.reconstruir("t-2", "Otra tienda", "", "v-2", EstadoTienda.ACTIVA);

    private TiendaRepositoryEnMemoria tiendas() {
        TiendaRepositoryEnMemoria tiendas = new TiendaRepositoryEnMemoria();
        tiendas.guardar(TIENDA_DE_VERA);
        tiendas.guardar(TIENDA_DE_LINA);
        return tiendas;
    }

    private Articulo crearArticulo(String id, int cantidad) {
        return crearArticulo(id, cantidad, TIENDA_DE_VERA);
    }

    private Articulo crearArticulo(String id, int cantidad, Tienda tienda) {
        return Articulo.crear(
                id,
                new NombreArticulo("Shampoo"),
                new Precio(new BigDecimal("25000")),
                Categoria.crear("cat-1", "Cuidado capilar"),
                Marca.crear("m-1", "Marca prueba"),
                Tono.MEDIO,
                List.of(TipoPiel.NORMAL),
                Inventario.crear(cantidad),
                new FechaVencimiento(LocalDate.of(2030, 12, 31)),
                tienda
        );
    }

    @Test
    void debeAlertarCuandoElStockEsMenorQueCinco() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();
        AlertarStockBajoUseCase useCase =
                new AlertarStockBajoUseCase(repository, tiendas());

        Articulo articulo = crearArticulo("a-1", 4);
        repository.guardar(articulo);

        List<Articulo> resultado = useCase.ejecutar("v-1");

        assertEquals(1, resultado.size());
        assertEquals("a-1", resultado.get(0).getId());
        assertEquals(4, resultado.get(0).getCantidadDisponible());
    }

    @Test
    void noDebeAlertarCuandoElStockEsIgualACinco() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();
        AlertarStockBajoUseCase useCase =
                new AlertarStockBajoUseCase(repository, tiendas());

        repository.guardar(crearArticulo("a-1", 5));

        List<Articulo> resultado = useCase.ejecutar("v-1");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void noDebeAlertarCuandoElStockEsMayorQueCinco() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();
        AlertarStockBajoUseCase useCase =
                new AlertarStockBajoUseCase(repository, tiendas());

        repository.guardar(crearArticulo("a-1", 10));

        List<Articulo> resultado = useCase.ejecutar("v-1");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void debeAlertarSoloPorLosArticulosConStockBajo() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();
        AlertarStockBajoUseCase useCase =
                new AlertarStockBajoUseCase(repository, tiendas());

        repository.guardar(crearArticulo("a-1", 2));
        repository.guardar(crearArticulo("a-2", 5));
        repository.guardar(crearArticulo("a-3", 8));

        List<Articulo> resultado = useCase.ejecutar("v-1");

        assertEquals(1, resultado.size());
        assertEquals("a-1", resultado.get(0).getId());
    }

    @Test
    void noDebeAlertarPorArticulosDeOtraTienda() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();
        AlertarStockBajoUseCase useCase = new AlertarStockBajoUseCase(repository, tiendas());

        repository.guardar(crearArticulo("a-1", 2));
        repository.guardar(crearArticulo("a-2", 1, TIENDA_DE_LINA));

        List<Articulo> resultado = useCase.ejecutar("v-1");

        assertEquals(1, resultado.size());
        assertEquals("a-1", resultado.get(0).getId());
    }

    @Test
    void unVendedorSinTiendaNoTieneInventario() {
        AlertarStockBajoUseCase useCase =
                new AlertarStockBajoUseCase(new ArticuloRepositoryEnMemoria(), tiendas());

        assertThrows(RecursoNoEncontradoException.class, () -> useCase.ejecutar("v-sin-tienda"));
    }
}
