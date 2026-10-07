package com.example.ecommerce.application;

import com.example.ecommerce.domain.entity.Usuario;

/**
 * Puerto para los tokens de sesión. La implementación (JWT) vive en infrastructure.
 */
public interface TokenService {

    String generar(Usuario usuario);

    /** @throws com.example.ecommerce.domain.exception.TokenInvalidoException si es falso, alterado o expiró. */
    long extraerUsuarioId(String token);
}
