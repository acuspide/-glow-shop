package com.example.ecommerce.infrastructure.persistence;

import com.example.ecommerce.domain.repository.HistorialDeCompras;
import com.example.ecommerce.domain.repository.PedidoRepository;
import com.example.ecommerce.domain.valueobject.EstadoPedido;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adaptador: responde con los pedidos guardados si un cliente recibió un artículo.
 *
 * Nota: hoy Pedido guarda el cliente como número (long) y Usuario usa UUID (String);
 * por eso se compara como texto. Cuando Pedido pase a String se cambia solo esta clase.
 */
@Component
@RequiredArgsConstructor
public class HistorialDeComprasConPedidos implements HistorialDeCompras {

    private final PedidoRepository pedidoRepository;

    @Override
    public boolean clienteRecibioArticulo(String clienteId, long articuloId) {
        return pedidoRepository.listarTodos().stream()
                .filter(pedido -> String.valueOf(pedido.getClienteId()).equals(clienteId))
                .filter(pedido -> pedido.getEstado() == EstadoPedido.ENTREGADO)
                .anyMatch(pedido -> pedido.getDetalles().stream()
                        .anyMatch(detalle -> detalle.getArticuloId() == articuloId));
    }
}
