package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.example.ecommerce.domain.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consultar pedido: el cliente ve el detalle y el estado de UNO de sus pedidos.
 * Si el pedido es de otra persona, el dominio lo rechaza (PedidoAjenoException -> 403).
 */
@Service
@RequiredArgsConstructor
public class ConsultarPedidoUseCase {

    private final PedidoRepository pedidoRepository;

    @Transactional(readOnly = true)
    public Pedido ejecutar(String pedidoId, String clienteId) {
        Pedido pedido = pedidoRepository.obtenerPorId(pedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("el pedido", pedidoId));
        pedido.verificarQuePerteneceA(clienteId);
        return pedido;
    }
}
