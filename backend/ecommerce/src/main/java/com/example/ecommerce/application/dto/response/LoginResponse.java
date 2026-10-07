package com.example.ecommerce.application.dto.response;

/**
 * Respuesta del login: el token que el cliente enviará como "Authorization: Bearer ...".
 */
public record LoginResponse(
        String token,
        String tipo,
        UsuarioResponse usuario
) {
}
