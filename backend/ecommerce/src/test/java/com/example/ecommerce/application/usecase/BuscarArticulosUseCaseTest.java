package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.*;
import com.example.ecommerce.domain.exception.ParametroInvalidoException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.Pagina;
import com.example.ecommerce.domain.valueobject.*;
import com.example.ecommerce.infrastructure.persistence.ArticuloRepositoryEnMemoria;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BuscarArticulosUseCaseTest {
    private Articulo crearArticulo(
            String id,
            String nombre,
            String categoria,
            String categoriaId,
            String marca,
            String marcaId,
            String tienda,
            String tiendaId,
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
                Tienda.reconstruir(tiendaId, tienda, "", "v-1", EstadoTienda.ACTIVA)
        );
    }

    private ArticuloRepository crearRepositorioConArticulos() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();

        Articulo labial = crearArticulo(
                "a-1", "Labial rojo", "Labios", "cat-1",
                "Maybelline", "m-1", "Tienda Beauty", "t-1",
                "35000", Tono.CLARO, TipoPiel.NORMAL, 10
        );

        Articulo crema = crearArticulo(
                "a-2", "Crema facial", "Rostro", "cat-2",
                "Nivea", "m-2", "Tienda Beauty", "t-1",
                "45000", Tono.MEDIO, TipoPiel.SECA, 5
        );

        Articulo base = crearArticulo(
                "a-3", "Base líquida", "Rostro", "cat-2",
                "Maybelline", "m-1", "Tienda Glam", "t-2",
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

        List<Articulo> resultado = useCase.ejecutar(criterios, 0, 10).contenido();

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

        List<Articulo> resultado = useCase.ejecutar(criterios, 0, 10).contenido();

        assertEquals(1, resultado.size());
        assertEquals("a-1", resultado.get(0).getId());
    }

    @Test
    void debeFiltrarPorCategoria() {
        ArticuloRepository repository = crearRepositorioConArticulos();
        BuscarArticulosUseCase useCase =
                new BuscarArticulosUseCase(repository);

        CriteriosBusquedaArticulo criterios =
                new CriteriosBusquedaArticulo(
                        null, "cat-2", null, null,
                        null, null, null, null
                );

        List<Articulo> resultado = useCase.ejecutar(criterios, 0, 10).contenido();

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream()
                .allMatch(a -> a.getCategoria().getId().equals("cat-2")));
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

        List<Articulo> resultado = useCase.ejecutar(criterios, 0, 10).contenido();

        assertEquals(3, resultado.size());
    }

    @Test
    void debeCombinarCategoriaYMarca() {
        ArticuloRepository repository = crearRepositorioConArticulos();
        BuscarArticulosUseCase useCase =
                new BuscarArticulosUseCase(repository);

        CriteriosBusquedaArticulo criterios =
                new CriteriosBusquedaArticulo(
                        null, "cat-2", "m-1", null,
                        null, null, null, null
                );

        List<Articulo> resultado = useCase.ejecutar(criterios, 0, 10).contenido();

        assertEquals(1, resultado.size());
        assertEquals("a-3", resultado.get(0).getId());
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

        List<Articulo> resultado = useCase.ejecutar(criterios, 0, 10).contenido();

        assertEquals(1, resultado.size());
        assertEquals("a-2", resultado.get(0).getId());
    }
    @Test
    void noDebeDevolverArticulosSinPublicar() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();

        Articulo articulo = crearArticulo(
                "a-4", "Labial sin publicar", "Labios", "cat-1",
                "Maybelline", "m-1", "Tienda Beauty", "t-1",
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

        List<Articulo> resultado = useCase.ejecutar(criterios, 0, 10).contenido();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void noDebeDevolverArticulosAgotados() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();

        Articulo articulo = crearArticulo(
                "a-5", "Crema agotada", "Rostro", "cat-2",
                "Nivea", "m-2", "Tienda Beauty", "t-1",
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

        List<Articulo> resultado = useCase.ejecutar(criterios, 0, 10).contenido();

        assertTrue(resultado.isEmpty());
    }


    @Test
    void noDebeDevolverArticulosVencidos() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();

        Articulo articulo = Articulo.crear(
                "a-6",
                new NombreArticulo("Base vencida"),
                new Precio(new BigDecimal("50000")),
                Categoria.crear("cat-2", "Rostro"),
                Marca.crear("m-1", "Maybelline"),
                Tono.OSCURO,
                List.of(TipoPiel.GRASA),
                Inventario.crear(8),
                new FechaVencimiento(LocalDate.of(2025, 12, 31)),
                Tienda.reconstruir("t-2", "Tienda Glam", "", "v-1", EstadoTienda.ACTIVA)
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

        List<Articulo> resultado = useCase.ejecutar(criterios, 0, 10).contenido();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void debePaginarYDevolverTotalesDeElementosYPaginas() {
        ArticuloRepository repository = crearRepositorioConArticulos();
        BuscarArticulosUseCase useCase =
                new BuscarArticulosUseCase(repository);

        CriteriosBusquedaArticulo sinFiltros =
                new CriteriosBusquedaArticulo(
                        null, null, null, null,
                        null, null, null, null
                );

        Pagina<Articulo> primera = useCase.ejecutar(sinFiltros, 0, 2);
        Pagina<Articulo> segunda = useCase.ejecutar(sinFiltros, 1, 2);

        assertEquals(3, primera.totalElementos());
        assertEquals(2, primera.totalPaginas());
        assertEquals(2, primera.contenido().size());
        assertEquals(1, segunda.contenido().size());
        // Orden por nombre: Base líquida, Crema facial, Labial rojo
        assertEquals("a-3", primera.contenido().get(0).getId());
        assertEquals("a-1", segunda.contenido().get(0).getId());
    }

    @Test
    void elTotalCuentaSoloLosArticulosQueCumplenElFiltro() {
        ArticuloRepository repository = crearRepositorioConArticulos();
        BuscarArticulosUseCase useCase =
                new BuscarArticulosUseCase(repository);

        CriteriosBusquedaArticulo criterios =
                new CriteriosBusquedaArticulo(
                        null, "cat-2", null, null,
                        null, null, null, null
                );

        Pagina<Articulo> pagina = useCase.ejecutar(criterios, 0, 1);

        assertEquals(2, pagina.totalElementos());
        assertEquals(2, pagina.totalPaginas());
        assertEquals(1, pagina.contenido().size());
    }

    @Test
    void noDebeAceptarUnaPaginaNegativa() {
        BuscarArticulosUseCase useCase =
                new BuscarArticulosUseCase(new ArticuloRepositoryEnMemoria());

        CriteriosBusquedaArticulo criterios =
                new CriteriosBusquedaArticulo(
                        null, null, null, null,
                        null, null, null, null
                );

        assertThrows(ParametroInvalidoException.class,
                () -> useCase.ejecutar(criterios, -1, 10));
    }

    @Test
    void noDebeAceptarUnTamanoDePaginaFueraDeRango() {
        BuscarArticulosUseCase useCase =
                new BuscarArticulosUseCase(new ArticuloRepositoryEnMemoria());

        CriteriosBusquedaArticulo criterios =
                new CriteriosBusquedaArticulo(
                        null, null, null, null,
                        null, null, null, null
                );

        assertThrows(ParametroInvalidoException.class,
                () -> useCase.ejecutar(criterios, 0, 0));
        assertThrows(ParametroInvalidoException.class,
                () -> useCase.ejecutar(criterios, 0, 51));
    }

}
