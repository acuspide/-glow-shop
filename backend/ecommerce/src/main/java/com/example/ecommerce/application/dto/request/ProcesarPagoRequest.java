package com.example.ecommerce.application.dto.request;

import com.example.ecommerce.domain.valueobject.MedioPago;
import jakarta.validation.constraints.NotNull;

public record ProcesarPagoRequest(

        @NotNull(message = "El medio de pago es obligatorio")
        MedioPago medioPago
) {
}
