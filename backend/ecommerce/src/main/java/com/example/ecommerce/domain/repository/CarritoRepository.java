package com.example.ecommerce.domain.repository;

import com.example.ecommerce.domain.entity.Carrito;

import java.util.Optional;

public interface CarritoRepository {
    Optional<Carrito> obtenerPorId(long id);

    Optional<Carrito> obtenerPorClienteId(long clienteId);

    long siguienteId();

    void guardar(Carrito carrito);
}
