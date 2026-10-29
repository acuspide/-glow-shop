package com.example.ecommerce.infrastructure.persistence.jpa.mapper;

import com.example.ecommerce.domain.entity.Pago;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.infrastructure.persistence.jpa.entity.PagoEntity;
import org.springframework.stereotype.Component;

@Component
public class PagoPersistenceMapper {

    public PagoEntity toEntity(Pago pago) {
        PagoEntity entity = new PagoEntity();
        entity.setId(pago.getId());
        entity.setPedidoId(pago.getPedidoId());
        entity.setMonto(pago.getMonto().valor());
        entity.setMedioPago(pago.getMedioPago());
        entity.setFechaPago(pago.getFechaPago());
        entity.setEstado(pago.getEstado());
        entity.setTransaccionId(pago.getTransaccionId());
        entity.setMotivoRechazo(pago.getMotivoRechazo());
        return entity;
    }

    public Pago toDomain(PagoEntity entity) {
        return Pago.reconstruir(
                entity.getId(),
                entity.getPedidoId(),
                new Precio(entity.getMonto()),
                entity.getMedioPago(),
                entity.getFechaPago(),
                entity.getEstado(),
                entity.getTransaccionId(),
                entity.getMotivoRechazo());
    }
}
