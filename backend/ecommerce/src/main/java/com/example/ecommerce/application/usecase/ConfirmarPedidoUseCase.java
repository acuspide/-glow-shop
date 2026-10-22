package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.repository.PedidoRepository;
import com.example.ecommerce.domain.valueobject.DetallePedido;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfirmarPedidoUseCase {

    private final PedidoRepository repository;

    @Transactional
    public Pedido ejecutar(long pedidoId, long clienteId, List<DetallePedido> detalles) {

        Pedido pedido = Pedido.crear(pedidoId, clienteId, detalles);

        pedido.confirmar();

        repository.guardar(pedido);

        return pedido;
    }
}
