package com.example.ecommerce.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CalificarArticuloRequest(

        @NotNull(message = "La calificación es obligatoria")
        @Min(value = 1, message = "La calificación mínima es 1 estrella")
        @Max(value = 5, message = "La calificación máxima es 5 estrellas")
        Integer puntuacion,

        @NotBlank(message = "El comentario es obligatorio")
        @Size(max = 500, message = "El comentario no puede superar los 500 caracteres")
        String comentario
) {
}
