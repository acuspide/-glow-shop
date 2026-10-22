package com.example.ecommerce.application;

/**
 * Respuesta de la pasarela de pagos: si aprobó el cobro, el identificador de la
 * transacción (RN56) y, si lo rechazó, el motivo (por ejemplo, fondos insuficientes).
 */
public record ResultadoCobro(boolean aprobado, String transaccionId, String motivoRechazo) {
}
