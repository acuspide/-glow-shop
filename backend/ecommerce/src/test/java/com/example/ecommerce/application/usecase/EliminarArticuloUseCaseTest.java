package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.Categoria;
import com.example.ecommerce.domain.entity.Inventario;
import com.example.ecommerce.domain.entity.Marca;
import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
import com.example.ecommerce.domain.exception.ArticuloNoPublicadoParaVentaException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.PedidoRepository;
import com.example.ecommerce.domain.valueobject.FechaVencimiento;
import com.example.ecommerce.domain.valueobject.NombreArticulo;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.domain.valueobject.TipoPiel;
import com.example.ecommerce.domain.valueobject.Tono;
import com.example.ecommerce.infrastructure.persistence.ArticuloRepositoryEnMemoria;
import com.example.ecommerce.infrastructure.persistence.PedidoRepositoryEnMemoria;
import com.example.ecommerce.domain.exception.ArticuloEliminadoException;
import com.example.ecommerce.domain.service.VerificadorDeArticuloSinPedidosActivos;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EliminarArticuloUseCaseTest {
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
    void debeEliminarArticuloLogicamenteSinBorrarloDelRepositorio() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();
        PedidoRepository pedidoRepository = new PedidoRepositoryEnMemoria();

        EliminarArticuloUseCase useCase =
                new EliminarArticuloUseCase(repository, new VerificadorDeArticuloSinPedidosActivos(pedidoRepository));

        Articulo articulo = crearArticulo(1L, 10);
        articulo.publicar();
        repository.guardar(articulo);

        Articulo eliminado = useCase.ejecutar(1L);

        assertSame(articulo, eliminado);
        assertSame(articulo, repository.obtenerPorId(1L).orElseThrow());

        assertEquals("Shampoo", eliminado.getNombre().valor());
        assertEquals(10, eliminado.getCantidadDisponible());
        assertEquals(1L, eliminado.getTienda().getId());

        assertThrows(
                ArticuloNoPublicadoParaVentaException.class,
                eliminado::validarDisponibleParaConsulta
        );

        assertThrows(
                ArticuloNoPublicadoParaVentaException.class,
                () -> eliminado.validarDisponibleParaVenta(1)
        );
    }

    @Test
    void debeLanzarExcepcionCuandoArticuloNoExiste() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();
        PedidoRepository pedidoRepository = new PedidoRepositoryEnMemoria();

        EliminarArticuloUseCase useCase =
                new EliminarArticuloUseCase(repository, new VerificadorDeArticuloSinPedidosActivos(pedidoRepository));

        assertThrows(
                ArticuloNoEncontradoException.class,
                () -> useCase.ejecutar(99L)
        );
    }

    @Test
    void noDebePermitirEliminarDosVecesElMismoArticulo() {
        ArticuloRepository repository = new ArticuloRepositoryEnMemoria();
        PedidoRepository pedidoRepository = new PedidoRepositoryEnMemoria();

        EliminarArticuloUseCase useCase =
                new EliminarArticuloUseCase(repository, new VerificadorDeArticuloSinPedidosActivos(pedidoRepository));

        Articulo articulo = crearArticulo(1L, 10);
        repository.guardar(articulo);

        useCase.ejecutar(1L);

        assertThrows(
                ArticuloEliminadoException.class,
                () -> useCase.ejecutar(1L)
        );

        assertSame(articulo, repository.obtenerPorId(1L).orElseThrow());
    }
}
