package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.CreacionUsuarioNoPermitidaException;
import com.example.ecommerce.domain.exception.ReglaDominioException;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.domain.valueobject.RolUsuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioTest {

    @Test
    void dosUsuariosConElMismoIdDebenSerIguales() {
        // Arrange
        Usuario usuario1 = new Usuario(1L, "Ana", new Email("ana@correo.com"), "hash1", RolUsuario.CLIENTE);
        Usuario usuario2 = new Usuario(1L, "Otro nombre", new Email("otro@correo.com"), "hash2", RolUsuario.VENDEDOR);

        // Act y Assert
        assertEquals(usuario1, usuario2);
    }

    @Test
    void unUsuarioNuevoDebeQuedarActivo() {
        // Arrange y Act
        Usuario usuario = new Usuario(1L, "Ana", new Email("ana@correo.com"), "hash", RolUsuario.CLIENTE);

        // Assert
        assertTrue(usuario.isActivo());
    }

    @Test
    void desactivarCambiaElEstadoDelUsuario() {
        // Arrange
        Usuario usuario = new Usuario(1L, "Ana", new Email("ana@correo.com"), "hash", RolUsuario.CLIENTE);

        // Act
        usuario.desactivar();

        // Assert
        assertFalse(usuario.isActivo());
    }

    @Test
    void noDebeCrearUsuarioSinNombre() {
        // Act y Assert
        assertThrows(ReglaDominioException.class, () -> {
            new Usuario(1L, " ", new Email("ana@correo.com"), "hash", RolUsuario.CLIENTE);
        });
    }

    @Test
    void noDebeCrearUsuarioSinRol() {
        // Act y Assert
        assertThrows(ReglaDominioException.class, () -> {
            new Usuario(1L, "Ana", new Email("ana@correo.com"), "hash", null);
        });
    }

    @Test
    void registrarClienteDebeCrearSiempreUnUsuarioConRolCliente() {
        // Arrange y Act
        Usuario usuario = Usuario.registrarCliente(1L, "Ana", new Email("ana@correo.com"), "hash");

        // Assert
        assertEquals(RolUsuario.CLIENTE, usuario.getRol());
    }

    @Test
    void unAdministradorActivoDebePoderCrearUnVendedor() {
        // Arrange
        Usuario admin = new Usuario(1L, "Admin", new Email("admin@glow.com"), "hash", RolUsuario.ADMINISTRADOR);

        // Act
        Usuario vendedor = admin.crearUsuarioConRol(2L, "Vera", new Email("vera@tienda.com"), "hash", RolUsuario.VENDEDOR);

        // Assert
        assertEquals(RolUsuario.VENDEDOR, vendedor.getRol());
    }

    @Test
    void unClienteNoDebePoderCrearUsuariosConRol() {
        // Arrange
        Usuario cliente = new Usuario(1L, "Ana", new Email("ana@correo.com"), "hash", RolUsuario.CLIENTE);

        // Act y Assert
        assertThrows(CreacionUsuarioNoPermitidaException.class, () ->
                cliente.crearUsuarioConRol(2L, "Vera", new Email("vera@tienda.com"), "hash", RolUsuario.VENDEDOR));
    }

    @Test
    void unVendedorNoDebePoderCrearUsuariosConRol() {
        // Arrange
        Usuario vendedor = new Usuario(1L, "Vera", new Email("vera@tienda.com"), "hash", RolUsuario.VENDEDOR);

        // Act y Assert
        assertThrows(CreacionUsuarioNoPermitidaException.class, () ->
                vendedor.crearUsuarioConRol(2L, "Otro", new Email("otro@tienda.com"), "hash", RolUsuario.VENDEDOR));
    }

    @Test
    void unAdministradorInactivoNoDebePoderCrearUsuariosConRol() {
        // Arrange
        Usuario admin = new Usuario(1L, "Admin", new Email("admin@glow.com"), "hash", RolUsuario.ADMINISTRADOR);
        admin.desactivar();

        // Act y Assert
        assertThrows(CreacionUsuarioNoPermitidaException.class, () ->
                admin.crearUsuarioConRol(2L, "Vera", new Email("vera@tienda.com"), "hash", RolUsuario.VENDEDOR));
    }
}
