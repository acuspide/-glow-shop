package com.example.ecommerce.infrastructure.rest.mapper;

import com.example.ecommerce.application.dto.response.PagoResponse;
import com.example.ecommerce.domain.entity.Pago;
import org.springframework.stereotype.Component;

@Component
public class PagoMapper {

    public PagoResponse toResponse(Pago pago) {
        return new PagoResponse(
                pago.getId(),
                pago.getPedidoId(),
                pago.getMonto().valor(),
                pago.getMedioPago().name(),
                pago.getFechaPago(),
                pago.getEstado().name(),
                pago.getTransaccionId(),
                pago.getMotivoRechazo()
        );
    }

}
