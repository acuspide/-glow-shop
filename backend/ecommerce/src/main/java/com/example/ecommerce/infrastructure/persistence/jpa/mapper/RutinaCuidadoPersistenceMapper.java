package com.example.ecommerce.infrastructure.persistence.jpa.mapper;

import com.example.ecommerce.domain.entity.RutinaCuidado;
import com.example.ecommerce.infrastructure.persistence.jpa.entity.ArticuloEntity;
import com.example.ecommerce.infrastructure.persistence.jpa.entity.RutinaCuidadoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RutinaCuidadoPersistenceMapper {

    private final ArticuloPersistenceMapper articuloMapper;

    /** Los artículos llegan como referencias a filas que ya existen en la base de datos. */
    public RutinaCuidadoEntity toEntity(RutinaCuidado rutina, List<ArticuloEntity> articulos) {
        RutinaCuidadoEntity entity = new RutinaCuidadoEntity();
        entity.setId(rutina.getId());
        entity.setNombre(rutina.getNombre());
        entity.setArticulos(new ArrayList<>(articulos));
        return entity;
    }

    public RutinaCuidado toDomain(RutinaCuidadoEntity entity) {
        return RutinaCuidado.reconstruir(
                entity.getId(),
                entity.getNombre(),
                entity.getArticulos().stream().map(articuloMapper::toDomain).toList());
    }
}
