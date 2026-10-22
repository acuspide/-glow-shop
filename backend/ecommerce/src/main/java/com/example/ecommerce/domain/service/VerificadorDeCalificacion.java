package com.example.ecommerce.domain.service;

import com.example.ecommerce.domain.exception.ArticuloNoRecibidoException;
import com.example.ecommerce.domain.exception.CalificacionDuplicadaException;
import com.example.ecommerce.domain.repository.CalificacionRepository;
import com.example.ecommerce.domain.repository.HistorialDeCompras;

/**
 * Servicio de dominio con las reglas de reseñas que necesitan mirar otros datos:
 *  - RN75: solo califica quien compró el artículo y ya lo recibió;
 *  - RN77: un cliente califica cada artículo una sola vez (después puede editarla).
 */
public class VerificadorDeCalificacion {

    private final HistorialDeCompras historialDeCompras;
    private final CalificacionRepository calificacionRepository;

    public VerificadorDeCalificacion(HistorialDeCompras historialDeCompras,
                                     CalificacionRepository calificacionRepository) {
        this.historialDeCompras = historialDeCompras;
        this.calificacionRepository = calificacionRepository;
    }

    public void verificarQuePuedeCalificar(String clienteId, String articuloId) {
        if (!historialDeCompras.clienteRecibioArticulo(clienteId, articuloId)) {
            throw new ArticuloNoRecibidoException();
        }
        if (calificacionRepository.existePorClienteYArticulo(clienteId, articuloId)) {
            throw new CalificacionDuplicadaException();
        }
    }
}
