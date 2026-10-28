package com.example.ecommerce.application.usecase;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.domain.entity.Calificacion;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.CalificacionAjenaException;
import com.example.ecommerce.domain.exception.PuntuacionInvalidaException;
import com.example.ecommerce.domain.repository.CalificacionRepository;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.domain.valueobject.Puntuacion;
import com.example.ecommerce.infrastructure.persistence.CalificacionRepositoryEnMemoria;
import com.example.ecommerce.infrastructure.persistence.UsuarioRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;

import static org.junit.jupiter.api.Assertions.*;

public class ActualizarCalificacionUseCaseTest {

    private final CalificacionRepository calificaciones = new CalificacionRepositoryEnMemoria();
    private final UsuarioRepository usuarios = new UsuarioRepositoryEnMemoria();
    private final ActualizarCalificacionUseCase useCase = new ActualizarCalificacionUseCase(calificaciones, usuarios);

    @BeforeEach
    void prepararDatos() {
        usuarios.guardar(Usuario.registrarCliente("u-1", "Ana", new Email("ana@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA));
        usuarios.guardar(Usuario.registrarCliente("u-2", "Bruno", new Email("bruno@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA));
        calificaciones.guardar(Calificacion.reconstruir("c-1", "a-10", "u-1", new Puntuacion(5), "Excelente", LocalDateTime.now(), false));
    }

    @Test
    void elAutorPuedeCambiarSusEstrellasYComentario() {
        useCase.ejecutar("c-1", "u-1", 2, "Se dañó al mes");

        Calificacion guardada = calificaciones.obtenerPorId("c-1").orElseThrow();
        assertEquals(2, guardada.getPuntuacion().valor());
        assertEquals("Se dañó al mes", guardada.getComentario());
    }

    @Test
    void otroClienteNoPuedeEditarla() {
        assertThrows(CalificacionAjenaException.class, () -> useCase.ejecutar("c-1", "u-2", 1, "Malo"));
    }

    @Test
    void lasEstrellasDebenSeguirEntre1Y5() {
        assertThrows(PuntuacionInvalidaException.class, () -> useCase.ejecutar("c-1", "u-1", 0, "Malo"));
    }

    @Test
    void unaCalificacionQueNoExisteResponde404() {
        assertThrows(RecursoNoEncontradoException.class, () -> useCase.ejecutar("no-existe", "u-1", 3, "Regular"));
    }
}
