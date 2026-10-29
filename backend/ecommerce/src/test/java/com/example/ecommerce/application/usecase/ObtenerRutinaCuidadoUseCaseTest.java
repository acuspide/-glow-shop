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
    void debeObtenerRutinaPorId() {

        RutinaCuidadoRepository repository =
                new RutinaCuidadoRepositoryEnMemoria();

        Articulo articulo1 = crearArticulo("a-1", 10);
        Articulo articulo2 = crearArticulo("a-2", 5);

        articulo1.publicar();
        articulo2.publicar();

        RutinaCuidado rutina = RutinaCuidado.crear(
                "r-1",
                "Rutina de cuidado diario",
                List.of(articulo1, articulo2)
        );

        repository.guardar(rutina);

        ObtenerRutinaCuidadoUseCase useCase =
                new ObtenerRutinaCuidadoUseCase(repository);

        RutinaCuidado resultado = useCase.ejecutar("r-1");

        assertNotNull(resultado);
        assertEquals("r-1", resultado.getId());
        assertEquals("Rutina de cuidado diario", resultado.getNombre());
        assertEquals(2, resultado.getArticulos().size());
    }


}




