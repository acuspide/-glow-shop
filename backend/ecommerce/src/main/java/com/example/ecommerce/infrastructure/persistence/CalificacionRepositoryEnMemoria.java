package com.example.ecommerce.infrastructure.persistence;

import com.example.ecommerce.domain.entity.Calificacion;
import com.example.ecommerce.domain.repository.CalificacionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
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
    public boolean existePorClienteYArticulo(String clienteId, long articuloId) {
        return calificaciones.values().stream()
                .filter(c -> !c.isEliminada()) // si la eliminó, puede volver a calificar
                .anyMatch(c -> c.getClienteId().equals(clienteId) && c.getArticuloId() == articuloId);
    }

    @Override
    public List<Calificacion> listarPorArticulo(long articuloId) {
        return calificaciones.values().stream()
                .filter(c -> c.getArticuloId() == articuloId)
                .toList();
    }
}
