package com.example.ecommerce.domain.valueobject;

/**
 * RN09: estados de una tienda. Nace PENDIENTE y solo publica cuando está ACTIVA (RN10).
 * SUSPENDIDA e INACTIVA quedan definidas para cuando se implementen esos casos de uso.
 */
public enum EstadoTienda {
    PENDIENTE,
    ACTIVA,
    SUSPENDIDA,
    INACTIVA
}
