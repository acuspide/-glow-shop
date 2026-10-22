package com.example.ecommerce.application.usecase;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.application.PasswordHasher;
import com.example.ecommerce.domain.exception.ContrasenaActualIncorrectaException;
import com.example.ecommerce.domain.exception.ContrasenaDebilException;
import com.example.ecommerce.domain.exception.CredencialesInvalidasException;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.service.VerificadorDeCorreoUnico;
import com.example.ecommerce.infrastructure.persistence.UsuarioRepositoryEnMemoria;
import com.example.ecommerce.infrastructure.security.PasswordHasherSha256;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CambiarContrasenaUseCaseTest {

    private final UsuarioRepository repository = new UsuarioRepositoryEnMemoria();
    private final PasswordHasher passwordHasher = new PasswordHasherSha256();
    private final CambiarContrasenaUseCase useCase = new CambiarContrasenaUseCase(repository, passwordHasher);
    private final AutenticarUsuarioUseCase autenticar = new AutenticarUsuarioUseCase(repository, passwordHasher);

    @BeforeEach
    void registrarUsuario() {
        new RegistrarClienteUseCase(repository, passwordHasher, new VerificadorDeCorreoUnico(repository))
                .ejecutar("u-1", "Ana", "ana@correo.com", "Clave123", DatosPrueba.TELEFONO_TEXTO, DatosPrueba.FECHA_VALOR);
    }

    @Test
    void conLaContrasenaActualCorrectaDebeCambiarla() {
        useCase.ejecutar("u-1", "Clave123", "NuevaClave9");

        assertEquals("u-1", autenticar.ejecutar("ana@correo.com", "NuevaClave9").getId());
        assertThrows(CredencialesInvalidasException.class, () -> autenticar.ejecutar("ana@correo.com", "Clave123"));
    }

    @Test
    void conLaContrasenaActualIncorrectaNoDebeCambiarla() {
        assertThrows(ContrasenaActualIncorrectaException.class,
                () -> useCase.ejecutar("u-1", "Equivocada1", "NuevaClave9"));
        assertEquals("u-1", autenticar.ejecutar("ana@correo.com", "Clave123").getId());
    }

    @Test
    void laNuevaContrasenaDebeSerSegura() {
        assertThrows(ContrasenaDebilException.class, () -> useCase.ejecutar("u-1", "Clave123", "debil"));
    }
}
