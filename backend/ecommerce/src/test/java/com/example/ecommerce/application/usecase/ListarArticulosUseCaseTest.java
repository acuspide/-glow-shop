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

public class ListarArticulosUseCaseTest {
    private Articulo crearArticulo(
            String id,
            String nombre,
            int cantidad
    ) {
        return Articulo.crear(
                id,
                new NombreArticulo(nombre),
                new Precio(new BigDecimal("20000")),
                Categoria.crear(1L, "Labios"),
                Marca.crear(1L, "Maybelline"),
                Tono.CLARO,
                List.of(TipoPiel.NORMAL),
                Inventario.crear(cantidad),
                new FechaVencimiento(LocalDate.of(2027, 12, 31)),
                Tienda.crear(1L, "Tienda Beauty")
        );
    }

    @Test
    void debeListarArticulosPublicadosYDisponibles() {

        // Arrange
        ArticuloRepository repository =
                new ArticuloRepositoryEnMemoria();

        Articulo labial = crearArticulo("a-1", "Labial", 10);
        Articulo crema = crearArticulo("a-2", "Crema facial", 5);

        labial.publicar();
        crema.publicar();

        repository.guardar(labial);
        repository.guardar(crema);

        ListarArticulosUseCase useCase =
                new ListarArticulosUseCase(repository);

        // Act
        List<Articulo> resultado = useCase.ejecutar();

        // Assert
        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(labial));
        assertTrue(resultado.contains(crema));
    }

    @Test
    void noDebeListarArticulosSinPublicar() {

        // Arrange
        ArticuloRepository repository =
                new ArticuloRepositoryEnMemoria();

        Articulo publicado = crearArticulo("a-1", "Labial", 10);
        Articulo sinPublicar = crearArticulo("a-2", "Crema facial", 5);

        publicado.publicar();

        repository.guardar(publicado);
        repository.guardar(sinPublicar);

        ListarArticulosUseCase useCase =
                new ListarArticulosUseCase(repository);

        // Act
        List<Articulo> resultado = useCase.ejecutar();

        // Assert
        assertEquals(1, resultado.size());
        assertTrue(resultado.contains(publicado));
        assertFalse(resultado.contains(sinPublicar));
    }

    @Test
    void debeDevolverListaVaciaCuandoNoHayArticulos() {

        // Arrange
        ArticuloRepository repository =
                new ArticuloRepositoryEnMemoria();

        ListarArticulosUseCase useCase =
                new ListarArticulosUseCase(repository);

        // Act
        List<Articulo> resultado = useCase.ejecutar();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void noDebeListarArticulosAgotados() {

        // Arrange
        ArticuloRepository repository =
                new ArticuloRepositoryEnMemoria();

        Articulo disponible = crearArticulo("a-1", "Labial", 10);
        Articulo agotado = crearArticulo("a-2", "Crema agotada", 0);

        disponible.publicar();
        agotado.publicar();

        repository.guardar(disponible);
        repository.guardar(agotado);

        ListarArticulosUseCase useCase =
                new ListarArticulosUseCase(repository);

        // Act
        List<Articulo> resultado = useCase.ejecutar();

        // Assert
        assertEquals(1, resultado.size());
        assertTrue(resultado.contains(disponible));
        assertFalse(resultado.contains(agotado));
    }

    @Test
    void noDebeListarArticulosVencidos() {

        // Arrange
        ArticuloRepository repository =
                new ArticuloRepositoryEnMemoria();

        Articulo disponible = crearArticulo("a-1", "Labial", 10);

        Articulo vencido = Articulo.crear(
                "a-2",
                new NombreArticulo("Crema vencida"),
                new Precio(new BigDecimal("20000")),
                Categoria.crear(1L, "Rostro"),
                Marca.crear(1L, "Maybelline"),
                Tono.CLARO,
                List.of(TipoPiel.NORMAL),
                Inventario.crear(5),
                new FechaVencimiento(LocalDate.of(2025, 12, 31)),
                Tienda.crear(1L, "Tienda Beauty")
        );

        disponible.publicar();
        repository.guardar(disponible);
        repository.guardar(vencido);

        ListarArticulosUseCase useCase =
                new ListarArticulosUseCase(repository);

        // Act
        List<Articulo> resultado = useCase.ejecutar();

        // Assert
        assertEquals(1, resultado.size());
        assertTrue(resultado.contains(disponible));
        assertFalse(resultado.contains(vencido));
    }

}
