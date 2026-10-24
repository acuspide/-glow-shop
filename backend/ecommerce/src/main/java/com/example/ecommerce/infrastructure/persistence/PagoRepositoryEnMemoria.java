package com.example.ecommerce.infrastructure.persistence;

import com.example.ecommerce.domain.entity.Pago;
import com.example.ecommerce.domain.repository.PagoRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class PagoRepositoryEnMemoria implements PagoRepository {

    private final Map<String, Pago> pagos = new HashMap<>();

    @Override
    public Optional<Pago> obtenerPorId(String id) {
        return Optional.ofNullable(pagos.get(id));
    }

    @Override
    public void guardar(Pago pago) {
        pagos.put(pago.getId(), pago);
    }

    @Override
    public List<Pago> obtenerPorPedidoId(String pedidoId) {
        return pagos.values().stream()
                .filter(p -> p.getPedidoId().equals(pedidoId))
                .toList();
    }
}