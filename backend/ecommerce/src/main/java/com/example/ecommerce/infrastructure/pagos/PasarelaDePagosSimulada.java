package com.example.ecommerce.infrastructure.pagos;

import com.example.ecommerce.application.PasarelaDePagos;
import com.example.ecommerce.application.ResultadoCobro;
import com.example.ecommerce.domain.valueobject.MedioPago;
import com.example.ecommerce.domain.valueobject.Precio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Implementación temporal: no se conecta con ningún banco. Hace de cuenta que toda
 * clienta tiene $1.000.000 disponibles: aprueba los cobros hasta ese valor y rechaza
 * los mayores por fondos insuficientes.
 * Se reemplaza por una pasarela real sin tocar los casos de uso.
 */
@Component
public class PasarelaDePagosSimulada implements PasarelaDePagos {

    private static final Logger log = LoggerFactory.getLogger(PasarelaDePagosSimulada.class);
    private static final BigDecimal SALDO_DISPONIBLE = new BigDecimal("1000000");
    private static final String FONDOS_INSUFICIENTES = "Fondos insuficientes";

    @Override
    public ResultadoCobro cobrar(Precio monto, MedioPago medioPago) {
        String transaccionId = "SIM-" + UUID.randomUUID();
        boolean alcanzanLosFondos = monto.valor().compareTo(SALDO_DISPONIBLE) <= 0;

        if (alcanzanLosFondos) {
            log.info("[PAGO SIMULADO] {} por {} con {} -> APROBADO", transaccionId, monto.valor(), medioPago);
            return new ResultadoCobro(true, transaccionId, null);
        }

        log.info("[PAGO SIMULADO] {} por {} con {} -> RECHAZADO: {}",
                transaccionId, monto.valor(), medioPago, FONDOS_INSUFICIENTES);
        return new ResultadoCobro(false, transaccionId, FONDOS_INSUFICIENTES);
    }
}