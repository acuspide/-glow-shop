package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.DescripcionTiendaMuyLargaException;
import com.example.ecommerce.domain.exception.NombreTiendaRequeridoException;
import com.example.ecommerce.domain.exception.TiendaAjenaException;
import com.example.ecommerce.domain.exception.TiendaNoActivaException;
import com.example.ecommerce.domain.exception.TiendaNoPendienteException;
import com.example.ecommerce.domain.valueobject.EstadoTienda;

import java.util.Objects;

/**
 * Tienda de un vendedor dentro del marketplace.
 * RN08: pertenece a un vendedor. RN05/RN06: nace PENDIENTE y un administrador la aprueba.
 * RN10: solo una tienda ACTIVA puede publicar artículos, y solo su dueño publica en ella.
 */
public class Tienda {

    private static final int MAXIMO_DESCRIPCION = 500;

    private final String id;
    private final String nombre;
    private final String descripcion;
    private final String vendedorId;
    private EstadoTienda estado;

    private Tienda(String id, String nombre, String descripcion, String vendedorId, EstadoTienda estado) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.vendedorId = vendedorId;
        this.estado = estado;
    }

    /** CU Registrar tienda: solo un vendedor activo, y la tienda queda pendiente de aprobación. */
    public static Tienda registrar(String id, String nombre, String descripcion, Usuario vendedor) {
        vendedor.verificarQueEsVendedorActivo();
        validarNombre(nombre);
        String descripcionLimpia = descripcion == null ? "" : descripcion.trim();
        validarDescripcion(descripcionLimpia);
        return new Tienda(id, nombre.trim(), descripcionLimpia, vendedor.getId(), EstadoTienda.PENDIENTE);
    }

    /** Para devolver una tienda ya guardada, tal como estaba. */
    public static Tienda reconstruir(String id, String nombre, String descripcion,
                                     String vendedorId, EstadoTienda estado) {
        return new Tienda(id, nombre, descripcion, vendedorId, estado);
    }

    /** CU Aprobar tienda: solo un administrador activo, y solo si la tienda sigue pendiente. */
    public void aprobarPor(Usuario administrador) {
        administrador.verificarQueEsAdministradorActivo();
        if (estado != EstadoTienda.PENDIENTE) {
            throw new TiendaNoPendienteException(estado);
        }
        this.estado = EstadoTienda.ACTIVA;
    }

    /** RN10: para publicar, la tienda debe ser del vendedor y estar activa. */
    public void verificarQuePuedePublicar(String vendedorId) {
        if (!this.vendedorId.equals(vendedorId)) {
            throw new TiendaAjenaException();
        }
        if (estado != EstadoTienda.ACTIVA) {
            throw new TiendaNoActivaException(estado);
        }
    }

    private static void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new NombreTiendaRequeridoException();
        }
    }

    private static void validarDescripcion(String descripcion) {
        if (descripcion.length() > MAXIMO_DESCRIPCION) {
            throw new DescripcionTiendaMuyLargaException(MAXIMO_DESCRIPCION);
        }
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getVendedorId() {
        return vendedorId;
    }

    public EstadoTienda getEstado() {
        return estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tienda otra)) return false;
        return id.equals(otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
