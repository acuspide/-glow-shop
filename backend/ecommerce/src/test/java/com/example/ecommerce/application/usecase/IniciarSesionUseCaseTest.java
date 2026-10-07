package com.example.ecommerce.application.usecase;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.application.PasswordHasher;
import com.example.ecommerce.application.SesionIniciada;
import com.example.ecommerce.domain.exception.CredencialesInvalidasException;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.infrastructure.persistence.UsuarioRepositoryEnMemoria;
import com.example.ecommerce.infrastructure.security.PasswordHasherSha256;
import com.example.ecommerce.infrastructure.security.TokenServiceHmacSha256;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IniciarSesionUseCaseTest {

    private final UsuarioRepository repository = new UsuarioRepositoryEnMemoria();
    private final PasswordHasher passwordHasher = new PasswordHasherSha256();
    private final TokenServiceHmacSha256 tokenService =
            new TokenServiceHmacSha256("secreto-de-pruebas-con-mas-de-32-caracteres", 60);
    private final IniciarSesionUseCase useCase = new IniciarSesionUseCase(
            new AutenticarUsuarioUseCase(repository, passwordHasher), tokenService);

    @Test
    void debeEntregarUnTokenQueIdentificaAlUsuarioAutenticado() {
        // Arrange
        new RegistrarClienteUseCase(repository, passwordHasher)
                .ejecutar(5L, "Ana", "ana@correo.com", "Clave123", DatosPrueba.TELEFONO_TEXTO, DatosPrueba.FECHA_VALOR);

        // Act
        SesionIniciada sesion = useCase.ejecutar("ana@correo.com", "Clave123");

        // Assert
        assertEquals(5L, tokenService.extraerUsuarioId(sesion.token()));
        assertEquals("ana@correo.com", sesion.usuario().getEmail().getValor());
    }

    @Test
    void noDebeEntregarTokenConCredencialesIncorrectas() {
        // Arrange
        new RegistrarClienteUseCase(repository, passwordHasher)
                .ejecutar(5L, "Ana", "ana@correo.com", "Clave123", DatosPrueba.TELEFONO_TEXTO, DatosPrueba.FECHA_VALOR);

        // Act y Assert
        assertThrows(CredencialesInvalidasException.class, () ->
                useCase.ejecutar("ana@correo.com", "ClaveIncorrecta1"));
    }
}
