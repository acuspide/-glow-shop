package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.MontoPagoRequeridoException;
import com.example.ecommerce.domain.exception.TransicionEstadoInvalidaException;
import com.example.ecommerce.domain.valueobject.EstadoPago;
import com.example.ecommerce.domain.valueobject.Precio;

import java.util.Objects;

public class Pago {
    private final long id;
    private final long pedidoId;
    private final Precio monto;
    private EstadoPago estado;

    public Pago(long id, long pedidoId, Precio monto) {
        validarMonto(monto);

        this.id = id;
        this.pedidoId = pedidoId;
        this.monto = monto;
        this.estado = EstadoPago.PENDIENTE;
    }

    private void validarMonto(Precio monto) {
        if (monto == null) {
            throw new MontoPagoRequeridoException();
        }
    }

    public void aprobar() {
        validarEstadoActual(EstadoPago.PENDIENTE, EstadoPago.APROBADO);
        this.estado = EstadoPago.APROBADO;
    }

    public void rechazar() {
        validarEstadoActual(EstadoPago.PENDIENTE, EstadoPago.RECHAZADO);
        this.estado = EstadoPago.RECHAZADO;
    }

    public void reembolsar() {
        validarEstadoActual(EstadoPago.APROBADO, EstadoPago.REEMBOLSADO);
        this.estado = EstadoPago.REEMBOLSADO;
    }

    private void validarEstadoActual(EstadoPago estadoRequerido, EstadoPago estadoDestino) {
        if (estado != estadoRequerido) {
            throw new TransicionEstadoInvalidaException(estado.name(), estadoDestino.name());
        }
    }

    public boolean estaAprobado() {
        return estado == EstadoPago.APROBADO;
    }

    public long getId() { return id; }
    public long getPedidoId() { return pedidoId; }
    public Precio getMonto() { return monto; }
    public EstadoPago getEstado() { return estado; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pago otro)) return false;
        return id == otro.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
