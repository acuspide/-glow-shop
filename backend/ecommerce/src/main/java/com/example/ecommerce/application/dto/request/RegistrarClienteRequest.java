package com.example.ecommerce.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

/**
 * Autorregistro de un cliente. No incluye el rol: quien se registra solo queda como CLIENTE.
 */
public record RegistrarClienteRequest(

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
        LocalDate fechaNacimiento
) {
}
