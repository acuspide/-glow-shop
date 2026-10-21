package com.example.ecommerce.application.usecase;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.application.PasswordHasher;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.ContrasenaDebilException;
import com.example.ecommerce.domain.exception.CorreoElectronicoDuplicadoException;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.domain.valueobject.RolUsuario;
import com.example.ecommerce.infrastructure.persistence.UsuarioRepositoryEnMemoria;
import com.example.ecommerce.infrastructure.security.PasswordHasherSha256;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrarVendedorUseCaseTest {

    private final UsuarioRepository repository = new UsuarioRepositoryEnMemoria();
    private final PasswordHasher passwordHasher = new PasswordHasherSha256();
    private final RegistrarVendedorUseCase useCase = new RegistrarVendedorUseCase(repository, passwordHasher);

    @Test
    void debeRegistrarUnVendedorNuevoConCorreoUnico() {
        // Act
        Usuario vendedor = useCase.ejecutar("u-1", "Vera", "vera@tienda.com", "Clave123", DatosPrueba.TELEFONO_TEXTO, DatosPrueba.FECHA_VALOR);

        // Assert
        assertEquals(RolUsuario.VENDEDOR, vendedor.getRol());
        assertTrue(vendedor.isActivo());
        assertTrue(repository.existePorEmail(new Email("vera@tienda.com")));
    }

    @Test
    void noDebePermitirUnCorreoYaRegistradoPorUnCliente() {
        // Arrange
        new RegistrarClienteUseCase(repository, passwordHasher)
                .ejecutar("u-1", "Vera", "vera@tienda.com", "Clave123", DatosPrueba.TELEFONO_TEXTO, DatosPrueba.FECHA_VALOR);

        // Act y Assert
        assertThrows(CorreoElectronicoDuplicadoException.class, () ->
                useCase.ejecutar("u-2", "Vera", "vera@tienda.com", "Clave123", DatosPrueba.TELEFONO_TEXTO, DatosPrueba.FECHA_VALOR));
    }

    @Test
    void noDebeRegistrarUnVendedorConContrasenaDebilYNoDebeGuardarNada() {
        // Act y Assert
        assertThrows(ContrasenaDebilException.class, () ->
                useCase.ejecutar("u-1", "Vera", "vera@tienda.com", "clave", DatosPrueba.TELEFONO_TEXTO, DatosPrueba.FECHA_VALOR));
        assertFalse(repository.existePorEmail(new Email("vera@tienda.com")));
    }

    @Test
    void debeAlmacenarLaContrasenaComoHash() {
        // Act
        Usuario vendedor = useCase.ejecutar("u-1", "Vera", "vera@tienda.com", "Clave123", DatosPrueba.TELEFONO_TEXTO, DatosPrueba.FECHA_VALOR);

        // Assert
        assertNotEquals("Clave123", vendedor.getContrasenaHash());
        assertTrue(passwordHasher.verificar("Clave123", vendedor.getContrasenaHash()));
    }
}
