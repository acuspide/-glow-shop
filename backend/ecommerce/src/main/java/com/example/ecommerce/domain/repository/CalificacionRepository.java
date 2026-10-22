package com.example.ecommerce.domain.repository;

import com.example.ecommerce.domain.entity.Calificacion;

import java.util.List;
import java.util.Optional;

public interface CalificacionRepository {

    void guardar(Calificacion calificacion);

    Optional<Calificacion> obtenerPorId(String id);

    /** Solo cuenta las calificaciones vigentes (no eliminadas). */
    boolean existePorClienteYArticulo(String clienteId, String articuloId);

    List<Calificacion> listarPorArticulo(String articuloId);
}
