package com.example.ecommerce.infrastructure.rest.mapper;

import com.example.ecommerce.application.dto.response.DetallePedidoResponse;
import com.example.ecommerce.application.dto.response.PedidoResponse;
import com.example.ecommerce.domain.entity.Pedido;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PedidoMapper {

    public PedidoResponse toResponse(Pedido pedido) {

        List<DetallePedidoResponse> detalles = pedido.getDetalles()
                .stream()
                .map(detalle -> new DetallePedidoResponse(
                        detalle.getArticuloId(),
                        detalle.getCantidad(),
                        detalle.getPrecioUnitario().valor(),
                        detalle.subtotal()
                ))
                .toList();

        return new PedidoResponse(
                pedido.getId(),
                pedido.getClienteId(),
                pedido.getEstado().name(),
                detalles,
                pedido.calcularSubtotal(),
                pedido.calcularTotal()
        );
    }
}
