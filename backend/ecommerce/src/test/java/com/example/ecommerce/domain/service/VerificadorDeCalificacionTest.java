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
    private final HistorialDeCompras historial = (clienteId, articuloId) -> clienteId.equals("u-1") && articuloId == 10L;
    private final CalificacionRepository calificaciones = new CalificacionRepositoryEnMemoria();
    private final VerificadorDeCalificacion verificador = new VerificadorDeCalificacion(historial, calificaciones);

    @Test
    void quienRecibioElArticuloYNoLoHaCalificadoPuedeCalificar() {
        assertDoesNotThrow(() -> verificador.verificarQuePuedeCalificar("u-1", 10L));
    }

    @Test
    void quienNoRecibioElArticuloNoPuedeCalificar() {
        assertThrows(ArticuloNoRecibidoException.class, () -> verificador.verificarQuePuedeCalificar("u-1", 99L));
        assertThrows(ArticuloNoRecibidoException.class, () -> verificador.verificarQuePuedeCalificar("u-2", 10L));
    }

    @Test
    void noSePuedeCalificarDosVecesElMismoArticulo() {
        calificaciones.guardar(Calificacion.reconstruir("c-1", 10L, "u-1", new Puntuacion(5), "Excelente", LocalDateTime.now()));

        assertThrows(CalificacionDuplicadaException.class, () -> verificador.verificarQuePuedeCalificar("u-1", 10L));
    }
}
