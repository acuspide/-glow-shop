package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.*;
import com.example.ecommerce.domain.exception.ArticuloNoPublicadoParaVentaException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.CarritoRepository;
import com.example.ecommerce.domain.valueobject.*;
import com.example.ecommerce.infrastructure.persistence.ArticuloRepositoryEnMemoria;
import com.example.ecommerce.infrastructure.persistence.CarritoRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AgregarAlCarritoUseCaseTest {

    private Articulo guardarArticulo(ArticuloRepository articuloRepository, String id, boolean publicado) {
        Articulo articulo = Articulo.crear(
                id,
                new NombreArticulo("Labial"),
                new Precio(new BigDecimal("20000")),
                Categoria.crear(1L, "Labios"),
                Marca.crear(1L, "Bloom"),
                Tono.OSCURO,
                List.of(TipoPiel.NORMAL),
                Inventario.crear(10),
                new FechaVencimiento(LocalDate.now().plusYears(1)),
                Tienda.crear(1L, "Tienda Beauty")
        );
        if (publicado) {
            articulo.publicar();
        }
        articuloRepository.guardar(articulo);
        return articulo;
    }

    @Test
    void debeCrearUnCarritoNuevoYAgregarElArticulo() {

        // Arrange
        ArticuloRepository articuloRepository = new ArticuloRepositoryEnMemoria();
        CarritoRepository carritoRepository = new CarritoRepositoryEnMemoria();
        AgregarAlCarritoUseCase useCase = new AgregarAlCarritoUseCase(carritoRepository, articuloRepository);
        guardarArticulo(articuloRepository, "a-1", true);

        // Act
        Carrito carrito = useCase.ejecutar("c-1", "u-100", "a-1", 2);

        // Assert
        assertEquals("u-100", carrito.getClienteId());
        assertEquals(new BigDecimal("40000"), carrito.calcularTotal());
        assertTrue(carritoRepository.obtenerPorClienteId("u-100").isPresent());
    }

    @Test
    void elMismoClienteDebeUsarSiempreSuMismoCarrito() {

        // Arrange
        ArticuloRepository articuloRepository = new ArticuloRepositoryEnMemoria();
        CarritoRepository carritoRepository = new CarritoRepositoryEnMemoria();
        AgregarAlCarritoUseCase useCase = new AgregarAlCarritoUseCase(carritoRepository, articuloRepository);
        guardarArticulo(articuloRepository, "a-1", true);
        guardarArticulo(articuloRepository, "a-2", true);

        // Act
        Carrito primero = useCase.ejecutar("c-1", "u-100", "a-1", 1);
        Carrito segundo = useCase.ejecutar("c-2", "u-100", "a-2", 1);

        // Assert
        assertEquals(primero.getId(), segundo.getId());
        assertEquals(2, segundo.getItems().size());
    }

    @Test
    void clientesDistintosDebenTenerCarritosDistintos() {

        // Arrange
        ArticuloRepository articuloRepository = new ArticuloRepositoryEnMemoria();
        CarritoRepository carritoRepository = new CarritoRepositoryEnMemoria();
        AgregarAlCarritoUseCase useCase = new AgregarAlCarritoUseCase(carritoRepository, articuloRepository);
        guardarArticulo(articuloRepository, "a-1", true);

        // Act
        Carrito deAna = useCase.ejecutar("c-1", "u-100", "a-1", 1);
        Carrito deLuisa = useCase.ejecutar("c-2", "u-200", "a-1", 1);

        // Assert
        assertNotEquals(deAna.getId(), deLuisa.getId());
    }

    @Test
    void noDebePermitirAgregarUnArticuloQueNoExiste() {

        // Arrange
        ArticuloRepository articuloRepository = new ArticuloRepositoryEnMemoria();
        CarritoRepository carritoRepository = new CarritoRepositoryEnMemoria();
        AgregarAlCarritoUseCase useCase = new AgregarAlCarritoUseCase(carritoRepository, articuloRepository);

        // Act y Assert
        assertThrows(NoSuchElementException.class, () -> {
            useCase.ejecutar("c-1", "u-100", "a-99", 1);
        });
    }

    @Test
    void noDebePermitirAgregarUnArticuloNoPublicado() {

        // Arrange
        ArticuloRepository articuloRepository = new ArticuloRepositoryEnMemoria();
        CarritoRepository carritoRepository = new CarritoRepositoryEnMemoria();
        AgregarAlCarritoUseCase useCase = new AgregarAlCarritoUseCase(carritoRepository, articuloRepository);
        guardarArticulo(articuloRepository, "a-1", false);

        // Act y Assert
        assertThrows(ArticuloNoPublicadoParaVentaException.class, () -> {
            useCase.ejecutar("c-1", "u-100", "a-1", 1);
        });
    }
}
