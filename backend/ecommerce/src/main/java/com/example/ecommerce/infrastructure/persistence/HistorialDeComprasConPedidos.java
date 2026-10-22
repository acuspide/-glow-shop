package com.example.ecommerce.infrastructure.persistence;

import com.example.ecommerce.domain.repository.HistorialDeCompras;
import com.example.ecommerce.domain.repository.PedidoRepository;
import com.example.ecommerce.domain.valueobject.EstadoPedido;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adaptador: responde con los pedidos guardados si un cliente recibió un artículo.
 */
@Component
@RequiredArgsConstructor
public class HistorialDeComprasConPedidos implements HistorialDeCompras {

    private final PedidoRepository pedidoRepository;

    @Override
    public boolean clienteRecibioArticulo(String clienteId, String articuloId) {
        return pedidoRepository.listarTodos().stream()
                .filter(pedido -> pedido.getClienteId().equals(clienteId))
                .filter(pedido -> pedido.getEstado() == EstadoPedido.ENTREGADO)
                .anyMatch(pedido -> pedido.getDetalles().stream()
                        .anyMatch(detalle -> detalle.getArticuloId().equals(articuloId)));
    }
}
