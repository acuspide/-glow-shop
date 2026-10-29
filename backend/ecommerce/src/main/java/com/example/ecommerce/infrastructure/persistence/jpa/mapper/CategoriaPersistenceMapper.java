package com.example.ecommerce.infrastructure.persistence.jpa.mapper;

import com.example.ecommerce.domain.entity.Categoria;
import com.example.ecommerce.infrastructure.persistence.jpa.entity.CategoriaEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoriaPersistenceMapper {

    public CategoriaEntity toEntity(Categoria categoria) {
        CategoriaEntity entity = new CategoriaEntity();
        entity.setId(categoria.getId());
        entity.setNombre(categoria.getNombre());
        return entity;
    }

    public Categoria toDomain(CategoriaEntity entity) {
        return Categoria.reconstruir(entity.getId(), entity.getNombre());
    }
}
