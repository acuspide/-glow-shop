package com.example.ecommerce.application.usecase;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.application.PasswordHasher;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.AccionSoloParaAdministradorException;
import com.example.ecommerce.domain.exception.UsuarioInactivoException;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.service.VerificadorDeCorreoUnico;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.infrastructure.persistence.UsuarioRepositoryEnMemoria;
import com.example.ecommerce.infrastructure.security.PasswordHasherSha256;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;

import static org.junit.jupiter.api.Assertions.*;

public class DesactivarUsuarioUseCaseTest {

    private final UsuarioRepository repository = new UsuarioRepositoryEnMemoria();
    private final PasswordHasher passwordHasher = new PasswordHasherSha256();
    private final DesactivarUsuarioUseCase useCase = new DesactivarUsuarioUseCase(repository);
    private final AutenticarUsuarioUseCase autenticar = new AutenticarUsuarioUseCase(repository, passwordHasher);

    @BeforeEach
    void guardarUsuarios() {
        repository.guardar(Usuario.administradorInicial("a-1", "Admin", new Email("admin@glow.com"), "hash",
                DatosPrueba.TELEFONO, DatosPrueba.FECHA));
        new RegistrarClienteUseCase(repository, passwordHasher, new VerificadorDeCorreoUnico(repository))
                .ejecutar("u-1", "Ana", "ana@correo.com", "Clave123", DatosPrueba.TELEFONO_TEXTO, DatosPrueba.FECHA_VALOR);
    }

    @Test
    void elUsuarioDesactivadoYaNoPuedeIniciarSesionPeroNoSeBorra() {
        useCase.ejecutar("a-1", "u-1");

        assertThrows(UsuarioInactivoException.class, () -> autenticar.ejecutar("ana@correo.com", "Clave123"));
        assertTrue(repository.obtenerPorId("u-1").isPresent()); // baja lógica: sigue existiendo
    }

    @Test
    void unClienteNoPuedeDesactivarUsuarios() {
        repository.guardar(Usuario.registrarCliente("u-2", "Bruno", new Email("bruno@correo.com"), "hash",
                DatosPrueba.TELEFONO, DatosPrueba.FECHA));

        assertThrows(AccionSoloParaAdministradorException.class, () -> useCase.ejecutar("u-2", "u-1"));
        assertTrue(repository.obtenerPorId("u-1").orElseThrow().isActivo());
    }

    @Test
    void siElUsuarioNoExisteDebeLanzarNoEncontrado() {
        assertThrows(RecursoNoEncontradoException.class, () -> useCase.ejecutar("a-1", "no-existe"));
    }
}
