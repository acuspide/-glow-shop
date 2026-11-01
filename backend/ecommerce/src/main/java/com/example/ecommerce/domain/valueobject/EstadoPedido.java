package com.example.ecommerce.domain.valueobject;

import java.util.EnumSet;
import java.util.Set;

public enum EstadoPedido {
    PENDIENTE,
    CONFIRMADO,
    EN_PREPARACION,
    ENVIADO,
    ENTREGADO,
    CANCELADO;

    public boolean puedeTransicionarA(EstadoPedido nuevoEstado) {
        return switch (this) {
            case PENDIENTE -> nuevoEstado == CONFIRMADO || nuevoEstado == CANCELADO;
            case CONFIRMADO -> nuevoEstado == EN_PREPARACION || nuevoEstado == CANCELADO;
            case EN_PREPARACION -> nuevoEstado == ENVIADO || nuevoEstado == CANCELADO;
            case ENVIADO -> nuevoEstado == ENTREGADO;
            case ENTREGADO, CANCELADO -> false;
        };
    }

    public boolean esFinal() {
        return this == ENTREGADO || this == CANCELADO;
    }

    /** Un pedido cuenta como venta para los reportes cuando ya fue entregado. */
    public boolean cuentaComoVenta() {
        return estadosQueCuentanComoVenta().contains(this);
    }

    public static Set<EstadoPedido> estadosQueCuentanComoVenta() {
        return EnumSet.of(ENTREGADO);
    }
}
