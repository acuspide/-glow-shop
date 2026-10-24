package com.example.ecommerce.domain.repository;

import com.example.ecommerce.domain.entity.Pedido;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository {
    Optional<Pedido> obtenerPorId(String id);

    void guardar(Pedido pedido);

    List<Pedido> listarTodos();
    List<Pedido> obtenerPorClienteId(String clienteId);
}

