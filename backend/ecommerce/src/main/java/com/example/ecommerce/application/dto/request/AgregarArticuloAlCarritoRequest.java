package com.example.ecommerce.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AgregarArticuloAlCarritoRequest(

        @NotNull(message = "El artículo es obligatorio")
        Long articuloId,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser mayor que cero")
        Integer cantidad
) {
}
