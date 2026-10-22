package com.example.ecommerce.domain.valueobject;

import com.example.ecommerce.domain.entity.Calificacion;

import java.util.Comparator;
import java.util.List;

/**
 * Calificaciones de un artículo con su promedio visible.
 * Las calificaciones quedan ordenadas de la más reciente a la más antigua
 * y no incluye las eliminadas (ni se muestran ni cuentan en el promedio).
 */
public record ResumenCalificaciones(String articuloId, List<Calificacion> calificaciones) {

    public ResumenCalificaciones {
        calificaciones = calificaciones.stream()
                .filter(calificacion -> !calificacion.isEliminada())
                .sorted(Comparator.comparing(Calificacion::getFecha).reversed())
                .toList();
    }

    /** Promedio de estrellas con un decimal; 0.0 si el artículo todavía no tiene calificaciones. */
    public double promedio() {
        double promedio = calificaciones.stream()
                .mapToInt(calificacion -> calificacion.getPuntuacion().valor())
                .average()
                .orElse(0.0);
        return Math.round(promedio * 10) / 10.0;
    }

    public int total() {
        return calificaciones.size();
    }
}
