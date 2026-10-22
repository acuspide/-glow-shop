package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.*;
import com.example.ecommerce.domain.repository.RutinaCuidadoRepository;
import com.example.ecommerce.domain.valueobject.*;
import com.example.ecommerce.infrastructure.persistence.RutinaCuidadoRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ObtenerRutinaCuidadoUseCaseTest {
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
    void debeObtenerRutinaPorId() {

        RutinaCuidadoRepository repository =
                new RutinaCuidadoRepositoryEnMemoria();

        Articulo articulo1 = crearArticulo("a-1", 10);
        Articulo articulo2 = crearArticulo("a-2", 5);

        articulo1.publicar();
        articulo2.publicar();

        RutinaCuidado rutina = RutinaCuidado.crear(
                1L,
                "Rutina de cuidado diario",
                List.of(articulo1, articulo2)
        );

        repository.guardar(rutina);

        ObtenerRutinaCuidadoUseCase useCase =
                new ObtenerRutinaCuidadoUseCase(repository);

        RutinaCuidado resultado = useCase.ejecutar(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Rutina de cuidado diario", resultado.getNombre());
        assertEquals(2, resultado.getArticulos().size());
    }


}




