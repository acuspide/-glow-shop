package com.example.ecommerce.infrastructure.persistence;

import com.example.ecommerce.domain.entity.Calificacion;
import com.example.ecommerce.domain.repository.CalificacionRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

// Se usa en las pruebas unitarias. En la aplicación corre el adaptador JPA (persistence/jpa).
public class CalificacionRepositoryEnMemoria implements CalificacionRepository {

    private final Map<String, Calificacion> calificaciones = new ConcurrentHashMap<>();

    @Override
    public void guardar(Calificacion calificacion) {
        calificaciones.put(calificacion.getId(), calificacion);
    }

    @Override
    public Optional<Calificacion> obtenerPorId(String id) {
        return Optional.ofNullable(calificaciones.get(id));
    }

    @Override
    public boolean existePorClienteYArticulo(String clienteId, String articuloId) {
        return calificaciones.values().stream()
                .filter(c -> !c.isEliminada()) // si la eliminó, puede volver a calificar
                .anyMatch(c -> c.getClienteId().equals(clienteId) && c.getArticuloId().equals(articuloId));
    }

    @Override
    public List<Calificacion> listarPorArticulo(String articuloId) {
        return calificaciones.values().stream()
                .filter(c -> c.getArticuloId().equals(articuloId))
                .toList();
    }
}
