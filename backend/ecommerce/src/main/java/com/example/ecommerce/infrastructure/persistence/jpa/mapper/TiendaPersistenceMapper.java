package com.example.ecommerce.infrastructure.persistence.jpa.mapper;

import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.infrastructure.persistence.jpa.entity.TiendaEntity;
import org.springframework.stereotype.Component;

@Component
public class TiendaPersistenceMapper {

    public TiendaEntity toEntity(Tienda tienda) {
        TiendaEntity entity = new TiendaEntity();
        entity.setId(tienda.getId());
        entity.setNombre(tienda.getNombre());
        entity.setDescripcion(tienda.getDescripcion());
        entity.setVendedorId(tienda.getVendedorId());
        entity.setEstado(tienda.getEstado());
        return entity;
    }

    public Tienda toDomain(TiendaEntity entity) {
        return Tienda.reconstruir(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getVendedorId(),
                entity.getEstado());
    }
}
