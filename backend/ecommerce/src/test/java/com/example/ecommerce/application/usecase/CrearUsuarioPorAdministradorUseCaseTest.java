package com.example.ecommerce.application.usecase;

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

    private static final long ADMIN_ID = 100L;

    private final UsuarioRepository repository = new UsuarioRepositoryEnMemoria();
    private final PasswordHasher passwordHasher = new PasswordHasherSha256();
    private final CrearUsuarioPorAdministradorUseCase useCase =
            new CrearUsuarioPorAdministradorUseCase(repository, passwordHasher);

    private Usuario guardarAdministrador() {
        Usuario admin = new Usuario(ADMIN_ID, "Admin", new Email("admin@glow.com"), "hash", RolUsuario.ADMINISTRADOR);
        repository.guardar(admin);
        return admin;
    }

    @Test
    void unAdministradorDebePoderCrearUnVendedor() {
        // Arrange
        guardarAdministrador();

        // Act
        Usuario vendedor = useCase.ejecutar(ADMIN_ID, 1L, "Vera", "vera@tienda.com", "clave123", RolUsuario.VENDEDOR);

        // Assert
        assertEquals(RolUsuario.VENDEDOR, vendedor.getRol());
        assertTrue(repository.existePorEmail(new Email("vera@tienda.com")));
    }

    @Test
    void unAdministradorDebePoderCrearOtroAdministrador() {
        // Arrange
        guardarAdministrador();

        // Act
        Usuario nuevoAdmin = useCase.ejecutar(ADMIN_ID, 2L, "Otro Admin", "otro@glow.com", "clave123", RolUsuario.ADMINISTRADOR);

        // Assert
        assertEquals(RolUsuario.ADMINISTRADOR, nuevoAdmin.getRol());
    }

    @Test
    void unClienteNoDebePoderCrearVendedoresYNoDebeGuardarNada() {
        // Arrange
        repository.guardar(new Usuario(1L, "Ana", new Email("ana@correo.com"), "hash", RolUsuario.CLIENTE));

        // Act y Assert (doble verificación: la excepción y el estado intacto)
        assertThrows(CreacionUsuarioNoPermitidaException.class, () ->
                useCase.ejecutar(1L, 2L, "Vera", "vera@tienda.com", "clave123", RolUsuario.VENDEDOR));
        assertFalse(repository.existePorEmail(new Email("vera@tienda.com")));
    }

    @Test
    void unAdministradorInactivoNoDebePoderCrearUsuarios() {
        // Arrange
        Usuario admin = guardarAdministrador();
        admin.desactivar();

        // Act y Assert
        assertThrows(CreacionUsuarioNoPermitidaException.class, () ->
                useCase.ejecutar(ADMIN_ID, 2L, "Vera", "vera@tienda.com", "clave123", RolUsuario.VENDEDOR));
    }

    @Test
    void siQuienPideNoExisteNoDebeTenerPermiso() {
        // Act y Assert
        assertThrows(CreacionUsuarioNoPermitidaException.class, () ->
                useCase.ejecutar(999L, 2L, "Vera", "vera@tienda.com", "clave123", RolUsuario.VENDEDOR));
    }

    @Test
    void noDebePermitirCrearUnUsuarioConUnCorreoYaRegistrado() {
        // Arrange
        guardarAdministrador();
        useCase.ejecutar(ADMIN_ID, 1L, "Vera", "vera@tienda.com", "clave123", RolUsuario.VENDEDOR);

        // Act y Assert
        assertThrows(CorreoElectronicoDuplicadoException.class, () ->
                useCase.ejecutar(ADMIN_ID, 2L, "Otra Vera", "vera@tienda.com", "clave123", RolUsuario.VENDEDOR));
    }
}
