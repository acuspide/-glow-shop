package com.example.ecommerce.infrastructure.persistence.jpa.mapper;

import com.example.ecommerce.domain.entity.Carrito;
import com.example.ecommerce.domain.valueobject.ItemCarrito;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.infrastructure.persistence.jpa.entity.CarritoEntity;
import com.example.ecommerce.infrastructure.persistence.jpa.entity.LineaArticuloEmbeddable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CarritoPersistenceMapper {

    public CarritoEntity toEntity(Carrito carrito) {
        CarritoEntity entity = new CarritoEntity();
        entity.setId(carrito.getId());
        entity.setClienteId(carrito.getClienteId());
        entity.setItems(new ArrayList<>(carrito.getItems().stream()
                .map(item -> new LineaArticuloEmbeddable(
                        item.getArticuloId(), item.getCantidad(), item.getPrecioUnitario().valor()))
                .toList()));
        return entity;
    }

    public Carrito toDomain(CarritoEntity entity) {
        return Carrito.reconstruir(
                entity.getId(),
                entity.getClienteId(),
                entity.getItems().stream()
                        .map(linea -> new ItemCarrito(
                                linea.getArticuloId(), linea.getCantidad(), new Precio(linea.getPrecioUnitario())))
                        .toList());
    }
}
