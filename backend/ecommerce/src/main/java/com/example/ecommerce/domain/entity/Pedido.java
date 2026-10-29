package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.*;
import com.example.ecommerce.domain.valueobject.DetallePedido;
import com.example.ecommerce.domain.valueobject.EstadoPedido;
import com.example.ecommerce.domain.valueobject.Precio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Pedido {
    private final String id;
    private final String clienteId;
    private final String tiendaId;   // un pedido por tienda: el vendedor de esa tienda lo despacha
    private final List<DetallePedido> detalles;
    private EstadoPedido estado;
    private String cuponId;
    private Precio descuento;
    private Precio impuestos;
    private Precio costoEnvio;
    private String pagoConfirmadoId;

    private Pedido(String id, String clienteId, String tiendaId, List<DetallePedido> detalles, EstadoPedido estado,
                   String cuponId, Precio descuento, Precio impuestos, Precio costoEnvio,
                   String pagoConfirmadoId) {
        this.id = id;
        this.clienteId = clienteId;
        this.tiendaId = tiendaId;
        this.detalles = new ArrayList<>(detalles);
        this.estado = estado;
        this.cuponId = cuponId;
        this.descuento = descuento;
        this.impuestos = impuestos;
        this.costoEnvio = costoEnvio;
        this.pagoConfirmadoId = pagoConfirmadoId;
    }

    public static Pedido crear(String id, String clienteId, String tiendaId, List<DetallePedido> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            throw new PedidoSinDetallesException();
        }
        if (tiendaId == null || tiendaId.isBlank()) {
            throw new TiendaRequeridaException();
        }
        Precio cero = new Precio(BigDecimal.ZERO);
        Pedido pedido = new Pedido(id, clienteId, tiendaId, List.of(), EstadoPedido.PENDIENTE,
                null, cero, cero, cero, null);
        detalles.forEach(pedido::agregarDetalle);
        return pedido;
    }

    public static Pedido reconstruir(String id, String clienteId, String tiendaId, List<DetallePedido> detalles,
                                     EstadoPedido estado, String cuponId, Precio descuento,
                                     Precio impuestos, Precio costoEnvio, String pagoConfirmadoId) {
        return new Pedido(id, clienteId, tiendaId, detalles, estado, cuponId, descuento, impuestos,
                costoEnvio, pagoConfirmadoId);
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


    public void verificarQuePerteneceA(String clienteId) {
        if (!this.clienteId.equals(clienteId)) {
            throw new PedidoAjenoException();
        }
    }
    public void verificarQueAceptaPago() {
        if (estado != EstadoPedido.CONFIRMADO || pagoConfirmadoId != null) {
            throw new PagoNoRegistrableException();
        }
    }

    /** Un pago aprobado confirma el pedido; un pago rechazado lo deja como estaba, esperando otro intento. */
    public void registrarSiFueAprobado(Pago pago) {
        if (pago.estaAprobado()) {
            registrarPago(pago);
        }
    }

    public void registrarPago(Pago pago) {
        verificarQueAceptaPago();
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

    /**
     * El vendedor despacha su pedido paso a paso: EN_PREPARACION -> ENVIADO -> ENTREGADO.
     * Cada paso conserva sus propias reglas (para preparar, el pago debe estar registrado).
     * Cualquier otro destino no lo puede elegir el vendedor.
     */
    public void avanzarA(EstadoPedido destino) {
        switch (destino) {
            case EN_PREPARACION -> iniciarPreparacion();
            case ENVIADO -> enviar();
            case ENTREGADO -> entregar();
            default -> throw new TransicionEstadoInvalidaException(estado.name(), destino.name());
        }
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

    public String getTiendaId() {
        return tiendaId;
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