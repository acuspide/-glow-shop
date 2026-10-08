package com.example.ecommerce.application.dto.response;

import com.example.ecommerce.domain.valueobject.RolUsuario;

/**
 * Vista pública de un usuario. A propósito NO incluye la contraseña ni su hash.
 */
public record UsuarioResponse(
        long id,
        String nombre,
        String email,
        RolUsuario rol,
        boolean activo
) {
}
