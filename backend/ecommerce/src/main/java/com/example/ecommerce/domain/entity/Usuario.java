package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.ContrasenaRequeridaException;
import com.example.ecommerce.domain.exception.CorreoElectronicoInvalidoException;
import com.example.ecommerce.domain.exception.CreacionUsuarioNoPermitidaException;
import com.example.ecommerce.domain.exception.FechaNacimientoInvalidaException;
import com.example.ecommerce.domain.exception.NombreUsuarioRequeridoException;
import com.example.ecommerce.domain.exception.RolRequeridoException;
import com.example.ecommerce.domain.exception.TelefonoInvalidoException;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.domain.valueobject.FechaNacimiento;
import com.example.ecommerce.domain.valueobject.RolUsuario;
import com.example.ecommerce.domain.valueobject.Telefono;

import java.util.Objects;

public class Usuario {

    private final long id;
    private String nombre;
    private Email email;
    private String contrasenaHash;
    private Telefono telefono;
    private FechaNacimiento fechaNacimiento;
    private RolUsuario rol;
    private boolean activo;

    public Usuario(long id, String nombre, Email email, String contrasenaHash,
                   Telefono telefono, FechaNacimiento fechaNacimiento, RolUsuario rol) {
        validarNombre(nombre);
        validarEmail(email);
        validarContrasenaHash(contrasenaHash);
        validarTelefono(telefono);
        validarFechaNacimiento(fechaNacimiento);
        validarRol(rol);

        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.contrasenaHash = contrasenaHash;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.rol = rol;
        this.activo = true;
    }

    // Autorregistro: los clientes se crean solos (RN03).
    public static Usuario registrarCliente(long id, String nombre, Email email, String contrasenaHash,
                                           Telefono telefono, FechaNacimiento fechaNacimiento) {
        return new Usuario(id, nombre, email, contrasenaHash, telefono, fechaNacimiento, RolUsuario.CLIENTE);
    }

    // Primer administrador del sistema: nadie lo puede crear, así que nace "sembrado" al arrancar.
    public static Usuario administradorInicial(long id, String nombre, Email email, String contrasenaHash,
                                               Telefono telefono, FechaNacimiento fechaNacimiento) {
        return new Usuario(id, nombre, email, contrasenaHash, telefono, fechaNacimiento, RolUsuario.ADMINISTRADOR);
    }

    // Solo un administrador activo puede crear vendedores u otros administradores (RN03).
    public Usuario crearUsuarioConRol(long id, String nombre, Email email, String contrasenaHash,
                                      Telefono telefono, FechaNacimiento fechaNacimiento, RolUsuario rol) {
        if (this.rol != RolUsuario.ADMINISTRADOR || !this.activo) {
            throw new CreacionUsuarioNoPermitidaException();
        }
        return new Usuario(id, nombre, email, contrasenaHash, telefono, fechaNacimiento, rol);
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new NombreUsuarioRequeridoException();
        }
    }

    private void validarEmail(Email email) {
        if (email == null) {
            throw new CorreoElectronicoInvalidoException();
        }
    }

    private void validarTelefono(Telefono telefono) {
        if (telefono == null) {
            throw new TelefonoInvalidoException();
        }
    }

    private void validarFechaNacimiento(FechaNacimiento fechaNacimiento) {
        if (fechaNacimiento == null) {
            throw new FechaNacimientoInvalidaException();
        }
    }

    private void validarContrasenaHash(String contrasenaHash) {
        if (contrasenaHash == null || contrasenaHash.trim().isEmpty()) {
            throw new ContrasenaRequeridaException();
        }
    }

    private void validarRol(RolUsuario rol) {
        if (rol == null) {
            throw new RolRequeridoException();
        }
    }

    public void desactivar() {
        this.activo = false;
    }

    public long getId() { return id; }
    public String getNombre() { return nombre; }
    public Email getEmail() { return email; }
    public String getContrasenaHash() { return contrasenaHash; }
    public Telefono getTelefono() { return telefono; }
    public FechaNacimiento getFechaNacimiento() { return fechaNacimiento; }
    public RolUsuario getRol() { return rol; }
    public boolean isActivo() { return activo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario otro)) return false;
        return id == otro.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}