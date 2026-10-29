package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.Categoria;
import com.example.ecommerce.domain.entity.Inventario;
import com.example.ecommerce.domain.entity.Marca;
import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.domain.valueobject.EstadoTienda;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
import com.example.ecommerce.domain.exception.ArticuloVencidoException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.valueobject.FechaVencimiento;
import com.example.ecommerce.domain.valueobject.NombreArticulo;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.domain.valueobject.TipoPiel;
import com.example.ecommerce.domain.valueobject.Tono;
import com.example.ecommerce.infrastructure.persistence.ArticuloRepositoryEnMemoria;
import com.example.ecommerce.infrastructure.persistence.CategoriaRepositoryEnMemoria;
import com.example.ecommerce.infrastructure.persistence.MarcaRepositoryEnMemoria;
import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.example.ecommerce.domain.exception.TiendaAjenaException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ActualizarArticuloUseCaseTest {
    private ActualizarArticuloUseCase crearUseCase(ArticuloRepository repository) {
        CategoriaRepositoryEnMemoria categorias = new CategoriaRepositoryEnMemoria();
        categorias.guardar(Categoria.crear("cat-2", "Cuidado facial"));
        MarcaRepositoryEnMemoria marcas = new MarcaRepositoryEnMemoria();
        marcas.guardar(Marca.crear("m-2", "Nueva marca"));
        return new ActualizarArticuloUseCase(repository, categorias, marcas);
    }

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
    void debeActualizarDatosDeArticuloExistente() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();
        ActualizarArticuloUseCase useCase =
                crearUseCase(repository);

        Articulo articulo = crearArticulo("a-1", 10);
        articulo.publicar();
        repository.guardar(articulo);

        Articulo actualizado = useCase.ejecutar(
                "a-1",
                "v-1",
                new NombreArticulo("Crema hidratante"),
                new Precio(new BigDecimal("45000")),
                "cat-2",
                "m-2",
                Tono.CLARO,
                List.of(TipoPiel.NORMAL),
                new FechaVencimiento(LocalDate.of(2031, 12, 31))
        );

        assertEquals("Crema hidratante", actualizado.getNombre().valor());
        assertEquals(
                0,
                new BigDecimal("45000").compareTo(
                        actualizado.getPrecio().valor()
                )
        );
        assertEquals("cat-2", actualizado.getCategoria().getId());
        assertEquals("m-2", actualizado.getMarca().getId());
        assertEquals(Tono.CLARO, actualizado.getTono());
        assertEquals(
                LocalDate.of(2031, 12, 31),
                actualizado.getFechaVencimiento().fecha()
        );

        assertTrue(actualizado.isPublicado());
        assertEquals(10, actualizado.getCantidadDisponible());
        assertEquals("t-1", actualizado.getTienda().getId());

        assertSame(
                actualizado,
                repository.obtenerPorId("a-1").orElseThrow()
        );
    }

    @Test
    void debeLanzarExcepcionCuandoArticuloNoExiste() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();
        ActualizarArticuloUseCase useCase =
                crearUseCase(repository);

        assertThrows(
                ArticuloNoEncontradoException.class,
                () -> useCase.ejecutar(
                        "a-99",
                        "v-1",
                        new NombreArticulo("Crema hidratante"),
                        new Precio(new BigDecimal("45000")),
                        "cat-2",
                        "m-2",
                        Tono.CLARO,
                        List.of(TipoPiel.NORMAL),
                        new FechaVencimiento(LocalDate.of(2031, 12, 31))
                )
        );
    }

    @Test
    void noDebeActualizarSiLaFechaEstaVencida() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();
        ActualizarArticuloUseCase useCase =
                crearUseCase(repository);

        Articulo articulo = crearArticulo("a-1", 10);
        repository.guardar(articulo);

        assertThrows(
                ArticuloVencidoException.class,
                () -> useCase.ejecutar(
                        "a-1",
                        "v-1",
                        new NombreArticulo("Crema hidratante"),
                        new Precio(new BigDecimal("45000")),
                        "cat-2",
                        "m-2",
                        Tono.CLARO,
                        List.of(TipoPiel.NORMAL),
                        new FechaVencimiento(LocalDate.now().minusDays(1))
                )
        );

        assertEquals("Shampoo", articulo.getNombre().valor());
        assertEquals(10, articulo.getCantidadDisponible());
    }

    @Test
    void otroVendedorNoPuedeActualizarElArticulo() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();
        ActualizarArticuloUseCase useCase = crearUseCase(repository);
        Articulo articulo = crearArticulo("a-1", 10);
        repository.guardar(articulo);

        assertThrows(TiendaAjenaException.class, () -> useCase.ejecutar(
                "a-1", "v-otro",
                new NombreArticulo("Crema hidratante"),
                new Precio(new BigDecimal("45000")),
                "cat-2", "m-2", Tono.CLARO, List.of(TipoPiel.NORMAL),
                new FechaVencimiento(LocalDate.of(2031, 12, 31))));

        assertEquals("Shampoo", articulo.getNombre().valor());
    }

    @Test
    void debeFallarSiLaCategoriaNoExiste() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();
        ActualizarArticuloUseCase useCase = crearUseCase(repository);
        repository.guardar(crearArticulo("a-1", 10));

        assertThrows(RecursoNoEncontradoException.class, () -> useCase.ejecutar(
                "a-1", "v-1",
                new NombreArticulo("Crema hidratante"),
                new Precio(new BigDecimal("45000")),
                "cat-99", "m-2", Tono.CLARO, List.of(TipoPiel.NORMAL),
                new FechaVencimiento(LocalDate.of(2031, 12, 31))));
    }
}
