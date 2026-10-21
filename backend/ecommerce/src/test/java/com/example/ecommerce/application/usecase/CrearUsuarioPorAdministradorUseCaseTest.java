package com.example.ecommerce.application.usecase;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.application.PasswordHasher;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.CorreoElectronicoDuplicadoException;
import com.example.ecommerce.domain.exception.CreacionUsuarioNoPermitidaException;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.domain.valueobject.RolUsuario;
import com.example.ecommerce.infrastructure.persistence.UsuarioRepositoryEnMemoria;
import com.example.ecommerce.infrastructure.security.PasswordHasherSha256;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CrearUsuarioPorAdministradorUseCaseTest {

    private static final String ADMIN_ID = "u-100";

    private final UsuarioRepository repository = new UsuarioRepositoryEnMemoria();
    private final PasswordHasher passwordHasher = new PasswordHasherSha256();
    private final CrearUsuarioPorAdministradorUseCase useCase =
            new CrearUsuarioPorAdministradorUseCase(repository, passwordHasher);

    private Usuario guardarAdministrador() {
        Usuario admin = Usuario.administradorInicial(ADMIN_ID, "Admin", new Email("admin@glow.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);
        repository.guardar(admin);
        return admin;
    }

    @Test
    void unAdministradorDebePoderCrearUnVendedor() {
        // Arrange
        guardarAdministrador();

        // Act
        Usuario vendedor = useCase.ejecutar(ADMIN_ID, "u-1", "Vera", "vera@tienda.com", "Clave123", DatosPrueba.TELEFONO_TEXTO, DatosPrueba.FECHA_VALOR, RolUsuario.VENDEDOR);

        // Assert
        assertEquals(RolUsuario.VENDEDOR, vendedor.getRol());
        assertTrue(repository.existePorEmail(new Email("vera@tienda.com")));
    }

    @Test
    void unAdministradorDebePoderCrearOtroAdministrador() {
        // Arrange
        guardarAdministrador();

        // Act
        Usuario nuevoAdmin = useCase.ejecutar(ADMIN_ID, "u-2", "Otro Admin", "otro@glow.com", "Clave123", DatosPrueba.TELEFONO_TEXTO, DatosPrueba.FECHA_VALOR, RolUsuario.ADMINISTRADOR);

        // Assert
        assertEquals(RolUsuario.ADMINISTRADOR, nuevoAdmin.getRol());
    }

    @Test
    void unClienteNoDebePoderCrearVendedoresYNoDebeGuardarNada() {
        // Arrange
        repository.guardar(Usuario.registrarCliente("u-1", "Ana", new Email("ana@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA));

        // Act y Assert (doble verificación: la excepción y el estado intacto)
        assertThrows(CreacionUsuarioNoPermitidaException.class, () ->
                useCase.ejecutar("u-1", "u-2", "Vera", "vera@tienda.com", "Clave123", DatosPrueba.TELEFONO_TEXTO, DatosPrueba.FECHA_VALOR, RolUsuario.VENDEDOR));
        assertFalse(repository.existePorEmail(new Email("vera@tienda.com")));
    }

    @Test
    void unAdministradorInactivoNoDebePoderCrearUsuarios() {
        // Arrange
        Usuario admin = guardarAdministrador();
        admin.desactivar();

        // Act y Assert
        assertThrows(CreacionUsuarioNoPermitidaException.class, () ->
                useCase.ejecutar(ADMIN_ID, "u-2", "Vera", "vera@tienda.com", "Clave123", DatosPrueba.TELEFONO_TEXTO, DatosPrueba.FECHA_VALOR, RolUsuario.VENDEDOR));
    }

    @Test
    void siQuienPideNoExisteNoDebeTenerPermiso() {
        // Act y Assert
        assertThrows(CreacionUsuarioNoPermitidaException.class, () ->
                useCase.ejecutar("u-999", "u-2", "Vera", "vera@tienda.com", "Clave123", DatosPrueba.TELEFONO_TEXTO, DatosPrueba.FECHA_VALOR, RolUsuario.VENDEDOR));
    }

    @Test
    void noDebePermitirCrearUnUsuarioConUnCorreoYaRegistrado() {
        // Arrange
        guardarAdministrador();
        useCase.ejecutar(ADMIN_ID, "u-1", "Vera", "vera@tienda.com", "Clave123", DatosPrueba.TELEFONO_TEXTO, DatosPrueba.FECHA_VALOR, RolUsuario.VENDEDOR);

        // Act y Assert
        assertThrows(CorreoElectronicoDuplicadoException.class, () ->
                useCase.ejecutar(ADMIN_ID, "u-2", "Otra Vera", "vera@tienda.com", "Clave123", DatosPrueba.TELEFONO_TEXTO, DatosPrueba.FECHA_VALOR, RolUsuario.VENDEDOR));
    }
}
