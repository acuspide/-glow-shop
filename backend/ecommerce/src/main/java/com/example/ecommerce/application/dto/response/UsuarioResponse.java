package com.example.ecommerce.application.dto.response;

import com.example.ecommerce.domain.valueobject.RolUsuario;

import java.time.LocalDate;

/**
 * Vista pública de un usuario. A propósito NO incluye la contraseña ni su hash.
 */
public record UsuarioResponse(
        long id,
        String nombre,
        String email,
        String telefono,
        LocalDate fechaNacimiento,
        RolUsuario rol,
        boolean activo
) {
}
