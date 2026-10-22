package com.example.ecommerce.domain.service;

import com.example.ecommerce.domain.entity.Calificacion;
import com.example.ecommerce.domain.exception.ArticuloNoRecibidoException;
import com.example.ecommerce.domain.exception.CalificacionDuplicadaException;
import com.example.ecommerce.domain.repository.CalificacionRepository;
import com.example.ecommerce.domain.repository.HistorialDeCompras;
import com.example.ecommerce.domain.valueobject.Puntuacion;
import com.example.ecommerce.infrastructure.persistence.CalificacionRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class VerificadorDeCalificacionTest {

    // Doble de prueba: Ana (u-1) recibió el artículo 10 y nada más.
    private final HistorialDeCompras historial = (clienteId, articuloId) -> clienteId.equals("u-1") && articuloId.equals("a-10");
    private final CalificacionRepository calificaciones = new CalificacionRepositoryEnMemoria();
    private final VerificadorDeCalificacion verificador = new VerificadorDeCalificacion(historial, calificaciones);

    @Test
    void quienRecibioElArticuloYNoLoHaCalificadoPuedeCalificar() {
        assertDoesNotThrow(() -> verificador.verificarQuePuedeCalificar("u-1", "a-10"));
    }

    @Test
    void quienNoRecibioElArticuloNoPuedeCalificar() {
        assertThrows(ArticuloNoRecibidoException.class, () -> verificador.verificarQuePuedeCalificar("u-1", "a-99"));
        assertThrows(ArticuloNoRecibidoException.class, () -> verificador.verificarQuePuedeCalificar("u-2", "a-10"));
    }

    @Test
    void siEliminoSuCalificacionPuedeVolverACalificar() {
        calificaciones.guardar(Calificacion.reconstruir("c-1", "a-10", "u-1", new Puntuacion(5), "Excelente", LocalDateTime.now(), true));

        assertDoesNotThrow(() -> verificador.verificarQuePuedeCalificar("u-1", "a-10"));
    }

    @Test
    void noSePuedeCalificarDosVecesElMismoArticulo() {
        calificaciones.guardar(Calificacion.reconstruir("c-1", "a-10", "u-1", new Puntuacion(5), "Excelente", LocalDateTime.now(), false));

        assertThrows(CalificacionDuplicadaException.class, () -> verificador.verificarQuePuedeCalificar("u-1", "a-10"));
    }
}
