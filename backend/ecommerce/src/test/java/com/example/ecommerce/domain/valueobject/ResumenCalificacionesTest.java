package com.example.ecommerce.domain.valueobject;

import com.example.ecommerce.domain.entity.Calificacion;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResumenCalificacionesTest {

    private final LocalDateTime base = LocalDateTime.of(2026, 10, 1, 10, 0);

    private Calificacion calificacion(String id, int estrellas, int diasDespues) {
        return Calificacion.reconstruir(id, 10L, "u-" + id, new Puntuacion(estrellas), "Comentario", base.plusDays(diasDespues), false);
    }

    @Test
    void elPromedioSeRedondeaAUnDecimal() {
        ResumenCalificaciones resumen = new ResumenCalificaciones(10L,
                List.of(calificacion("1", 5, 0), calificacion("2", 4, 1), calificacion("3", 4, 2)));

        assertEquals(4.3, resumen.promedio());
        assertEquals(3, resumen.total());
    }

    @Test
    void lasCalificacionesEliminadasNoSeMuestranNiCuentanEnElPromedio() {
        Calificacion eliminada = Calificacion.reconstruir("x", 10L, "u-x", new Puntuacion(1), "Retirada", base, true);
        ResumenCalificaciones resumen = new ResumenCalificaciones(10L, List.of(calificacion("1", 5, 0), eliminada));

        assertEquals(5.0, resumen.promedio());
        assertEquals(1, resumen.total());
    }

    @Test
    void sinCalificacionesElPromedioEsCero() {
        ResumenCalificaciones resumen = new ResumenCalificaciones(10L, List.of());

        assertEquals(0.0, resumen.promedio());
        assertEquals(0, resumen.total());
    }

    @Test
    void lasCalificacionesQuedanDeLaMasRecienteALaMasAntigua() {
        ResumenCalificaciones resumen = new ResumenCalificaciones(10L,
                List.of(calificacion("vieja", 3, 0), calificacion("nueva", 5, 5), calificacion("media", 4, 2)));

        assertEquals(List.of("nueva", "media", "vieja"),
                resumen.calificaciones().stream().map(Calificacion::getId).toList());
    }
}
