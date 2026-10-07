package com.example.ecommerce.infrastructure.rest.mapper;

import com.example.ecommerce.application.dto.response.CarritoResponse;
import com.example.ecommerce.application.dto.response.ItemCarritoResponse;
import com.example.ecommerce.domain.entity.Carrito;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CarritoMapper {

    public CarritoResponse toResponse(Carrito carrito) {

        List<ItemCarritoResponse> items = carrito.getItems()
                .stream()
                .map(item -> new ItemCarritoResponse(
                        item.getArticuloId(),
                        item.getCantidad(),
                        item.getPrecioUnitario().valor(),
                        item.subtotal()
                ))
                .toList();

        return new CarritoResponse(
                carrito.getId(),
                carrito.getClienteId(),
                items,
                carrito.calcularTotal()
        );
    }
}