package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.*;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.valueobject.*;
import com.example.ecommerce.infrastructure.persistence.ArticuloRepositoryEnMemoria;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BuscarArticulosUseCaseTest {
    private Articulo crearArticulo(
            long id,
            String nombre,
            String categoria,
            long categoriaId,
            String marca,
            long marcaId,
            String tienda,
            long tiendaId,
            String precio,
            Tono tono,
            TipoPiel tipoPiel,
            int cantidad
    ) {
        return Articulo.crear(
                id,
                new NombreArticulo(nombre),
                new Precio(new BigDecimal(precio)),
                Categoria.crear(categoriaId, categoria),
                Marca.crear(marcaId, marca),
                tono,
                List.of(tipoPiel),
                Inventario.crear(cantidad),
                new FechaVencimiento(LocalDate.of(2028, 12, 31)),
                Tienda.crear(tiendaId, tienda)
        );
    }

    private ArticuloRepository crearRepositorioConArticulos() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();

        Articulo labial = crearArticulo(
                1L, "Labial rojo", "Labios", 1L,
                "Maybelline", 1L, "Tienda Beauty", 1L,
                "35000", Tono.CLARO, TipoPiel.NORMAL, 10
        );

        Articulo crema = crearArticulo(
                2L, "Crema facial", "Rostro", 2L,
                "Nivea", 2L, "Tienda Beauty", 1L,
                "45000", Tono.MEDIO, TipoPiel.SECA, 5
        );

        Articulo base = crearArticulo(
                3L, "Base líquida", "Rostro", 2L,
                "Maybelline", 1L, "Tienda Glam", 2L,
                "50000", Tono.OSCURO, TipoPiel.GRASA, 8
        );

        labial.publicar();
        crema.publicar();
        base.publicar();

        repository.guardar(labial);
        repository.guardar(crema);
        repository.guardar(base);

        return repository;
    }

    @Test
    void debeDevolverArticulosDisponiblesCuandoNoHayFiltros() {
        ArticuloRepository repository = crearRepositorioConArticulos();
        BuscarArticulosUseCase useCase =
                new BuscarArticulosUseCase(repository);

        CriteriosBusquedaArticulo criterios =
                new CriteriosBusquedaArticulo(
                        null, null, null, null,
                        null, null, null, null
                );

        List<Articulo> resultado = useCase.ejecutar(criterios);

        assertEquals(3, resultado.size());
    }

    @Test
    void debeBuscarPorTextoIgnorandoMayusculas() {
        ArticuloRepository repository = crearRepositorioConArticulos();
        BuscarArticulosUseCase useCase =
                new BuscarArticulosUseCase(repository);

        CriteriosBusquedaArticulo criterios =
                new CriteriosBusquedaArticulo(
                        "LABIAL", null, null, null,
                        null, null, null, null
                );

        List<Articulo> resultado = useCase.ejecutar(criterios);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
    }

    @Test
    void debeFiltrarPorCategoria() {
        ArticuloRepository repository = crearRepositorioConArticulos();
        BuscarArticulosUseCase useCase =
                new BuscarArticulosUseCase(repository);

        CriteriosBusquedaArticulo criterios =
                new CriteriosBusquedaArticulo(
                        null, 2L, null, null,
                        null, null, null, null
                );

        List<Articulo> resultado = useCase.ejecutar(criterios);

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream()
                .allMatch(a -> a.getCategoria().getId() == 2L));
    }

    @Test
    void debeFiltrarPorRangoDePrecioIncluyendoLosLimites() {
        ArticuloRepository repository = crearRepositorioConArticulos();
        BuscarArticulosUseCase useCase =
                new BuscarArticulosUseCase(repository);

        CriteriosBusquedaArticulo criterios =
                new CriteriosBusquedaArticulo(
                        null, null, null, null,
                        new BigDecimal("35000"),
                        new BigDecimal("50000"),
                        null, null
                );

        List<Articulo> resultado = useCase.ejecutar(criterios);

        assertEquals(3, resultado.size());
    }

    @Test
    void debeCombinarCategoriaYMarca() {
        ArticuloRepository repository = crearRepositorioConArticulos();
        BuscarArticulosUseCase useCase =
                new BuscarArticulosUseCase(repository);

        CriteriosBusquedaArticulo criterios =
                new CriteriosBusquedaArticulo(
                        null, 2L, 1L, null,
                        null, null, null, null
                );

        List<Articulo> resultado = useCase.ejecutar(criterios);

        assertEquals(1, resultado.size());
        assertEquals(3L, resultado.get(0).getId());
    }

    @Test
    void debeFiltrarPorTonoYTipoDePiel() {
        ArticuloRepository repository = crearRepositorioConArticulos();
        BuscarArticulosUseCase useCase =
                new BuscarArticulosUseCase(repository);

        CriteriosBusquedaArticulo criterios =
                new CriteriosBusquedaArticulo(
                        null, null, null, null,
                        null, null, Tono.MEDIO, TipoPiel.SECA
                );

        List<Articulo> resultado = useCase.ejecutar(criterios);

        assertEquals(1, resultado.size());
        assertEquals(2L, resultado.get(0).getId());
    }
    @Test
    void noDebeDevolverArticulosSinPublicar() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();

        Articulo articulo = crearArticulo(
                4L, "Labial sin publicar", "Labios", 1L,
                "Maybelline", 1L, "Tienda Beauty", 1L,
                "30000", Tono.CLARO, TipoPiel.NORMAL, 10
        );

        // No llamamos a publicar()
        repository.guardar(articulo);

        BuscarArticulosUseCase useCase =
                new BuscarArticulosUseCase(repository);

        CriteriosBusquedaArticulo criterios =
                new CriteriosBusquedaArticulo(
                        null, null, null, null,
                        null, null, null, null
                );

        List<Articulo> resultado = useCase.ejecutar(criterios);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void noDebeDevolverArticulosAgotados() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();

        Articulo articulo = crearArticulo(
                5L, "Crema agotada", "Rostro", 2L,
                "Nivea", 2L, "Tienda Beauty", 1L,
                "45000", Tono.MEDIO, TipoPiel.SECA, 0
        );

        articulo.publicar();
        repository.guardar(articulo);

        BuscarArticulosUseCase useCase =
                new BuscarArticulosUseCase(repository);

        CriteriosBusquedaArticulo criterios =
                new CriteriosBusquedaArticulo(
                        null, null, null, null,
                        null, null, null, null
                );

        List<Articulo> resultado = useCase.ejecutar(criterios);

        assertTrue(resultado.isEmpty());
    }


    @Test
    void noDebeDevolverArticulosVencidos() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();

        Articulo articulo = Articulo.crear(
                6L,
                new NombreArticulo("Base vencida"),
                new Precio(new BigDecimal("50000")),
                Categoria.crear(2L, "Rostro"),
                Marca.crear(1L, "Maybelline"),
                Tono.OSCURO,
                List.of(TipoPiel.GRASA),
                Inventario.crear(8),
                new FechaVencimiento(LocalDate.of(2025, 12, 31)),
                Tienda.crear(2L, "Tienda Glam")
        );

        // Guardamos el artículo vencido sin intentar publicarlo.
        repository.guardar(articulo);

        BuscarArticulosUseCase useCase =
                new BuscarArticulosUseCase(repository);

        CriteriosBusquedaArticulo criterios =
                new CriteriosBusquedaArticulo(
                        null, null, null, null,
                        null, null, null, null
                );

        List<Articulo> resultado = useCase.ejecutar(criterios);

        assertTrue(resultado.isEmpty());
    }

}
