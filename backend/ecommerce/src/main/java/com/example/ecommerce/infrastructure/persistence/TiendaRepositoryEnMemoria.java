package com.example.ecommerce.infrastructure.persistence;

import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.domain.repository.TiendaRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

// Se usa en las pruebas unitarias. En la aplicación corre el adaptador JPA (persistence/jpa).
public class TiendaRepositoryEnMemoria implements TiendaRepository {

    private final Map<String, Tienda> tiendas = new ConcurrentHashMap<>();

    @Override
    public void guardar(Tienda tienda) {
        tiendas.put(tienda.getId(), tienda);
    }

    @Override
    public Optional<Tienda> obtenerPorId(String id) {
        return Optional.ofNullable(tiendas.get(id));
    }

    @Override
    public boolean existePorVendedorId(String vendedorId) {
        return tiendas.values().stream()
                .anyMatch(tienda -> tienda.getVendedorId().equals(vendedorId));
    }

    @Override
    public Optional<Tienda> obtenerPorVendedorId(String vendedorId) {
        return tiendas.values().stream()
                .filter(tienda -> tienda.getVendedorId().equals(vendedorId))
                .findFirst();
    }
}
