package com.example.ecommerce.application.usecase;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.Calificacion;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.AccionSoloParaClienteException;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
import com.example.ecommerce.domain.exception.ArticuloNoRecibidoException;
import com.example.ecommerce.domain.exception.CalificacionDuplicadaException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.CalificacionRepository;
import com.example.ecommerce.domain.repository.HistorialDeCompras;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.service.VerificadorDeCalificacion;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.infrastructure.persistence.CalificacionRepositoryEnMemoria;
import com.example.ecommerce.infrastructure.persistence.UsuarioRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CalificarArticuloUseCaseTest {

    private final CalificacionRepository calificaciones = new CalificacionRepositoryEnMemoria();
    private final UsuarioRepository usuarios = new UsuarioRepositoryEnMemoria();
    private final ArticuloRepository articulos = mock(ArticuloRepository.class);
    // Ana (u-1) recibió el artículo 10.
    private final HistorialDeCompras historial = (clienteId, articuloId) -> clienteId.equals("u-1") && articuloId == 10L;

    private final CalificarArticuloUseCase useCase = new CalificarArticuloUseCase(
            calificaciones, usuarios, articulos, new VerificadorDeCalificacion(historial, calificaciones));

    @BeforeEach
    void prepararDatos() {
        usuarios.guardar(Usuario.registrarCliente("u-1", "Ana", new Email("ana@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA));
        when(articulos.obtenerPorId(10L)).thenReturn(Optional.of(mock(Articulo.class)));
        when(articulos.obtenerPorId(99L)).thenReturn(Optional.empty());
    }

    @Test
    void elClienteQueRecibioElArticuloPuedeCalificarlo() {
        Calificacion calificacion = useCase.ejecutar("c-1", "u-1", 10L, 5, "Me encantó");

        assertEquals(5, calificacion.getPuntuacion().valor());
        assertTrue(calificaciones.existePorClienteYArticulo("u-1", 10L));
    }

    @Test
    void noSePuedeCalificarUnArticuloNoRecibido() {
        when(articulos.obtenerPorId(11L)).thenReturn(Optional.of(mock(Articulo.class)));

        assertThrows(ArticuloNoRecibidoException.class, () -> useCase.ejecutar("c-1", "u-1", 11L, 5, "Bueno"));
        assertFalse(calificaciones.existePorClienteYArticulo("u-1", 11L));
    }

    @Test
    void noSePuedeCalificarDosVeces() {
        useCase.ejecutar("c-1", "u-1", 10L, 5, "Me encantó");

        assertThrows(CalificacionDuplicadaException.class, () -> useCase.ejecutar("c-2", "u-1", 10L, 1, "Cambié de opinión"));
    }

    @Test
    void unArticuloQueNoExisteResponde404() {
        assertThrows(ArticuloNoEncontradoException.class, () -> useCase.ejecutar("c-1", "u-1", 99L, 5, "Bueno"));
    }

    @Test
    void unUsuarioQueNoExisteNoPuedeCalificar() {
        assertThrows(AccionSoloParaClienteException.class, () -> useCase.ejecutar("c-1", "no-existe", 10L, 5, "Bueno"));
    }
}
