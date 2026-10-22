package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.domain.exception.TokenInvalidoException;

/**
 * Lee el token del encabezado "Authorization: Bearer <token>".
 */
final class BearerToken {

    private static final String PREFIJO = "Bearer ";

    private BearerToken() {
    }

    static String extraer(String autorizacion) {
        if (autorizacion == null || !autorizacion.startsWith(PREFIJO)) {
            throw new TokenInvalidoException();
        }
        return autorizacion.substring(PREFIJO.length()).trim();
    }
}
