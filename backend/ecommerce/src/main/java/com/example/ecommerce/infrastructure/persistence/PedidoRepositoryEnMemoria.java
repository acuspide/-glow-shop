package com.example.ecommerce.infrastructure.persistence;

import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.repository.PedidoRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class PedidoRepositoryEnMemoria implements PedidoRepository {

    private final Map<Long, Pedido> pedidos = new HashMap<>();

    @Override
    public Optional<Pedido> obtenerPorId(long id) {
        return Optional.ofNullable(pedidos.get(id));
    }

    @Override
    public void guardar(Pedido pedido) {
        pedidos.put(pedido.getId(), pedido);
    }

    @Override
    public List<Pedido> listarTodos() {
        return List.copyOf(pedidos.values());
    }
}
