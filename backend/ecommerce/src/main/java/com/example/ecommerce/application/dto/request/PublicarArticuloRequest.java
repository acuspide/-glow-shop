package com.example.ecommerce.application.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.example.ecommerce.domain.valueobject.Tono;
import com.example.ecommerce.domain.valueobject.TipoPiel;

public record PublicarArticuloRequest(
        @NotNull(message = "El identificador del artículo es obligatorio")
        @Positive(message = "El identificador debe ser mayor que cero")
        Long id,

        @NotBlank(message = "El nombre del artículo es obligatorio")
        String nombre,

        @NotNull(message = "El precio es obligatorio")
        @PositiveOrZero(message = "El precio no puede ser negativo")
        BigDecimal precio,

        @NotNull(message = "El identificador de la categoría es obligatorio")
        @Positive(message = "El identificador de la categoría debe ser mayor que cero")
        Long categoriaId,

        @NotBlank(message = "El nombre de la categoría es obligatorio")
        String categoriaNombre,

        @NotNull(message = "El identificador de la marca es obligatorio")
        @Positive(message = "El identificador de la marca debe ser mayor que cero")
        Long marcaId,

        @NotBlank(message = "El nombre de la marca es obligatorio")
        String marcaNombre,

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

        @NotNull(message = "El identificador de la tienda es obligatorio")
        @Positive(message = "El identificador de la tienda debe ser mayor que cero")
        Long tiendaId,

        @NotBlank(message = "El nombre de la tienda es obligatorio")
        String tiendaNombre
) {
}
