package com.example.ecommerce.domain.entity;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.domain.exception.CreacionUsuarioNoPermitidaException;
import com.example.ecommerce.domain.exception.CorreoElectronicoInvalidoException;
import com.example.ecommerce.domain.exception.FechaNacimientoInvalidaException;
import com.example.ecommerce.domain.exception.ReglaDominioException;
import com.example.ecommerce.domain.exception.TelefonoInvalidoException;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.domain.valueobject.RolUsuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioTest {

    @Test
    void dosUsuariosConElMismoIdDebenSerIguales() {
        // Arrange
        Usuario usuario1 = Usuario.reconstruir(1L, "Ana", new Email("ana@correo.com"), "hash1", DatosPrueba.TELEFONO, DatosPrueba.FECHA, RolUsuario.CLIENTE, true);
        Usuario usuario2 = Usuario.reconstruir(1L, "Otro nombre", new Email("otro@correo.com"), "hash2", DatosPrueba.TELEFONO, DatosPrueba.FECHA, RolUsuario.VENDEDOR, true);

        // Act y Assert
        assertEquals(usuario1, usuario2);
    }

    @Test
    void unUsuarioNuevoDebeQuedarActivo() {
        // Arrange y Act
        Usuario usuario = Usuario.registrarCliente(1L, "Ana", new Email("ana@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);

        // Assert
        assertTrue(usuario.isActivo());
    }

    @Test
    void desactivarCambiaElEstadoDelUsuario() {
        // Arrange
        Usuario usuario = Usuario.registrarCliente(1L, "Ana", new Email("ana@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);

        // Act
        usuario.desactivar();

        // Assert
        assertFalse(usuario.isActivo());
    }

    @Test
    void noDebeCrearUsuarioSinNombre() {
        // Act y Assert
        assertThrows(ReglaDominioException.class, () -> {
            Usuario.registrarCliente(1L, " ", new Email("ana@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);
        });
    }

    @Test
    void noDebeCrearUsuarioSinRol() {
        // Arrange
        Usuario admin = Usuario.administradorInicial(1L, "Admin", new Email("admin@glow.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);

        // Act y Assert
        assertThrows(ReglaDominioException.class, () -> {
            admin.crearUsuarioConRol(2L, "Ana", new Email("ana@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA, null);
        });
    }

    @Test
    void registrarClienteDebeCrearSiempreUnUsuarioConRolCliente() {
        // Arrange y Act
        Usuario usuario = Usuario.registrarCliente(1L, "Ana", new Email("ana@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);

        // Assert
        assertEquals(RolUsuario.CLIENTE, usuario.getRol());
    }

    @Test
    void unAdministradorActivoDebePoderCrearUnVendedor() {
        // Arrange
        Usuario admin = Usuario.administradorInicial(1L, "Admin", new Email("admin@glow.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);

        // Act
        Usuario vendedor = admin.crearUsuarioConRol(2L, "Vera", new Email("vera@tienda.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA, RolUsuario.VENDEDOR);

        // Assert
        assertEquals(RolUsuario.VENDEDOR, vendedor.getRol());
    }

    @Test
    void unClienteNoDebePoderCrearUsuariosConRol() {
        // Arrange
        Usuario cliente = Usuario.registrarCliente(1L, "Ana", new Email("ana@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);

        // Act y Assert
        assertThrows(CreacionUsuarioNoPermitidaException.class, () ->
                cliente.crearUsuarioConRol(2L, "Vera", new Email("vera@tienda.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA, RolUsuario.VENDEDOR));
    }

    @Test
    void unVendedorNoDebePoderCrearUsuariosConRol() {
        // Arrange
        Usuario vendedor = Usuario.reconstruir(1L, "Vera", new Email("vera@tienda.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA, RolUsuario.VENDEDOR, true);

        // Act y Assert
        assertThrows(CreacionUsuarioNoPermitidaException.class, () ->
                vendedor.crearUsuarioConRol(2L, "Otro", new Email("otro@tienda.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA, RolUsuario.VENDEDOR));
    }

    @Test
    void unAdministradorInactivoNoDebePoderCrearUsuariosConRol() {
        // Arrange
        Usuario admin = Usuario.administradorInicial(1L, "Admin", new Email("admin@glow.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);
        admin.desactivar();

        // Act y Assert
        assertThrows(CreacionUsuarioNoPermitidaException.class, () ->
                admin.crearUsuarioConRol(2L, "Vera", new Email("vera@tienda.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA, RolUsuario.VENDEDOR));
    }

    @Test
    void administradorInicialDebeCrearUnUsuarioActivoConRolAdministrador() {
        // Arrange y Act
        Usuario admin = Usuario.administradorInicial(1L, "Admin", new Email("admin@glow.com"), "hash",
                DatosPrueba.TELEFONO, DatosPrueba.FECHA);

        // Assert
        assertEquals(RolUsuario.ADMINISTRADOR, admin.getRol());
        assertTrue(admin.isActivo());
    }

    @Test
    void noDebeCrearUsuarioSinTelefono() {
        // Act y Assert
        assertThrows(TelefonoInvalidoException.class, () ->
                Usuario.registrarCliente(1L, "Ana", new Email("ana@correo.com"), "hash", null, DatosPrueba.FECHA));
    }

    @Test
    void noDebeCrearUsuarioSinFechaDeNacimiento() {
        // Act y Assert
        assertThrows(FechaNacimientoInvalidaException.class, () ->
                Usuario.registrarCliente(1L, "Ana", new Email("ana@correo.com"), "hash", DatosPrueba.TELEFONO, null));
    }

    @Test
    void noDebeCrearUsuarioSinCorreo() {
        // Act y Assert
        assertThrows(CorreoElectronicoInvalidoException.class, () ->
                Usuario.registrarCliente(1L, "Ana", null, "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA));
    }

    @Test
    void reconstruirDebeConservarElEstadoInactivo() {
        // Arrange y Act
        Usuario usuario = Usuario.reconstruir(1L, "Ana", new Email("ana@correo.com"), "hash",
                DatosPrueba.TELEFONO, DatosPrueba.FECHA, RolUsuario.CLIENTE, false);

        // Assert
        assertFalse(usuario.isActivo());
        assertEquals(RolUsuario.CLIENTE, usuario.getRol());
    }

    @Test
    void elUsuarioDebeGuardarSuTelefonoYSuFechaDeNacimiento() {
        // Arrange y Act
        Usuario usuario = Usuario.registrarCliente(1L, "Ana", new Email("ana@correo.com"), "hash",
                DatosPrueba.TELEFONO, DatosPrueba.FECHA);

        // Assert
        assertEquals(DatosPrueba.TELEFONO, usuario.getTelefono());
        assertEquals(DatosPrueba.FECHA, usuario.getFechaNacimiento());
    }
}
