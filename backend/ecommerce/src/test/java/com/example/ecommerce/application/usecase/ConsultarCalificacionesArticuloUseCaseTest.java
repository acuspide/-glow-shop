package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.Calificacion;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.CalificacionRepository;
import com.example.ecommerce.domain.valueobject.Puntuacion;
import com.example.ecommerce.domain.valueobject.ResumenCalificaciones;
import com.example.ecommerce.infrastructure.persistence.CalificacionRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConsultarCalificacionesArticuloUseCaseTest {

    private final CalificacionRepository calificaciones = new CalificacionRepositoryEnMemoria();
    private final ArticuloRepository articulos = mock(ArticuloRepository.class);
    private final ConsultarCalificacionesArticuloUseCase useCase =
            new ConsultarCalificacionesArticuloUseCase(calificaciones, articulos);

    @Test
    void debeDevolverSoloLasCalificacionesDelArticuloConSuPromedio() {
        Articulo articulo = mock(Articulo.class);
        when(articulos.obtenerPorId("a-10")).thenReturn(Optional.of(articulo));
        LocalDateTime ahora = LocalDateTime.now();
        calificaciones.guardar(Calificacion.reconstruir("c-1", "a-10", "u-1", new Puntuacion(5), "Excelente", ahora, false));
        calificaciones.guardar(Calificacion.reconstruir("c-2", "a-10", "u-2", new Puntuacion(2), "Regular", ahora, false));
        calificaciones.guardar(Calificacion.reconstruir("c-3", "a-11", "u-1", new Puntuacion(1), "De otro artículo", ahora, false));

        ResumenCalificaciones resumen = useCase.ejecutar("a-10");

        assertEquals(2, resumen.total());
        assertEquals(3.5, resumen.promedio());
    }

    @Test
    void unArticuloQueNoExisteResponde404() {
        when(articulos.obtenerPorId("a-99")).thenReturn(Optional.empty());

        assertThrows(ArticuloNoEncontradoException.class, () -> useCase.ejecutar("a-99"));
    }
}
