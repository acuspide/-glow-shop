package com.example.ecommerce.application;

import com.example.ecommerce.domain.valueobject.Email;

/**
 * Puerto para enviar correos. La implementación vive en infrastructure.
 */
public interface NotificadorCorreo {

    void enviarCodigoRecuperacion(Email destinatario, String codigo, int minutosVigencia);
}
