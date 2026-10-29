package com.example.ecommerce.infrastructure.persistence;

import com.example.ecommerce.domain.entity.Carrito;
import com.example.ecommerce.domain.repository.CarritoRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// Se usa en las pruebas unitarias. En la aplicación corre el adaptador JPA (persistence/jpa).
public class CarritoRepositoryEnMemoria implements CarritoRepository {

    private final Map<String, Carrito> carritos = new HashMap<>();

    @Override
    public Optional<Carrito> obtenerPorId(String id) {
        return Optional.ofNullable(carritos.get(id));
    }

    @Override
    public Optional<Carrito> obtenerPorClienteId(String clienteId) {
        return carritos.values().stream()
                .filter(carrito -> carrito.getClienteId().equals(clienteId))
                .findFirst();
    }

    @Override
    public void guardar(Carrito carrito) {
        carritos.put(carrito.getId(), carrito);
    }
}