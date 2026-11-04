package com.example.ecommerce.application;

/**
 * Un parámetro de la petición no es válido (por ejemplo, una página negativa o un límite fuera de rango).
 * No es una regla del negocio sino una validación de entrada del caso de uso, por eso no extiende
 * ReglaDominioException: el manejador global la convierte en 400 y no en 422.
 */
public class ParametroInvalidoException extends RuntimeException {
    public ParametroInvalidoException(String mensaje) {
        super(mensaje);
    }
}
