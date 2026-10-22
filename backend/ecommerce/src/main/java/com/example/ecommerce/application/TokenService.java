package com.example.ecommerce.application;

import com.example.ecommerce.domain.entity.Usuario;

/**
 * Puerto para los tokens de sesión. La implementación (JWT) vive en infrastructure.
 */
public interface TokenService {

    String generar(Usuario usuario);

    /** @throws com.example.ecommerce.domain.exception.TokenInvalidoException si es falso, alterado o expiró. */
    String extraerUsuarioId(String token);

    /**
     * Invalida el token antes de que expire (cierre de sesión).
     * @throws com.example.ecommerce.domain.exception.TokenInvalidoException si ya era falso, alterado o expiró.
     */
    void revocar(String token);
}
