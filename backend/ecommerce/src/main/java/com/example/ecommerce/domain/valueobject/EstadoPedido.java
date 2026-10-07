package com.example.ecommerce.domain.valueobject;

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
}
