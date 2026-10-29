package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Listar mis pedidos: el historial de compras del cliente con sesión iniciada.
 * Solo devuelve sus pedidos; nunca los de otra persona.
 */
@Service
@RequiredArgsConstructor
public class ListarMisPedidosUseCase {

    private final PedidoRepository pedidoRepository;

    @Transactional(readOnly = true)
    public List<Pedido> ejecutar(String clienteId) {
        return pedidoRepository.obtenerPorClienteId(clienteId);
    }
}
