package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.MedioPagoRequeridoException;
import com.example.ecommerce.domain.exception.MontoPagoRequeridoException;
import com.example.ecommerce.domain.exception.TransaccionRequeridaException;
import com.example.ecommerce.domain.exception.TransicionEstadoInvalidaException;
import com.example.ecommerce.domain.valueobject.EstadoPago;
import com.example.ecommerce.domain.valueobject.MedioPago;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.domain.exception.MotivoRechazoRequeridoException;
import java.util.Objects;

public class Pago {
    private final String id;
    private final String pedidoId;
    private final Precio monto;
    private final MedioPago medioPago;
    private EstadoPago estado;
    private String transaccionId;   // lo entrega el sistema de pago (RN56)
    private String motivoRechazo;   // solo si el pago fue rechazado

    private Pago(String id, String pedidoId, Precio monto, MedioPago medioPago,
                 EstadoPago estado, String transaccionId, String motivoRechazo) {
        this.id = id;
        this.pedidoId = pedidoId;
        this.monto = monto;
        this.medioPago = medioPago;
        this.estado = estado;
        this.transaccionId = transaccionId;
        this.motivoRechazo = motivoRechazo;
    }

    public static Pago crear(String id, String pedidoId, Precio monto, MedioPago medioPago) {
        validarMonto(monto);
        validarMedioPago(medioPago);
        return new Pago(id, pedidoId, monto, medioPago, EstadoPago.PENDIENTE, null, null);
    }

    public static Pago reconstruir(String id, String pedidoId, Precio monto, MedioPago medioPago,
                                   EstadoPago estado, String transaccionId, String motivoRechazo) {
        return new Pago(id, pedidoId, monto, medioPago, estado, transaccionId, motivoRechazo);
    }

    private static void validarMonto(Precio monto) {
        if (monto == null) {
            throw new MontoPagoRequeridoException();
        }
    }

    private static void validarMedioPago(MedioPago medioPago) {
        if (medioPago == null) {
            throw new MedioPagoRequeridoException();
        }
    }

    public void aprobar(String transaccionId) {
        validarEstadoActual(EstadoPago.PENDIENTE, EstadoPago.APROBADO);
        validarTransaccion(transaccionId);
        this.transaccionId = transaccionId;
        this.estado = EstadoPago.APROBADO;
    }

    public void rechazar(String transaccionId, String motivoRechazo) {
        validarEstadoActual(EstadoPago.PENDIENTE, EstadoPago.RECHAZADO);
        validarTransaccion(transaccionId);
        validarMotivoRechazo(motivoRechazo);
        this.transaccionId = transaccionId;
        this.motivoRechazo = motivoRechazo;
        this.estado = EstadoPago.RECHAZADO;
    }

    private void validarMotivoRechazo(String motivoRechazo) {
        if (motivoRechazo == null || motivoRechazo.isBlank()) {
            throw new MotivoRechazoRequeridoException();
        }
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

    private void validarTransaccion(String transaccionId) {
        if (transaccionId == null || transaccionId.isBlank()) {
            throw new TransaccionRequeridaException();
        }
    }

    public boolean estaAprobado() {
        return estado == EstadoPago.APROBADO;
    }

    public String getId() { return id; }
    public String getPedidoId() { return pedidoId; }
    public Precio getMonto() { return monto; }
    public MedioPago getMedioPago() { return medioPago; }
    public EstadoPago getEstado() { return estado; }
    public String getTransaccionId() { return transaccionId; }
    public String getMotivoRechazo() { return motivoRechazo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pago otro)) return false;
        return id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}