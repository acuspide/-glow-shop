package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.Categoria;
import com.example.ecommerce.domain.entity.Marca;
import com.example.ecommerce.domain.entity.Inventario;
import com.example.ecommerce.domain.entity.Tienda;
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
    private Articulo crearArticulo(long id, int cantidad) {
        return Articulo.crear(
                id,
                new NombreArticulo("Shampoo"),
                new Precio(new BigDecimal("25000")),
                Categoria.crear(1L, "Cuidado capilar"),
                Marca.crear(1L, "Marca prueba"),
                Tono.MEDIO,
                List.of(TipoPiel.NORMAL),
                Inventario.crear(cantidad),
                new FechaVencimiento(LocalDate.of(2030, 12, 31)),
                Tienda.crear(1L, "Tienda prueba")
        );
    }

    @Test
    void debeAlertarCuandoElStockEsMenorQueCinco() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();
        AlertarStockBajoUseCase useCase =
                new AlertarStockBajoUseCase(repository);

        Articulo articulo = crearArticulo(1L, 4);
        repository.guardar(articulo);

        List<Articulo> resultado = useCase.ejecutar();

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(4, resultado.get(0).getCantidadDisponible());
    }

    @Test
    void noDebeAlertarCuandoElStockEsIgualACinco() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();
        AlertarStockBajoUseCase useCase =
                new AlertarStockBajoUseCase(repository);

        repository.guardar(crearArticulo(1L, 5));

        List<Articulo> resultado = useCase.ejecutar();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void noDebeAlertarCuandoElStockEsMayorQueCinco() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();
        AlertarStockBajoUseCase useCase =
                new AlertarStockBajoUseCase(repository);

        repository.guardar(crearArticulo(1L, 10));

        List<Articulo> resultado = useCase.ejecutar();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void debeAlertarSoloPorLosArticulosConStockBajo() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();
        AlertarStockBajoUseCase useCase =
                new AlertarStockBajoUseCase(repository);

        repository.guardar(crearArticulo(1L, 2));
        repository.guardar(crearArticulo(2L, 5));
        repository.guardar(crearArticulo(3L, 8));

        List<Articulo> resultado = useCase.ejecutar();

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
    }
}
