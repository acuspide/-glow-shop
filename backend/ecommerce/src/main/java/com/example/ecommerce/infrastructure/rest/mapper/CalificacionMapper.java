package com.example.ecommerce.infrastructure.rest.mapper;

import com.example.ecommerce.application.dto.response.CalificacionResponse;
import com.example.ecommerce.domain.entity.Calificacion;
import org.springframework.stereotype.Component;

@Component
public class CalificacionMapper {

    public CalificacionResponse toResponse(Calificacion calificacion) {
        return new CalificacionResponse(
                calificacion.getId(),
                calificacion.getArticuloId(),
                calificacion.getClienteId(),
                calificacion.getPuntuacion().valor(),
                calificacion.getComentario(),
                calificacion.getFecha()
        );
    }
}
