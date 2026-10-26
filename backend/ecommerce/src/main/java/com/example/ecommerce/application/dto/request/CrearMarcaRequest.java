package com.example.ecommerce.application.dto.request;
import jakarta.validation.constraints.NotBlank;

public record CrearMarcaRequest(
        @NotBlank(message = "El nombre de la marca es obligatorio")
        String nombre
) {
}
