package com.example.ecommerce.application.dto.request;

import com.example.ecommerce.domain.valueobject.MedioPago;

/** El medio de pago es opcional: si no se envía, se reintenta con el mismo del pago rechazado. */
public record ReintentarPagoRequest(
        MedioPago medioPago
) {
}