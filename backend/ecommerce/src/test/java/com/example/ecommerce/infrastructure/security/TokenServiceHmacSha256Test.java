package com.example.ecommerce.infrastructure.security;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.TokenInvalidoException;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.domain.valueobject.RolUsuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TokenServiceHmacSha256Test {

    private static final String SECRETO = "secreto-de-pruebas-con-mas-de-32-caracteres";

    private final TokenServiceHmacSha256 tokenService = new TokenServiceHmacSha256(SECRETO, 60);

    private Usuario administrador() {
        return Usuario.administradorInicial(7L, "Admin", new Email("admin@glow.com"), "hash",
                DatosPrueba.TELEFONO, DatosPrueba.FECHA);
    }

    @Test
    void unTokenGeneradoDebePermitirRecuperarElIdDelUsuario() {
        // Arrange
        String token = tokenService.generar(administrador());

        // Act
        long id = tokenService.extraerUsuarioId(token);

        // Assert
        assertEquals(7L, id);
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void noDebeAceptarUnTokenConElPayloadAlterado() {
        // Arrange
        String[] partes = tokenService.generar(administrador()).split("\\.");
        String payloadFalso = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(
                "{\"sub\":\"1\",\"rol\":\"ADMINISTRADOR\",\"iat\":1,\"exp\":99999999999}".getBytes());
        String alterado = partes[0] + "." + payloadFalso + "." + partes[2];

        // Act y Assert
        assertThrows(TokenInvalidoException.class, () -> tokenService.extraerUsuarioId(alterado));
    }

    @Test
    void noDebeAceptarUnTokenFirmadoConOtroSecreto() {
        // Arrange
        TokenServiceHmacSha256 otro = new TokenServiceHmacSha256("otro-secreto-distinto-con-mas-de-32-caracteres", 60);
        String token = otro.generar(administrador());

        // Act y Assert
        assertThrows(TokenInvalidoException.class, () -> tokenService.extraerUsuarioId(token));
    }

    @Test
    void noDebeAceptarUnTokenExpirado() {
        // Arrange
        TokenServiceHmacSha256 sinVigencia = new TokenServiceHmacSha256(SECRETO, 0);
        String token = sinVigencia.generar(administrador());

        // Act y Assert
        assertThrows(TokenInvalidoException.class, () -> sinVigencia.extraerUsuarioId(token));
    }

    @Test
    void noDebeAceptarTextoQueNoEsUnToken() {
        // Act y Assert
        assertThrows(TokenInvalidoException.class, () -> tokenService.extraerUsuarioId("esto-no-es-un-token"));
        assertThrows(TokenInvalidoException.class, () -> tokenService.extraerUsuarioId("a.b.c"));
        assertThrows(TokenInvalidoException.class, () -> tokenService.extraerUsuarioId(null));
    }

    @Test
    void noDebeAceptarUnTokenSinFirma() {
        // Arrange
        String[] partes = tokenService.generar(administrador()).split("\\.");
        String sinFirma = partes[0] + "." + partes[1] + ".";

        // Act y Assert
        assertThrows(TokenInvalidoException.class, () -> tokenService.extraerUsuarioId(sinFirma));
    }

    @Test
    void noDebeCrearseConUnSecretoCorto() {
        // Act y Assert
        assertThrows(IllegalArgumentException.class, () -> new TokenServiceHmacSha256("corto", 60));
    }
}
