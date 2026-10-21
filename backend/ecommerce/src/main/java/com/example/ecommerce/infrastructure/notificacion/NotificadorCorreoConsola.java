package com.example.ecommerce.infrastructure.notificacion;

import com.example.ecommerce.application.NotificadorCorreo;
import com.example.ecommerce.domain.valueobject.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Implementación temporal: en lugar de enviar el correo, lo escribe en el log de la aplicación.
 * Se reemplaza por un envío real (SMTP) sin tocar los casos de uso.
 */
@Component
public class NotificadorCorreoConsola implements NotificadorCorreo {

    private static final Logger log = LoggerFactory.getLogger(NotificadorCorreoConsola.class);

    @Override
    public void enviarCodigoRecuperacion(Email destinatario, String codigo, int minutosVigencia) {
        log.info("[CORREO SIMULADO] Para: {} | Tu código de recuperación es {} (vence en {} minutos)",
                destinatario.getValor(), codigo, minutosVigencia);
    }
}
