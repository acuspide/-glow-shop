package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.domain.entity.Calificacion;
import com.example.ecommerce.domain.valueobject.Puntuacion;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.CalificacionPersistenceMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import({CalificacionRepositoryJpa.class, CalificacionPersistenceMapper.class})
class CalificacionRepositoryJpaTest {

    private static final LocalDateTime FECHA = LocalDateTime.of(2026, 10, 1, 12, 0);

    @Autowired
    private CalificacionRepositoryJpa repositorio;

    @Test
    void guardaYReconstruyeLaCalificacion() {
        repositorio.guardar(Calificacion.reconstruir("c-1", "a-1", "u-1", new Puntuacion(5), "Excelente", FECHA, false));

        Calificacion leida = repositorio.obtenerPorId("c-1").orElseThrow();

        assertEquals(5, leida.getPuntuacion().valor());
        assertEquals("Excelente", leida.getComentario());
        assertEquals(FECHA, leida.getFecha());
        assertFalse(leida.isEliminada());
    }

    @Test
    void unaCalificacionEliminadaNoCuentaParaVolverACalificar() {
        repositorio.guardar(Calificacion.reconstruir("c-1", "a-1", "u-1", new Puntuacion(5), "Bien", FECHA, true));
        assertFalse(repositorio.existePorClienteYArticulo("u-1", "a-1"));

        repositorio.guardar(Calificacion.reconstruir("c-2", "a-1", "u-1", new Puntuacion(4), "Otra vez", FECHA, false));
        assertTrue(repositorio.existePorClienteYArticulo("u-1", "a-1"));
        assertFalse(repositorio.existePorClienteYArticulo("u-2", "a-1"));
    }

    @Test
    void listaLasCalificacionesDeUnArticulo() {
        repositorio.guardar(Calificacion.reconstruir("c-1", "a-1", "u-1", new Puntuacion(5), "A", FECHA, false));
        repositorio.guardar(Calificacion.reconstruir("c-2", "a-1", "u-2", new Puntuacion(3), "B", FECHA, true));
        repositorio.guardar(Calificacion.reconstruir("c-3", "a-2", "u-1", new Puntuacion(1), "C", FECHA, false));

        assertEquals(2, repositorio.listarPorArticulo("a-1").size());
    }
}
