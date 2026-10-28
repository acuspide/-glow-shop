package com.example.ecommerce.domain.exception;

/**
 * Se busca algo por su id y no existe (artículo, carrito, pedido, usuario...).
 * El GlobalExceptionHandler la convierte en 404 con este mensaje.
 */
public class RecursoNoEncontradoException extends ReglaDominioException {

    public RecursoNoEncontradoException(String recurso, String id) {
        super("No se encontró " + recurso + " con id: " + id);
    }
}
