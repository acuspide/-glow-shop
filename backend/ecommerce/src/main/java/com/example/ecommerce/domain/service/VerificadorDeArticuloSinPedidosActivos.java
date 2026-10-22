package com.example.ecommerce.domain.service;

import com.example.ecommerce.domain.exception.ArticuloConPedidosActivosException;
import com.example.ecommerce.domain.repository.PedidoRepository;

/**
 * Servicio de dominio: un artículo no se puede eliminar mientras esté en un pedido activo
 * (que todavía no se entregó ni se canceló). La regla necesita mirar los pedidos, por eso
 * no cabe dentro de la entidad Articulo.
 */
public class VerificadorDeArticuloSinPedidosActivos {

    private final PedidoRepository pedidoRepository;

    public VerificadorDeArticuloSinPedidosActivos(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public void verificarQuePuedeEliminarse(long articuloId) {
        boolean tienePedidosActivos = pedidoRepository.listarTodos().stream()
                .filter(pedido -> !pedido.getEstado().esFinal())
                .anyMatch(pedido -> pedido.getDetalles().stream()
                        .anyMatch(detalle -> detalle.getArticuloId() == articuloId));

        if (tienePedidosActivos) {
            throw new ArticuloConPedidosActivosException(articuloId);
        }
    }
}
