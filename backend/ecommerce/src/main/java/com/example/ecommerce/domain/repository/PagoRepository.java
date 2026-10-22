package com.example.ecommerce.domain.repository;

import com.example.ecommerce.domain.entity.Pago;

import java.util.Optional;

public interface PagoRepository {
    Optional<Pago> obtenerPorId(String id);

    void guardar(Pago pago);
}
