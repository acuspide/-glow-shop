package com.example.ecommerce.application.usecase;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.TokenInvalidoException;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.infrastructure.security.TokenServiceHmacSha256;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CerrarSesionUseCaseTest {

    private final TokenServiceHmacSha256 tokenService =
            new TokenServiceHmacSha256("secreto-de-pruebas-con-mas-de-32-caracteres", 60);
    private final CerrarSesionUseCase useCase = new CerrarSesionUseCase(tokenService);

    private final Usuario ana = Usuario.registrarCliente("u-1", "Ana", new Email("ana@correo.com"), "hash",
            DatosPrueba.TELEFONO, DatosPrueba.FECHA);

    @Test
    void despuesDeCerrarSesionElTokenYaNoSirve() {
        // Arrange
        String token = tokenService.generar(ana);

        // Act
        useCase.ejecutar(token);

        // Assert
        assertThrows(TokenInvalidoException.class, () -> tokenService.extraerUsuarioId(token));
    }

    @Test
    void cerrarUnaSesionNoAfectaLasOtrasSesionesDelMismoUsuario() {
        // Arrange: el mismo usuario inició sesión dos veces (por ejemplo, celular y computador)
        String tokenCelular = tokenService.generar(ana);
        String tokenComputador = tokenService.generar(ana);

        // Act
        useCase.ejecutar(tokenCelular);

        // Assert
        assertEquals("u-1", tokenService.extraerUsuarioId(tokenComputador));
    }

    @Test
    void noDebeCerrarSesionConUnTokenInvalido() {
        // Act y Assert
        assertThrows(TokenInvalidoException.class, () -> useCase.ejecutar("token-falso"));
    }

    @Test
    void noDebeCerrarDosVecesLaMismaSesion() {
        // Arrange
        String token = tokenService.generar(ana);
        useCase.ejecutar(token);

        // Act y Assert
        assertThrows(TokenInvalidoException.class, () -> useCase.ejecutar(token));
    }
}
