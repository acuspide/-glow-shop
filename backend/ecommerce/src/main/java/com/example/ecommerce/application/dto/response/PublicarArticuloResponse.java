package com.example.ecommerce.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PublicarArticuloResponse(
        long id,
        String nombre,
        BigDecimal precio,
        long categoriaId,
        String categoriaNombre,
        long marcaId,
        String marcaNombre,
        String tono,
        List<String> tiposPiel,
        int cantidadDisponible,
        LocalDate fechaVencimiento,
        long tiendaId,
        String tiendaNombre,
        boolean publicado
) {
}
