package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.*;
import com.example.ecommerce.domain.exception.ArticuloAgotadoException;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
import com.example.ecommerce.domain.exception.ArticuloNoPublicadoParaVentaException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.valueobject.*;
import com.example.ecommerce.infrastructure.persistence.ArticuloRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MostrarDetalleArticuloUseCaseTest {

    @Test
    void debeMostrarDetalleCuandoElArticuloExiste() {

        // Arrange
        ArticuloRepository repository =
                new ArticuloRepositoryEnMemoria();

        Articulo articulo = Articulo.crear(
                "a-1",
                new NombreArticulo("Labial"),
                new Precio(new BigDecimal("20000")),
                Categoria.crear("cat-1", "Labios"),
                Marca.crear("m-1", "Maybelline"),
                Tono.CLARO,
                List.of(TipoPiel.NORMAL),
                Inventario.crear(10),
                new FechaVencimiento(LocalDate.of(2027, 12, 31)),
                Tienda.reconstruir("t-1", "Tienda Beauty", "", "v-1", EstadoTienda.ACTIVA)
        );
        articulo.publicar();

        repository.guardar(articulo);

        MostrarDetalleArticuloUseCase useCase =
                new MostrarDetalleArticuloUseCase(repository);

        // Act
        Articulo resultado = useCase.ejecutar("a-1");

        // Assert
        assertNotNull(resultado);
        assertEquals("a-1", resultado.getId());
        assertEquals(articulo, resultado);
        assertEquals("Labial", resultado.getNombre().valor());
    }

    @Test
    void debeLanzarExcepcionCuandoElArticuloNoExiste() {

        // Arrange
        ArticuloRepository repository =
                new ArticuloRepositoryEnMemoria();

        MostrarDetalleArticuloUseCase useCase =
                new MostrarDetalleArticuloUseCase(repository);

        // Act y Assert
        assertThrows(
                ArticuloNoEncontradoException.class,
                () -> useCase.ejecutar("a-999")
        );
    }

    @Test
    void debeLanzarExcepcionCuandoElArticuloNoEstaPublicado() {

        // Arrange
        ArticuloRepository repository =
                new ArticuloRepositoryEnMemoria();

        Articulo articulo = Articulo.crear(
                "a-1",
                new NombreArticulo("Labial"),
                new Precio(new BigDecimal("20000")),
                Categoria.crear("cat-1", "Labios"),
                Marca.crear("m-1", "Maybelline"),
                Tono.CLARO,
                List.of(TipoPiel.NORMAL),
                Inventario.crear(10),
                new FechaVencimiento(LocalDate.of(2027, 12, 31)),
                Tienda.reconstruir("t-1", "Tienda Beauty", "", "v-1", EstadoTienda.ACTIVA)
        );

        // No llamamos a articulo.publicar()
        repository.guardar(articulo);

        MostrarDetalleArticuloUseCase useCase =
                new MostrarDetalleArticuloUseCase(repository);

        // Act y Assert
        assertThrows(
                ArticuloNoPublicadoParaVentaException.class,
                () -> useCase.ejecutar("a-1")
        );
    }

    @Test
    void debeLanzarExcepcionCuandoElArticuloEstaAgotado() {

        // Arrange
        ArticuloRepository repository =
                new ArticuloRepositoryEnMemoria();

        Articulo articulo = Articulo.crear(
                "a-3",
                new NombreArticulo("Labial agotado"),
                new Precio(new BigDecimal("20000")),
                Categoria.crear("cat-1", "Labios"),
                Marca.crear("m-1", "Maybelline"),
                Tono.CLARO,
                List.of(TipoPiel.NORMAL),
                Inventario.crear(0),
                new FechaVencimiento(LocalDate.of(2027, 12, 31)),
                Tienda.reconstruir("t-1", "Tienda Beauty", "", "v-1", EstadoTienda.ACTIVA)
        );

        articulo.publicar();
        repository.guardar(articulo);

        MostrarDetalleArticuloUseCase useCase =
                new MostrarDetalleArticuloUseCase(repository);

        // Act y Assert
        assertThrows(
                ArticuloAgotadoException.class,
                () -> useCase.ejecutar("a-3")
        );
    }




}
