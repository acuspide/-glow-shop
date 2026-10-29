package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.AccionSoloParaAdministradorException;
import com.example.ecommerce.domain.exception.AccionSoloParaClienteException;
import com.example.ecommerce.domain.exception.AccionSoloParaVendedorException;
import com.example.ecommerce.domain.exception.AutodesactivacionNoPermitidaException;
import com.example.ecommerce.domain.exception.ContrasenaActualIncorrectaException;
import com.example.ecommerce.domain.exception.ContrasenaRequeridaException;
import com.example.ecommerce.domain.exception.CorreoElectronicoInvalidoException;
import com.example.ecommerce.domain.exception.CreacionUsuarioNoPermitidaException;
import com.example.ecommerce.domain.exception.CredencialesInvalidasException;
import com.example.ecommerce.domain.exception.FechaNacimientoInvalidaException;
import com.example.ecommerce.domain.exception.NombreUsuarioRequeridoException;
import com.example.ecommerce.domain.exception.RolRequeridoException;
import com.example.ecommerce.domain.exception.TelefonoInvalidoException;
import com.example.ecommerce.domain.exception.UsuarioInactivoException;
import com.example.ecommerce.domain.exception.UsuarioYaDesactivadoException;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.domain.valueobject.FechaNacimiento;
import com.example.ecommerce.domain.valueobject.RolUsuario;
import com.example.ecommerce.domain.valueobject.Telefono;

import java.util.Objects;
import java.util.function.BiPredicate;

public class Usuario {

    private final String id;
    private String nombre;
    private Email email;
    private String contrasenaHash;
    private Telefono telefono;
    private FechaNacimiento fechaNacimiento;
    private RolUsuario rol;
    private boolean activo;

    // Constructor privado: nadie crea un Usuario sin pasar por un método de fábrica.
    private Usuario(String id, String nombre, Email email, String contrasenaHash,
                    Telefono telefono, FechaNacimiento fechaNacimiento, RolUsuario rol, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.contrasenaHash = contrasenaHash;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.rol = rol;
        this.activo = activo;
    }

    // Constructor de un usuario NUEVO: siempre nace activo y, si un dato rompe una regla,
    // lanza la excepción y el objeto nunca llega a usarse.
    private Usuario(String id, String nombre, Email email, String contrasenaHash,
                    Telefono telefono, FechaNacimiento fechaNacimiento, RolUsuario rol) {
        this(id, nombre, email, contrasenaHash, telefono, fechaNacimiento, rol, true);
        validarNombre(nombre);
        validarEmail(email);
        validarContrasenaHash(contrasenaHash);
        validarTelefono(telefono);
        validarFechaNacimiento(fechaNacimiento);
        validarRol(rol);
    }

    /**
     * Solo para el adaptador de persistencia: "revive" un usuario que YA existía.
     * NO valida: esos datos ya pasaron por las reglas cuando el usuario nació.
     */
    public static Usuario reconstruir(String id, String nombre, Email email, String contrasenaHash,
                                      Telefono telefono, FechaNacimiento fechaNacimiento,
                                      RolUsuario rol, boolean activo) {
        return new Usuario(id, nombre, email, contrasenaHash, telefono, fechaNacimiento, rol, activo);
    }

    // Autorregistro: los clientes se crean solos (RN03).
    public static Usuario registrarCliente(String id, String nombre, Email email, String contrasenaHash,
                                           Telefono telefono, FechaNacimiento fechaNacimiento) {
        return new Usuario(id, nombre, email, contrasenaHash, telefono, fechaNacimiento, RolUsuario.CLIENTE);
    }

    // Autorregistro de vendedor: el vendedor se crea solo. Para publicar necesitará
    // que un administrador apruebe su tienda (RN05, RN06).
    public static Usuario registrarVendedor(String id, String nombre, Email email, String contrasenaHash,
                                            Telefono telefono, FechaNacimiento fechaNacimiento) {
        return new Usuario(id, nombre, email, contrasenaHash, telefono, fechaNacimiento, RolUsuario.VENDEDOR);
    }

    // Primer administrador del sistema: nadie lo puede crear, así que nace "sembrado" al arrancar.
    public static Usuario administradorInicial(String id, String nombre, Email email, String contrasenaHash,
                                               Telefono telefono, FechaNacimiento fechaNacimiento) {
        return new Usuario(id, nombre, email, contrasenaHash, telefono, fechaNacimiento, RolUsuario.ADMINISTRADOR);
    }

    // Solo un administrador activo puede crear vendedores u otros administradores (RN03).
    public Usuario crearUsuarioConRol(String id, String nombre, Email email, String contrasenaHash,
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

    /**
     * Verifica que quien inicia sesión sea este usuario.
     * Primero la contraseña: así, sin conocerla, nadie puede averiguar qué cuentas están desactivadas.
     *
     * @param coincideConHash compara la contraseña escrita con el hash guardado.
     */
    public void autenticar(String contrasenaPlano, BiPredicate<String, String> coincideConHash) {
        if (contrasenaPlano == null || !coincideConHash.test(contrasenaPlano, contrasenaHash)) {
            throw new CredencialesInvalidasException();
        }
        if (!activo) {
            throw new UsuarioInactivoException();
        }
    }

    // Para las acciones de administración (listar usuarios, desactivarlos, aprobar tiendas...).
    public void verificarQueEsAdministradorActivo() {
        if (rol != RolUsuario.ADMINISTRADOR || !activo) {
            throw new AccionSoloParaAdministradorException();
        }
    }

    // Para las acciones propias del comprador (calificar artículos...).
    public void verificarQueEsClienteActivo() {
        if (rol != RolUsuario.CLIENTE || !activo) {
            throw new AccionSoloParaClienteException();
        }
    }

    // Para las acciones propias del vendedor (registrar su tienda, publicar artículos...).
    public void verificarQueEsVendedorActivo() {
        if (rol != RolUsuario.VENDEDOR || !activo) {
            throw new AccionSoloParaVendedorException();
        }
    }

    /**
     * CU-08: un administrador bloquea esta cuenta. Es una baja lógica: el usuario no se borra
     * y su historial (pedidos, calificaciones...) se conserva; solo deja de poder iniciar sesión.
     */
    public void desactivarPor(Usuario administrador) {
        administrador.verificarQueEsAdministradorActivo();
        if (this.equals(administrador)) {
            throw new AutodesactivacionNoPermitidaException();
        }
        desactivar();
    }

    public void desactivar() {
        if (!activo) {
            throw new UsuarioYaDesactivadoException();
        }
        this.activo = false;
    }

    // Edita los datos personales. El correo no se cambia aquí (RN07 exige que siga siendo único).
    public void actualizarDatos(String nombre, Telefono telefono) {
        validarNombre(nombre);
        validarTelefono(telefono);
        this.nombre = nombre;
        this.telefono = telefono;
    }

    /**
     * Cambio directo de contraseña desde el perfil: exige conocer la contraseña actual.
     *
     * @param coincideConHash compara la contraseña escrita con el hash guardado.
     */
    public void cambiarContrasena(String contrasenaActual, String nuevoContrasenaHash,
                                  BiPredicate<String, String> coincideConHash) {
        if (contrasenaActual == null || !coincideConHash.test(contrasenaActual, contrasenaHash)) {
            throw new ContrasenaActualIncorrectaException();
        }
        cambiarContrasena(nuevoContrasenaHash);
    }

    // Reemplaza la contraseña. Recibe el hash: la contraseña en texto plano nunca entra al dominio.
    public void cambiarContrasena(String nuevoContrasenaHash) {
        validarContrasenaHash(nuevoContrasenaHash);
        this.contrasenaHash = nuevoContrasenaHash;
    }

    public String getId() { return id; }
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
        return id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}