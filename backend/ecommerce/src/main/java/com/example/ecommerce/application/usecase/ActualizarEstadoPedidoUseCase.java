package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.example.ecommerce.domain.repository.PedidoRepository;
import com.example.ecommerce.domain.repository.TiendaRepository;
import com.example.ecommerce.domain.valueobject.EstadoPedido;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Actualizar estado del pedido (CU-75): el vendedor dueño de la tienda del pedido lo despacha
 * (en preparación -> enviado -> entregado). Con el pedido entregado, el cliente ya puede calificar.
 */
@Service
@RequiredArgsConstructor
public class ActualizarEstadoPedidoUseCase {

    private final PedidoRepository pedidoRepository;
    private final TiendaRepository tiendaRepository;

    @Transactional
    public Pedido ejecutar(String pedidoId, String vendedorId, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.obtenerPorId(pedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("el pedido", pedidoId));
        Tienda tienda = tiendaRepository.obtenerPorId(pedido.getTiendaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("la tienda", pedido.getTiendaId()));

        tienda.verificarQueEsDe(vendedorId);   // solo el vendedor de esa tienda
        pedido.avanzarA(nuevoEstado);          // el dominio valida la transición

        pedidoRepository.guardar(pedido);
        return pedido;
    }
}
