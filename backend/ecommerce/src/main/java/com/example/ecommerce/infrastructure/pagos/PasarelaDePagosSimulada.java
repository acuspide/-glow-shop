package com.example.ecommerce.infrastructure.pagos;

import com.example.ecommerce.application.PasarelaDePagos;
import com.example.ecommerce.application.ResultadoCobro;
import com.example.ecommerce.domain.valueobject.MedioPago;
import com.example.ecommerce.domain.valueobject.Precio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

/**
 * Implementación temporal: no se conecta con ningún banco. Hace de cuenta que la
 * clienta tiene un saldo disponible distinto en cada medio de pago, y rechaza por
 * fondos insuficientes cuando el monto lo supera. Así, si un pago con Nequi se
 * rechaza, reintentarlo con tarjeta de crédito puede aprobarse.
 * Se reemplaza por una pasarela real sin tocar los casos de uso.
 */
@Component
public class PasarelaDePagosSimulada implements PasarelaDePagos {

    private static final Logger log = LoggerFactory.getLogger(PasarelaDePagosSimulada.class);
    private static final String FONDOS_INSUFICIENTES = "Fondos insuficientes";

    private static final Map<MedioPago, BigDecimal> SALDO_DISPONIBLE = Map.of(
            MedioPago.NEQUI, new BigDecimal("1000000"),
            MedioPago.TARJETA_DEBITO, new BigDecimal("1000000"),
            MedioPago.PSE, new BigDecimal("3000000"),
            MedioPago.TARJETA_CREDITO, new BigDecimal("5000000")
    );

    @Override
    public ResultadoCobro cobrar(Precio monto, MedioPago medioPago) {
        String transaccionId = "SIM-" + UUID.randomUUID();
        boolean alcanzanLosFondos = monto.valor().compareTo(SALDO_DISPONIBLE.get(medioPago)) <= 0;

        if (alcanzanLosFondos) {
            log.info("[PAGO SIMULADO] {} por {} con {} -> APROBADO", transaccionId, monto.valor(), medioPago);
            return new ResultadoCobro(true, transaccionId, null);
        }

        log.info("[PAGO SIMULADO] {} por {} con {} -> RECHAZADO: {}",
                transaccionId, monto.valor(), medioPago, FONDOS_INSUFICIENTES);
        return new ResultadoCobro(false, transaccionId, FONDOS_INSUFICIENTES);
    }
}