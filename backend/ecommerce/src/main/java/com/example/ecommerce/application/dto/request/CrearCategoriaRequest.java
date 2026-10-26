package com.example.ecommerce.application.dto.request;
import jakarta.validation.constraints.NotBlank;

public record CrearCategoriaRequest(
        @NotBlank(message = "El nombre de la categoría es obligatorio")
                                     String nombre
) {
}
