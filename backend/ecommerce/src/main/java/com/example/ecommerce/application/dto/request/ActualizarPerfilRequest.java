package com.example.ecommerce.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ActualizarPerfilRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El teléfono es obligatorio")
        String telefono
) {
}
