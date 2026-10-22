package com.example.ecommerce.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PublicarArticuloResponse(
        String id,
        String nombre,
        BigDecimal precio,
        String categoriaId,
        String categoriaNombre,
        String marcaId,
        String marcaNombre,
        String tono,
        List<String> tiposPiel,
        int cantidadDisponible,
        LocalDate fechaVencimiento,
        String tiendaId,
        String tiendaNombre,
        boolean publicado
) {
}
