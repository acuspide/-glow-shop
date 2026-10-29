package com.example.ecommerce.infrastructure.persistence.jpa.mapper;

import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.valueobject.DetallePedido;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.infrastructure.persistence.jpa.entity.LineaArticuloEmbeddable;
import com.example.ecommerce.infrastructure.persistence.jpa.entity.PedidoEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class PedidoPersistenceMapper {

    public PedidoEntity toEntity(Pedido pedido) {
        PedidoEntity entity = new PedidoEntity();
        entity.setId(pedido.getId());
        entity.setClienteId(pedido.getClienteId());
        entity.setDetalles(new ArrayList<>(pedido.getDetalles().stream()
                .map(detalle -> new LineaArticuloEmbeddable(
                        detalle.getArticuloId(), detalle.getCantidad(), detalle.getPrecioUnitario().valor()))
                .toList()));
        entity.setEstado(pedido.getEstado());
        entity.setCuponId(pedido.getCuponId());
        entity.setDescuento(pedido.getDescuento().valor());
        entity.setImpuestos(pedido.getImpuestos().valor());
        entity.setCostoEnvio(pedido.getCostoEnvio().valor());
        entity.setPagoConfirmadoId(pedido.getPagoConfirmadoId());
        return entity;
    }

    public Pedido toDomain(PedidoEntity entity) {
        return Pedido.reconstruir(
                entity.getId(),
                entity.getClienteId(),
                entity.getDetalles().stream()
                        .map(linea -> new DetallePedido(
                                linea.getArticuloId(), linea.getCantidad(), new Precio(linea.getPrecioUnitario())))
                        .toList(),
                entity.getEstado(),
                entity.getCuponId(),
                new Precio(entity.getDescuento()),
                new Precio(entity.getImpuestos()),
                new Precio(entity.getCostoEnvio()),
                entity.getPagoConfirmadoId());
    }
}
