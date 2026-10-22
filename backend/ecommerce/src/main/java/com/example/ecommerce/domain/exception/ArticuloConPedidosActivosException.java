package com.example.ecommerce.domain.exception;

public class ArticuloConPedidosActivosException extends ReglaDominioException {
    public ArticuloConPedidosActivosException(String articuloId) {
        super("No se puede eliminar el artículo con id: " + articuloId
                + " porque tiene pedidos activos.");
    }
}
