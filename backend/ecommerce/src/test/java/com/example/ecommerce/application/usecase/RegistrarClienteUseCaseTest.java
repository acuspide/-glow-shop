package com.example.ecommerce.application.usecase;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.application.PasswordHasher;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.ContrasenaDebilException;
import com.example.ecommerce.domain.exception.ContrasenaRequeridaException;
import com.example.ecommerce.domain.exception.CorreoElectronicoDuplicadoException;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.domain.valueobject.RolUsuario;
import com.example.ecommerce.infrastructure.persistence.UsuarioRepositoryEnMemoria;
import com.example.ecommerce.infrastructure.security.PasswordHasherSha256;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrarClienteUseCaseTest {

    @Test
    void debeRegistrarUnClienteNuevoConCorreoUnico() {
        // Arrange
        UsuarioRepository repository = new UsuarioRepositoryEnMemoria();
        PasswordHasher passwordHasher = new PasswordHasherSha256();
        RegistrarClienteUseCase useCase = new RegistrarClienteUseCase(repository, passwordHasher);

        // Act
        Usuario usuario = useCase.ejecutar(1L, "Ana Pérez", "ana@correo.com", "Clave123", DatosPrueba.TELEFONO_TEXTO, DatosPrueba.FECHA_VALOR);

        // Assert
        assertEquals(RolUsuario.CLIENTE, usuario.getRol());
        assertTrue(repository.existePorEmail(new Email("ana@correo.com")));
    }

    @Test
    void noDebePermitirRegistrarDosUsuariosConElMismoCorreo() {
        // Arrange
        UsuarioRepository repository = new UsuarioRepositoryEnMemoria();
        PasswordHasher passwordHasher = new PasswordHasherSha256();
        RegistrarClienteUseCase useCase = new RegistrarClienteUseCase(repository, passwordHasher);
        useCase.ejecutar(1L, "Ana", "ana@correo.com", "Clave123", DatosPrueba.TELEFONO_TEXTO, DatosPrueba.FECHA_VALOR);

        // Act y Assert
        assertThrows(CorreoElectronicoDuplicadoException.class, () -> {
            useCase.ejecutar(2L, "Otra Ana", "ana@correo.com", "OtraClave1", DatosPrueba.TELEFONO_TEXTO, DatosPrueba.FECHA_VALOR);
        });
    }

    @Test
    void debeAlmacenarLaContrasenaComoHashYNoEnTextoPlano() {
        // Arrange
        UsuarioRepository repository = new UsuarioRepositoryEnMemoria();
        PasswordHasher passwordHasher = new PasswordHasherSha256();
        RegistrarClienteUseCase useCase = new RegistrarClienteUseCase(repository, passwordHasher);

        // Act
        Usuario usuario = useCase.ejecutar(1L, "Ana", "ana@correo.com", "Clave123", DatosPrueba.TELEFONO_TEXTO, DatosPrueba.FECHA_VALOR);

        // Assert
        assertNotEquals("Clave123", usuario.getContrasenaHash());
    }

    @Test
    void noDebeRegistrarConContrasenaVaciaYNoDebeGuardarNada() {
        // Arrange
        UsuarioRepository repository = new UsuarioRepositoryEnMemoria();
        PasswordHasher passwordHasher = new PasswordHasherSha256();
        RegistrarClienteUseCase useCase = new RegistrarClienteUseCase(repository, passwordHasher);

        // Act y Assert (doble verificación: la excepción y el estado intacto)
        assertThrows(ContrasenaRequeridaException.class, () ->
                useCase.ejecutar(1L, "Ana", "ana@correo.com", "   ", DatosPrueba.TELEFONO_TEXTO, DatosPrueba.FECHA_VALOR));
        assertFalse(repository.existePorEmail(new Email("ana@correo.com")));
    }

    @Test
    void noDebeRegistrarConUnaContrasenaDebilYNoDebeGuardarNada() {
        // Arrange
        UsuarioRepository repository = new UsuarioRepositoryEnMemoria();
        PasswordHasher passwordHasher = new PasswordHasherSha256();
        RegistrarClienteUseCase useCase = new RegistrarClienteUseCase(repository, passwordHasher);

        // Act y Assert (doble verificación: la excepción y el estado intacto)
        assertThrows(ContrasenaDebilException.class, () ->
                useCase.ejecutar(1L, "Ana", "ana@correo.com", "clave123", DatosPrueba.TELEFONO_TEXTO, DatosPrueba.FECHA_VALOR));
        assertFalse(repository.existePorEmail(new Email("ana@correo.com")));
    }
}
