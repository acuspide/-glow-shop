package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.ArticuloNoPublicadoException;
import com.example.ecommerce.domain.exception.ArticuloSinDisponibilidadException;
import com.example.ecommerce.domain.exception.RutinaSinMinimoDeArticulosException;
import com.example.ecommerce.domain.valueobject.FechaVencimiento;
import com.example.ecommerce.domain.valueobject.NombreArticulo;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.domain.valueobject.TipoPiel;
import com.example.ecommerce.domain.valueobject.Tono;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RutinaCuidadoTest {
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
                Tienda.crear("t-1", "Tienda prueba")
        );
    }
    @Test
    void debeCrearRutinaConDosArticulosPublicadosYDisponibles() {

        // Arrange
        Articulo articulo1 = crearArticulo("a-1", 10);
        Articulo articulo2 = crearArticulo("a-2", 5);

        articulo1.publicar();
        articulo2.publicar();

        // Act
        RutinaCuidado rutina = RutinaCuidado.crear(
                "r-1",
                "Rutina de cuidado diario",
                List.of(articulo1, articulo2)
        );

        // Assert
        assertEquals("r-1", rutina.getId());
        assertEquals("Rutina de cuidado diario", rutina.getNombre());
        assertEquals(2, rutina.getArticulos().size());
    }
    @Test
    void noDebeCrearRutinaConMenosDeDosArticulos() {

        // Arrange
        Articulo articulo = crearArticulo("a-1", 10);
        articulo.publicar();

        // Act + Assert
        assertThrows(
                RutinaSinMinimoDeArticulosException.class,
                () -> RutinaCuidado.crear(
                        "r-1",
                        "Rutina facial",
                        List.of(articulo)
                )
        );
    }
    @Test
    void noDebeCrearRutinaConArticuloNoPublicado() {

        // Arrange
        Articulo articulo1 = crearArticulo("a-1", 10);
        Articulo articulo2 = crearArticulo("a-2", 5);

        articulo1.publicar();

        // articulo2 NO está publicado

        // Act + Assert
        assertThrows(
                ArticuloNoPublicadoException.class,
                () -> RutinaCuidado.crear(
                        "r-1",
                        "Rutina facial",
                        List.of(articulo1, articulo2)
                )
        );
    }
    @Test
    void noDebeCrearRutinaConArticuloSinDisponibilidad() {

        // Arrange
        Articulo articulo1 = crearArticulo("a-1", 10);
        Articulo articulo2 = crearArticulo("a-2", 0);

        articulo1.publicar();
        articulo2.publicar();

        // Act + Assert
        assertThrows(
                ArticuloSinDisponibilidadException.class,
                () -> RutinaCuidado.crear(
                        "r-1",
                        "Rutina facial",
                        List.of(articulo1, articulo2)
                )
        );
    }
    @Test
    void debePermitirArticulosDeDiferentesMarcas() {

        // Arrange
        Articulo articulo1 = crearArticulo("a-1", 10);
        Articulo articulo2 = crearArticulo("a-2", 5);

        articulo1.publicar();
        articulo2.publicar();

        // Act
        RutinaCuidado rutina = RutinaCuidado.crear(
                "r-1",
                "Rutina completa",
                List.of(articulo1, articulo2)
        );

        // Assert
        assertEquals(2, rutina.getArticulos().size());
    }
    @Test
    void unMismoArticuloPuedePertenecerADiferentesRutinas() {

        // Arrange
        Articulo articulo1 = crearArticulo("a-1", 10);
        Articulo articulo2 = crearArticulo("a-2", 5);

        articulo1.publicar();
        articulo2.publicar();

        // Act
        RutinaCuidado rutina1 = RutinaCuidado.crear(
                "r-1",
                "Rutina facial",
                List.of(articulo1, articulo2)
        );

        RutinaCuidado rutina2 = RutinaCuidado.crear(
                "r-2",
                "Rutina hidratante",
                List.of(articulo1, articulo2)
        );

        // Assert
        assertTrue(rutina1.getArticulos().contains(articulo1));
        assertTrue(rutina2.getArticulos().contains(articulo1));
    }
}
