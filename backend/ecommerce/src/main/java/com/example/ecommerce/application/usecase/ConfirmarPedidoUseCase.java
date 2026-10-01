package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.exception.PedidoSinDetallesException;
import com.example.ecommerce.domain.repository.PedidoRepository;
import com.example.ecommerce.domain.valueobject.DetallePedido;

import java.util.List;

public class ConfirmarPedidoUseCase {

    private final PedidoRepository repository;

    public ConfirmarPedidoUseCase(PedidoRepository repository) {
        this.repository = repository;
    }

    public Pedido ejecutar(long pedidoId, long clienteId, List<DetallePedido> detalles) {

        if (detalles == null || detalles.isEmpty()) {
            throw new PedidoSinDetallesException();
        }

        Pedido pedido = new Pedido(pedidoId, clienteId, detalles.get(0));

        detalles.stream().skip(1).forEach(pedido::agregarDetalle);

        pedido.confirmar();

        repository.guardar(pedido);

        return pedido;
    }
}
