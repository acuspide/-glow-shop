package com.example.ecommerce.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CambiarContrasenaRequest(

        @NotBlank(message = "La contraseña actual es obligatoria")
        String contrasenaActual,

        @NotBlank(message = "La nueva contraseña es obligatoria")
        String nuevaContrasena
) {
}
