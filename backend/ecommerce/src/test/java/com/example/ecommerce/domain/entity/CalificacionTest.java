package com.example.ecommerce.domain.entity;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.domain.exception.AccionSoloParaClienteException;
import com.example.ecommerce.domain.exception.ComentarioInvalidoException;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.domain.valueobject.Puntuacion;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CalificacionTest {

    private final LocalDateTime ahora = LocalDateTime.of(2026, 10, 20, 10, 0);
    private final Usuario ana = Usuario.registrarCliente("u-1", "Ana", new Email("ana@correo.com"), "hash",
            DatosPrueba.TELEFONO, DatosPrueba.FECHA);

    @Test
    void calificarCreaLaCalificacionConLosIdsDelArticuloYDelCliente() {
        Calificacion calificacion = Calificacion.calificar("c-1", 10L, ana, new Puntuacion(5), "  Excelente sérum  ", ahora);

        assertEquals(10L, calificacion.getArticuloId());
        assertEquals("u-1", calificacion.getClienteId());
        assertEquals(5, calificacion.getPuntuacion().valor());
        assertEquals("Excelente sérum", calificacion.getComentario());
    }

    @Test
    void elComentarioEsObligatorio() {
        assertThrows(ComentarioInvalidoException.class,
                () -> Calificacion.calificar("c-1", 10L, ana, new Puntuacion(4), "  ", ahora));
    }

    @Test
    void elComentarioNoPuedeSuperar500Caracteres() {
        String largo = "a".repeat(501);

        assertThrows(ComentarioInvalidoException.class,
                () -> Calificacion.calificar("c-1", 10L, ana, new Puntuacion(4), largo, ahora));
    }

    @Test
    void unVendedorNoPuedeCalificar() {
        Usuario vera = Usuario.registrarVendedor("u-2", "Vera", new Email("vera@tienda.com"), "hash",
                DatosPrueba.TELEFONO, DatosPrueba.FECHA);

        assertThrows(AccionSoloParaClienteException.class,
                () -> Calificacion.calificar("c-1", 10L, vera, new Puntuacion(4), "Bueno", ahora));
    }

    @Test
    void dosCalificacionesConElMismoIdSonLaMisma() {
        Calificacion a = Calificacion.calificar("c-1", 10L, ana, new Puntuacion(5), "Excelente", ahora);
        Calificacion b = Calificacion.reconstruir("c-1", 10L, "u-1", new Puntuacion(1), "Otro texto", ahora);

        assertEquals(a, b);
    }
}
