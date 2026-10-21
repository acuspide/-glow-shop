package com.example.ecommerce.domain.repository;

/**
 * Puerto para preguntar por las compras de un cliente sin depender de cómo se guardan los pedidos.
 */
public interface HistorialDeCompras {

    /** true si el cliente tiene un pedido ENTREGADO que incluye el artículo. */
    boolean clienteRecibioArticulo(String clienteId, long articuloId);
}
