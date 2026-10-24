package com.example.ecommerce.domain.repository;

import com.example.ecommerce.domain.entity.Pago;

import java.util.Optional;
import java.util.List;

public interface PagoRepository {
    Optional<Pago> obtenerPorId(String id);

    void guardar(Pago pago);
    List<Pago> obtenerPorPedidoId(String pedidoId);
}
