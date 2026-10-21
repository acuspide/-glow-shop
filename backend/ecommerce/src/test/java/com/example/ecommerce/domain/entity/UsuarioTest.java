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
        Usuario usuario1 = Usuario.reconstruir("u-1", "Ana", new Email("ana@correo.com"), "hash1", DatosPrueba.TELEFONO, DatosPrueba.FECHA, RolUsuario.CLIENTE, true);
        Usuario usuario2 = Usuario.reconstruir("u-1", "Otro nombre", new Email("otro@correo.com"), "hash2", DatosPrueba.TELEFONO, DatosPrueba.FECHA, RolUsuario.VENDEDOR, true);

        // Act y Assert
        assertEquals(usuario1, usuario2);
    }

    @Test
    void unUsuarioNuevoDebeQuedarActivo() {
        // Arrange y Act
        Usuario usuario = Usuario.registrarCliente("u-1", "Ana", new Email("ana@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);

        // Assert
        assertTrue(usuario.isActivo());
    }

    @Test
    void desactivarCambiaElEstadoDelUsuario() {
        // Arrange
        Usuario usuario = Usuario.registrarCliente("u-1", "Ana", new Email("ana@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);

        // Act
        usuario.desactivar();

        // Assert
        assertFalse(usuario.isActivo());
    }

    @Test
    void noDebeCrearUsuarioSinNombre() {
        // Act y Assert
        assertThrows(ReglaDominioException.class, () -> {
            Usuario.registrarCliente("u-1", " ", new Email("ana@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);
        });
    }

    @Test
    void noDebeCrearUsuarioSinRol() {
        // Arrange
        Usuario admin = Usuario.administradorInicial("u-1", "Admin", new Email("admin@glow.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);

        // Act y Assert
        assertThrows(ReglaDominioException.class, () -> {
            admin.crearUsuarioConRol("u-2", "Ana", new Email("ana@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA, null);
        });
    }

    @Test
    void registrarClienteDebeCrearSiempreUnUsuarioConRolCliente() {
        // Arrange y Act
        Usuario usuario = Usuario.registrarCliente("u-1", "Ana", new Email("ana@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);

        // Assert
        assertEquals(RolUsuario.CLIENTE, usuario.getRol());
    }

    @Test
    void registrarVendedorDebeCrearUnUsuarioActivoConRolVendedor() {
        // Arrange y Act
        Usuario usuario = Usuario.registrarVendedor("u-1", "Vera", new Email("vera@tienda.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);

        // Assert
        assertEquals(RolUsuario.VENDEDOR, usuario.getRol());
        assertTrue(usuario.isActivo());
    }

    @Test
    void registrarVendedorDebeValidarLosDatosComoCualquierUsuario() {
        // Act y Assert
        assertThrows(ReglaDominioException.class, () ->
                Usuario.registrarVendedor("u-1", " ", new Email("vera@tienda.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA));
    }

    @Test
    void unAdministradorActivoDebePoderCrearUnVendedor() {
        // Arrange
        Usuario admin = Usuario.administradorInicial("u-1", "Admin", new Email("admin@glow.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);

        // Act
        Usuario vendedor = admin.crearUsuarioConRol("u-2", "Vera", new Email("vera@tienda.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA, RolUsuario.VENDEDOR);

        // Assert
        assertEquals(RolUsuario.VENDEDOR, vendedor.getRol());
    }

    @Test
    void unClienteNoDebePoderCrearUsuariosConRol() {
        // Arrange
        Usuario cliente = Usuario.registrarCliente("u-1", "Ana", new Email("ana@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);

        // Act y Assert
        assertThrows(CreacionUsuarioNoPermitidaException.class, () ->
                cliente.crearUsuarioConRol("u-2", "Vera", new Email("vera@tienda.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA, RolUsuario.VENDEDOR));
    }

    @Test
    void unVendedorNoDebePoderCrearUsuariosConRol() {
        // Arrange
        Usuario vendedor = Usuario.reconstruir("u-1", "Vera", new Email("vera@tienda.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA, RolUsuario.VENDEDOR, true);

        // Act y Assert
        assertThrows(CreacionUsuarioNoPermitidaException.class, () ->
                vendedor.crearUsuarioConRol("u-2", "Otro", new Email("otro@tienda.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA, RolUsuario.VENDEDOR));
    }

    @Test
    void unAdministradorInactivoNoDebePoderCrearUsuariosConRol() {
        // Arrange
        Usuario admin = Usuario.administradorInicial("u-1", "Admin", new Email("admin@glow.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);
        admin.desactivar();

        // Act y Assert
        assertThrows(CreacionUsuarioNoPermitidaException.class, () ->
                admin.crearUsuarioConRol("u-2", "Vera", new Email("vera@tienda.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA, RolUsuario.VENDEDOR));
    }

    @Test
    void administradorInicialDebeCrearUnUsuarioActivoConRolAdministrador() {
        // Arrange y Act
        Usuario admin = Usuario.administradorInicial("u-1", "Admin", new Email("admin@glow.com"), "hash",
                DatosPrueba.TELEFONO, DatosPrueba.FECHA);

        // Assert
        assertEquals(RolUsuario.ADMINISTRADOR, admin.getRol());
        assertTrue(admin.isActivo());
    }

    @Test
    void noDebeCrearUsuarioSinTelefono() {
        // Act y Assert
        assertThrows(TelefonoInvalidoException.class, () ->
                Usuario.registrarCliente("u-1", "Ana", new Email("ana@correo.com"), "hash", null, DatosPrueba.FECHA));
    }

    @Test
    void noDebeCrearUsuarioSinFechaDeNacimiento() {
        // Act y Assert
        assertThrows(FechaNacimientoInvalidaException.class, () ->
                Usuario.registrarCliente("u-1", "Ana", new Email("ana@correo.com"), "hash", DatosPrueba.TELEFONO, null));
    }

    @Test
    void noDebeCrearUsuarioSinCorreo() {
        // Act y Assert
        assertThrows(CorreoElectronicoInvalidoException.class, () ->
                Usuario.registrarCliente("u-1", "Ana", null, "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA));
    }

    @Test
    void cambiarContrasenaDebeReemplazarElHash() {
        Usuario usuario = Usuario.registrarCliente("u-1", "Ana", new Email("ana@correo.com"), "hash-viejo", DatosPrueba.TELEFONO, DatosPrueba.FECHA);

        usuario.cambiarContrasena("hash-nuevo");

        assertEquals("hash-nuevo", usuario.getContrasenaHash());
    }

    @Test
    void noDebeCambiarLaContrasenaPorUnHashVacio() {
        Usuario usuario = Usuario.registrarCliente("u-1", "Ana", new Email("ana@correo.com"), "hash-viejo", DatosPrueba.TELEFONO, DatosPrueba.FECHA);

        assertThrows(ReglaDominioException.class, () -> usuario.cambiarContrasena(" "));
        assertEquals("hash-viejo", usuario.getContrasenaHash());
    }

    @Test
    void reconstruirDebeConservarElEstadoInactivo() {
        // Arrange y Act
        Usuario usuario = Usuario.reconstruir("u-1", "Ana", new Email("ana@correo.com"), "hash",
                DatosPrueba.TELEFONO, DatosPrueba.FECHA, RolUsuario.CLIENTE, false);

        // Assert
        assertFalse(usuario.isActivo());
        assertEquals(RolUsuario.CLIENTE, usuario.getRol());
    }

    @Test
    void elUsuarioDebeGuardarSuTelefonoYSuFechaDeNacimiento() {
        // Arrange y Act
        Usuario usuario = Usuario.registrarCliente("u-1", "Ana", new Email("ana@correo.com"), "hash",
                DatosPrueba.TELEFONO, DatosPrueba.FECHA);

        // Assert
        assertEquals(DatosPrueba.TELEFONO, usuario.getTelefono());
        assertEquals(DatosPrueba.FECHA, usuario.getFechaNacimiento());
    }
}
