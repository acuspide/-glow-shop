package com.example.ecommerce.application.dto.request;

import com.example.ecommerce.domain.valueobject.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

/**
 * Creación de un vendedor o administrador por parte de un administrador.
 */
public record CrearUsuarioConRolRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El correo electrónico es obligatorio")
        @Email(message = "El correo electrónico no tiene un formato válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        String contrasena,

        @NotBlank(message = "El teléfono es obligatorio")
        String telefono,

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser pasada")
        LocalDate fechaNacimiento,

        @NotNull(message = "El rol es obligatorio")
        RolUsuario rol
) {
}
