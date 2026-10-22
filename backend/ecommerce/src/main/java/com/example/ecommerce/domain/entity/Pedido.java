package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.CargoPedidoRequeridoException;
import com.example.ecommerce.domain.exception.CuponNoVigenteException;
import com.example.ecommerce.domain.exception.CuponYaAplicadoException;
import com.example.ecommerce.domain.exception.DescuentoSuperaSubtotalException;
import com.example.ecommerce.domain.exception.PedidoNoModificableException;
import com.example.ecommerce.domain.exception.PedidoSinDetallesException;
import com.example.ecommerce.domain.exception.TransicionEstadoInvalidaException;
import com.example.ecommerce.domain.valueobject.DetallePedido;
import com.example.ecommerce.domain.valueobject.EstadoPedido;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.domain.exception.PagoInvalidoException;
import com.example.ecommerce.domain.exception.PagoNoRegistrableException;
import com.example.ecommerce.domain.exception.PagoNoRegistradoException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Pedido {
    private final String id;
    private final String clienteId;
    private final List<DetallePedido> detalles;
    private EstadoPedido estado;
    private String cuponId;
    private Precio descuento;
    private Precio impuestos;
    private Precio costoEnvio;
    private String pagoConfirmadoId;

    private Pedido(String id, String clienteId) {
        this.id = id;
        this.clienteId = clienteId;
        this.detalles = new ArrayList<>();
        this.estado = EstadoPedido.PENDIENTE;
        this.descuento = new Precio(BigDecimal.ZERO);
        this.impuestos = new Precio(BigDecimal.ZERO);
        this.costoEnvio = new Precio(BigDecimal.ZERO);
    }

    public static Pedido crear(String id, String clienteId, List<DetallePedido> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            throw new PedidoSinDetallesException();
        }
        Pedido pedido = new Pedido(id, clienteId);
        detalles.forEach(pedido::agregarDetalle);
        return pedido;
    }

    public void agregarDetalle(DetallePedido detalle) {
        validarModificable();
        if (detalle == null) {
            throw new PedidoSinDetallesException();
        }
        detalles.add(detalle);
    }

    public void aplicarCupon(Cupon cupon) {
        validarModificable();
        if (cuponId != null) {
            throw new CuponYaAplicadoException();
        }
        if (!cupon.estaVigente()) {
            throw new CuponNoVigenteException();
        }

        BigDecimal subtotal = calcularSubtotal();
        Precio descuentoCalculado = cupon.calcularDescuento(subtotal);
        if (descuentoCalculado.valor().compareTo(subtotal) > 0) {
            throw new DescuentoSuperaSubtotalException();
        }

        this.cuponId = cupon.getId();
        this.descuento = descuentoCalculado;
    }

    public void asignarImpuestos(Precio impuestos) {
        validarModificable();
        if (impuestos == null) {
            throw new CargoPedidoRequeridoException();
        }
        this.impuestos = impuestos;
    }

    public void asignarCostoEnvio(Precio costoEnvio) {
        validarModificable();
        if (costoEnvio == null) {
            throw new CargoPedidoRequeridoException();
        }
        this.costoEnvio = costoEnvio;
    }

    public void confirmar() {
        transicionarA(EstadoPedido.CONFIRMADO);
    }

    public void registrarPago(Pago pago) {
        if (estado != EstadoPedido.CONFIRMADO || pagoConfirmadoId != null) {
            throw new PagoNoRegistrableException();
        }
        if (pago == null
                || !pago.estaAprobado()
                || !pago.getPedidoId().equals(id)
                || pago.getMonto().valor().compareTo(calcularTotal()) != 0) {
            throw new PagoInvalidoException();
        }
        this.pagoConfirmadoId = pago.getId();
    }

    public void iniciarPreparacion() {
        if (pagoConfirmadoId == null) {
            throw new PagoNoRegistradoException();
        }
        transicionarA(EstadoPedido.EN_PREPARACION);
    }

    public void enviar() {
        transicionarA(EstadoPedido.ENVIADO);
    }

    public void entregar() {
        transicionarA(EstadoPedido.ENTREGADO);
    }
    public void cancelar() {
        transicionarA(EstadoPedido.CANCELADO);
    }

    private void transicionarA(EstadoPedido nuevoEstado) {
        if (!estado.puedeTransicionarA(nuevoEstado)) {
            throw new TransicionEstadoInvalidaException(estado.name(), nuevoEstado.name());
        }
        this.estado = nuevoEstado;
    }

    private void validarModificable() {
        if (estado != EstadoPedido.PENDIENTE) {
            throw new PedidoNoModificableException();
        }
    }

    public BigDecimal calcularSubtotal() {
        return detalles.stream()
                .map(DetallePedido::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularTotal() {
        return calcularSubtotal()
                .subtract(descuento.valor())
                .add(impuestos.valor())
                .add(costoEnvio.valor());
    }

    public String getId() {
        return id;
    }

    public String getClienteId() {
        return clienteId;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public List<DetallePedido> getDetalles() {
        return List.copyOf(detalles);
    }

    public String getCuponId() {
        return cuponId;
    }

    public Precio getDescuento() {
        return descuento;
    }

    public Precio getImpuestos() {
        return impuestos;
    }

    public Precio getCostoEnvio() {
        return costoEnvio;
    }
    public String getPagoConfirmadoId() {
        return pagoConfirmadoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pedido otro)) return false;
        return id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}