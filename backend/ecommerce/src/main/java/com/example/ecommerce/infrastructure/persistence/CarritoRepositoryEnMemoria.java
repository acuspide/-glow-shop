package com.example.ecommerce.infrastructure.persistence;

import com.example.ecommerce.domain.entity.Carrito;
import com.example.ecommerce.domain.repository.CarritoRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CarritoRepositoryEnMemoria implements CarritoRepository {

    private final Map<Long, Carrito> carritos = new HashMap<>();
    private final AtomicLong secuencia = new AtomicLong(0);

    @Override
    public Optional<Carrito> obtenerPorId(long id) {
        return Optional.ofNullable(carritos.get(id));
    }

    @Override
    public Optional<Carrito> obtenerPorClienteId(long clienteId) {
        return carritos.values().stream()
                .filter(carrito -> carrito.getClienteId() == clienteId)
                .findFirst();
    }

    @Override
    public long siguienteId() {
        return secuencia.incrementAndGet();
    }

    @Override
    public void guardar(Carrito carrito) {
        carritos.put(carrito.getId(), carrito);
    }
}