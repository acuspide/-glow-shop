package com.example.ecommerce.application.usecase;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.application.PasswordHasher;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.ContrasenaDebilException;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.domain.valueobject.RolUsuario;
import com.example.ecommerce.infrastructure.persistence.UsuarioRepositoryEnMemoria;
import com.example.ecommerce.infrastructure.security.PasswordHasherSha256;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CrearAdministradorInicialUseCaseTest {

    private final UsuarioRepository repository = new UsuarioRepositoryEnMemoria();
    private final PasswordHasher passwordHasher = new PasswordHasherSha256();
    private final CrearAdministradorInicialUseCase useCase =
            new CrearAdministradorInicialUseCase(repository, passwordHasher);

    @Test
    void debeCrearElAdministradorInicialSiNoExiste() {
        // Act
        boolean creado = useCase.ejecutar(1L, "Admin", "admin@glow.com", "Admin12345",
                DatosPrueba.TELEFONO_TEXTO, DatosPrueba.FECHA_VALOR);

        // Assert
        assertTrue(creado);
        Usuario admin = repository.buscarPorEmail(new Email("admin@glow.com")).orElseThrow();
        assertEquals(RolUsuario.ADMINISTRADOR, admin.getRol());
    }

    @Test
    void noDebeDuplicarAlAdministradorSiYaExiste() {
        // Arrange
        useCase.ejecutar(1L, "Admin", "admin@glow.com", "Admin12345",
                DatosPrueba.TELEFONO_TEXTO, DatosPrueba.FECHA_VALOR);

        // Act
        boolean creadoOtraVez = useCase.ejecutar(2L, "Admin", "admin@glow.com", "Admin12345",
                DatosPrueba.TELEFONO_TEXTO, DatosPrueba.FECHA_VALOR);

        // Assert
        assertFalse(creadoOtraVez);
        assertTrue(repository.obtenerPorId(2L).isEmpty());
    }

    @Test
    void noDebeCrearElAdministradorConUnaContrasenaDebilYNoDebeGuardarNada() {
        // Act y Assert (doble verificación: la excepción y el estado intacto)
        assertThrows(ContrasenaDebilException.class, () ->
                useCase.ejecutar(1L, "Admin", "admin@glow.com", "admin",
                        DatosPrueba.TELEFONO_TEXTO, DatosPrueba.FECHA_VALOR));
        assertFalse(repository.existePorEmail(new Email("admin@glow.com")));
    }
}
