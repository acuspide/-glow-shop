package com.example.ecommerce.application.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.example.ecommerce.domain.valueobject.Tono;
import com.example.ecommerce.domain.valueobject.TipoPiel;

public record PublicarArticuloRequest(
        @NotBlank(message = "El nombre del artículo es obligatorio")
        String nombre,

        @NotNull(message = "El precio es obligatorio")
        @PositiveOrZero(message = "El precio no puede ser negativo")
        BigDecimal precio,

        @NotBlank(message = "El identificador de la categoría es obligatorio")
        String categoriaId,

        @NotBlank(message = "El identificador de la marca es obligatorio")
        String marcaId,

        @NotNull(message = "El tono es obligatorio")
        Tono tono,

        @NotNull(message = "Los tipos de piel son obligatorios")
        @Size(min = 1, message = "Debe seleccionar al menos un tipo de piel")
        List<TipoPiel> tiposPiel,

        @NotNull(message = "La cantidad disponible es obligatoria")
        @PositiveOrZero(message = "El inventario no puede ser negativo")
        Integer cantidadDisponible,

        @NotNull(message = "La fecha de vencimiento es obligatoria")
        LocalDate fechaVencimiento,

        @NotBlank(message = "El identificador de la tienda es obligatorio")
        String tiendaId
) {
}
