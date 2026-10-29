package com.example.ecommerce.infrastructure.persistence.jpa.mapper;

import com.example.ecommerce.domain.entity.CodigoRecuperacion;
import com.example.ecommerce.infrastructure.persistence.jpa.entity.CodigoRecuperacionEntity;
import org.springframework.stereotype.Component;

@Component
public class CodigoRecuperacionPersistenceMapper {

    public CodigoRecuperacionEntity toEntity(CodigoRecuperacion codigo) {
        CodigoRecuperacionEntity entity = new CodigoRecuperacionEntity();
        entity.setUsuarioId(codigo.getUsuarioId());
        entity.setCodigoHash(codigo.getCodigoHash());
        entity.setExpiraEn(codigo.getExpiraEn());
        entity.setIntentosFallidos(codigo.getIntentosFallidos());
        entity.setUsado(codigo.isUsado());
        return entity;
    }

    public CodigoRecuperacion toDomain(CodigoRecuperacionEntity entity) {
        return CodigoRecuperacion.reconstruir(
                entity.getUsuarioId(),
                entity.getCodigoHash(),
                entity.getExpiraEn(),
                entity.getIntentosFallidos(),
                entity.isUsado());
    }
}
