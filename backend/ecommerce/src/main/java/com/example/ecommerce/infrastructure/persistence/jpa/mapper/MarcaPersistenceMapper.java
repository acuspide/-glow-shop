package com.example.ecommerce.infrastructure.persistence.jpa.mapper;

import com.example.ecommerce.domain.entity.Marca;
import com.example.ecommerce.infrastructure.persistence.jpa.entity.MarcaEntity;
import org.springframework.stereotype.Component;

@Component
public class MarcaPersistenceMapper {

    public MarcaEntity toEntity(Marca marca) {
        MarcaEntity entity = new MarcaEntity();
        entity.setId(marca.getId());
        entity.setNombre(marca.getNombre());
        return entity;
    }

    public Marca toDomain(MarcaEntity entity) {
        return Marca.reconstruir(entity.getId(), entity.getNombre());
    }
}
