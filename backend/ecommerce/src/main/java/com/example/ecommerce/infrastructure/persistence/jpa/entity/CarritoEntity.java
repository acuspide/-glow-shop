package com.example.ecommerce.infrastructure.persistence.jpa.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carritos")
@Getter @Setter
@NoArgsConstructor
public class CarritoEntity {

    @Id
    @Column(length = 36)
    private String id;

    // Cada cliente tiene un solo carrito.
    @Column(name = "cliente_id", nullable = false, unique = true, length = 36)
    private String clienteId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "carrito_items", joinColumns = @JoinColumn(name = "carrito_id"))
    @OrderColumn(name = "posicion")
    private List<LineaArticuloEmbeddable> items = new ArrayList<>();
}
