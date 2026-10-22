package com.example.ecommerce.domain.exception;

public class MotivoRechazoRequeridoException extends ReglaDominioException {
    public MotivoRechazoRequeridoException() {
        super("Un pago rechazado debe indicar el motivo del rechazo.");
    }
}