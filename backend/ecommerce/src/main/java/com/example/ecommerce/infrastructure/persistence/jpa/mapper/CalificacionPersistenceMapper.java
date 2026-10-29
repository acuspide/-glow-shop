package com.example.ecommerce.infrastructure.persistence.jpa.mapper;

import com.example.ecommerce.domain.entity.Calificacion;
import com.example.ecommerce.domain.valueobject.Puntuacion;
import com.example.ecommerce.infrastructure.persistence.jpa.entity.CalificacionEntity;
import org.springframework.stereotype.Component;

@Component
public class CalificacionPersistenceMapper {

    public CalificacionEntity toEntity(Calificacion calificacion) {
        CalificacionEntity entity = new CalificacionEntity();
        entity.setId(calificacion.getId());
        entity.setArticuloId(calificacion.getArticuloId());
        entity.setClienteId(calificacion.getClienteId());
        entity.setPuntuacion(calificacion.getPuntuacion().valor());
        entity.setComentario(calificacion.getComentario());
        entity.setFecha(calificacion.getFecha());
        entity.setEliminada(calificacion.isEliminada());
        return entity;
    }

    public Calificacion toDomain(CalificacionEntity entity) {
        return Calificacion.reconstruir(
                entity.getId(),
                entity.getArticuloId(),
                entity.getClienteId(),
                new Puntuacion(entity.getPuntuacion()),
                entity.getComentario(),
                entity.getFecha(),
                entity.isEliminada());
    }
}
