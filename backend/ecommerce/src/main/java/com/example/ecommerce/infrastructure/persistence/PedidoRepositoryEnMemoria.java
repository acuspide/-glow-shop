package com.example.ecommerce.infrastructure.persistence;

import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.repository.PedidoRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Se usa en las pruebas unitarias. En la aplicación corre el adaptador JPA (persistence/jpa).
public class PedidoRepositoryEnMemoria implements PedidoRepository {

    private final Map<String, Pedido> pedidos = new HashMap<>();

    @Override
    public Optional<Pedido> obtenerPorId(String id) {
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
    @Override
    public List<Pedido> obtenerPorClienteId(String clienteId) {
        return pedidos.values().stream()
                .filter(p -> p.getClienteId().equals(clienteId))
                .toList();
    }

    @Override
    public List<Pedido> obtenerPorTiendaId(String tiendaId) {
        return pedidos.values().stream()
                .filter(p -> p.getTiendaId().equals(tiendaId))
                .toList();
    }
}
