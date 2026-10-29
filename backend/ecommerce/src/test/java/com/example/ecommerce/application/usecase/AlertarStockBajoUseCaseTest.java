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
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AlertarStockBajoUseCaseTest {
    private Articulo crearArticulo(String id, int cantidad) {
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
                Tienda.reconstruir("t-1", "Tienda prueba", "", "v-1", EstadoTienda.ACTIVA)
        );
    }

    @Test
    void debeAlertarCuandoElStockEsMenorQueCinco() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();
        AlertarStockBajoUseCase useCase =
                new AlertarStockBajoUseCase(repository);

        Articulo articulo = crearArticulo("a-1", 4);
        repository.guardar(articulo);

        List<Articulo> resultado = useCase.ejecutar();

        assertEquals(1, resultado.size());
        assertEquals("a-1", resultado.get(0).getId());
        assertEquals(4, resultado.get(0).getCantidadDisponible());
    }

    @Test
    void noDebeAlertarCuandoElStockEsIgualACinco() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();
        AlertarStockBajoUseCase useCase =
                new AlertarStockBajoUseCase(repository);

        repository.guardar(crearArticulo("a-1", 5));

        List<Articulo> resultado = useCase.ejecutar();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void noDebeAlertarCuandoElStockEsMayorQueCinco() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();
        AlertarStockBajoUseCase useCase =
                new AlertarStockBajoUseCase(repository);

        repository.guardar(crearArticulo("a-1", 10));

        List<Articulo> resultado = useCase.ejecutar();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void debeAlertarSoloPorLosArticulosConStockBajo() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();
        AlertarStockBajoUseCase useCase =
                new AlertarStockBajoUseCase(repository);

        repository.guardar(crearArticulo("a-1", 2));
        repository.guardar(crearArticulo("a-2", 5));
        repository.guardar(crearArticulo("a-3", 8));

        List<Articulo> resultado = useCase.ejecutar();

        assertEquals(1, resultado.size());
        assertEquals("a-1", resultado.get(0).getId());
    }
}
