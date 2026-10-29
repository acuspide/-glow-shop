package com.example.ecommerce.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Una línea "artículo, cantidad y precio unitario". La usan los ítems del carrito y los detalles del pedido.
 * El precio se guarda congelado: es el del momento en que se agregó o se compró.
 */
@Embeddable
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class LineaArticuloEmbeddable {

    @Column(name = "articulo_id", nullable = false, length = 36)
    private String articuloId;

    @Column(nullable = false)
    private int cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;
}
