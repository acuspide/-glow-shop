package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.repository.PedidoRepository;
import com.example.ecommerce.domain.valueobject.DetallePedido;

import java.util.List;

public class ConfirmarPedidoUseCase {

    private final PedidoRepository repository;

    public ConfirmarPedidoUseCase(PedidoRepository repository) {
        this.repository = repository;
    }

    public Pedido ejecutar(long pedidoId, long clienteId, List<DetallePedido> detalles) {

        Pedido pedido = Pedido.crear(pedidoId, clienteId, detalles);

        pedido.confirmar();

        repository.guardar(pedido);

        return pedido;
    }
}
