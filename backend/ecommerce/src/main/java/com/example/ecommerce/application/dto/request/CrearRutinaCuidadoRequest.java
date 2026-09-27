package com.example.ecommerce.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CrearRutinaCuidadoRequest(

        @NotBlank(message = "El nombre de la rutina es obligatorio")
        String nombre,

        @NotNull(message = "Los artículos son obligatorios")
        @Size(min = 2, message = "La rutina debe tener mínimo 2 artículos")
        List<Long> articulosIds
) {
}
