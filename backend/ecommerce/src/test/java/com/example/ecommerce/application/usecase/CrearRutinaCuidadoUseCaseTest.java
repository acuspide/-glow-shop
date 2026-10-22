package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.*;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.RutinaCuidadoRepository;
import com.example.ecommerce.domain.valueobject.FechaVencimiento;
import com.example.ecommerce.domain.valueobject.NombreArticulo;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.domain.valueobject.TipoPiel;
import com.example.ecommerce.domain.valueobject.Tono;
import com.example.ecommerce.infrastructure.persistence.ArticuloRepositoryEnMemoria;
import com.example.ecommerce.infrastructure.persistence.RutinaCuidadoRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


public class CrearRutinaCuidadoUseCaseTest {
    private Articulo crearArticulo(String id, int cantidad) {
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
    void debeCrearYGuardarRutinaDeCuidado() {

        RutinaCuidadoRepository rutinaRepository =
                new com.example.ecommerce.infrastructure.persistence.RutinaCuidadoRepositoryEnMemoria();

        ArticuloRepository articuloRepository =
                new com.example.ecommerce.infrastructure.persistence.ArticuloRepositoryEnMemoria();

        CrearRutinaCuidadoUseCase useCase =
                new CrearRutinaCuidadoUseCase(
                        rutinaRepository,
                        articuloRepository
                );

        Articulo articulo1 = crearArticulo("a-1", 10);
        Articulo articulo2 = crearArticulo("a-2", 5);

        articulo1.publicar();
        articulo2.publicar();

        articuloRepository.guardar(articulo1);
        articuloRepository.guardar(articulo2);

        RutinaCuidado rutinaCreada = useCase.ejecutar(
                1L,
                "Rutina de cuidado diario",
                List.of("a-1", "a-2")
        );

        assertNotNull(rutinaCreada);
        assertEquals(1L, rutinaCreada.getId());
        assertEquals("Rutina de cuidado diario", rutinaCreada.getNombre());
        assertEquals(2, rutinaCreada.getArticulos().size());

        Optional<RutinaCuidado> rutina =
                rutinaRepository.obtenerPorId(1L);

        assertTrue(rutina.isPresent());
    }
}

