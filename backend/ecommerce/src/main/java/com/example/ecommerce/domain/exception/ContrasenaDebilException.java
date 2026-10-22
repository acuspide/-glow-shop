package com.example.ecommerce.domain.exception;

public class ContrasenaDebilException extends ReglaDominioException {
    public ContrasenaDebilException() {
        super("La contraseña debe tener mínimo 8 caracteres, al menos una mayúscula y al menos un número.");
    }
}
