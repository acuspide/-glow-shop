package com.example.ecommerce.application.usecase;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.TelefonoInvalidoException;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.infrastructure.persistence.UsuarioRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;

import static org.junit.jupiter.api.Assertions.*;

public class ActualizarPerfilUseCaseTest {

    private final UsuarioRepository repository = new UsuarioRepositoryEnMemoria();
    private final ActualizarPerfilUseCase useCase = new ActualizarPerfilUseCase(repository);

    @BeforeEach
    void guardarUsuario() {
        repository.guardar(Usuario.registrarCliente("u-1", "Ana", new Email("ana@correo.com"), "hash",
                DatosPrueba.TELEFONO, DatosPrueba.FECHA));
    }

    @Test
    void debeGuardarElNuevoNombreYTelefono() {
        useCase.ejecutar("u-1", "Ana María", "3109876543");

        Usuario guardado = repository.obtenerPorId("u-1").orElseThrow();
        assertEquals("Ana María", guardado.getNombre());
        assertEquals("3109876543", guardado.getTelefono().valor());
        assertEquals("ana@correo.com", guardado.getEmail().getValor());
    }

    @Test
    void noDebeAceptarUnTelefonoInvalido() {
        assertThrows(TelefonoInvalidoException.class, () -> useCase.ejecutar("u-1", "Ana", "12"));
    }

    @Test
    void siElUsuarioNoExisteDebeLanzarNoEncontrado() {
        assertThrows(RecursoNoEncontradoException.class, () -> useCase.ejecutar("no-existe", "Ana", "3109876543"));
    }
}
