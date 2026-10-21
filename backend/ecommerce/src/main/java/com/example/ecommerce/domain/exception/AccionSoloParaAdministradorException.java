package com.example.ecommerce.domain.exception;

public class AccionSoloParaAdministradorException extends ReglaDominioException {
    public AccionSoloParaAdministradorException() {
        super("Solo un administrador activo puede realizar esta acción.");
    }
}
