package com.example.ecommerce.infrastructure.rest.mapper;

import com.example.ecommerce.application.dto.response.CalificacionResponse;
import com.example.ecommerce.application.dto.response.CalificacionesArticuloResponse;
import com.example.ecommerce.domain.entity.Calificacion;
import com.example.ecommerce.domain.valueobject.ResumenCalificaciones;
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

    public CalificacionesArticuloResponse toResumenResponse(ResumenCalificaciones resumen) {
        return new CalificacionesArticuloResponse(
                resumen.articuloId(),
                resumen.promedio(),
                resumen.total(),
                resumen.calificaciones().stream().map(this::toResponse).toList()
        );
    }
}
