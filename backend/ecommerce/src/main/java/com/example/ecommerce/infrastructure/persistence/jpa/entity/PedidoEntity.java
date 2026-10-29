package com.example.ecommerce.infrastructure.persistence.jpa.entity;

import com.example.ecommerce.domain.valueobject.EstadoPedido;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Getter @Setter
@NoArgsConstructor
public class PedidoEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "cliente_id", nullable = false, length = 36)
    private String clienteId;

    @Column(name = "tienda_id", nullable = false, length = 36)
    private String tiendaId;

    // Los detalles guardan el precio congelado del momento de la compra (tabla pedido_detalles).
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pedido_detalles", joinColumns = @JoinColumn(name = "pedido_id"))
    @OrderColumn(name = "posicion")
    private List<LineaArticuloEmbeddable> detalles = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado;

    @Column(name = "cupon_id", length = 36)
    private String cuponId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal descuento;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal impuestos;

    @Column(name = "costo_envio", nullable = false, precision = 12, scale = 2)
    private BigDecimal costoEnvio;

    @Column(name = "pago_confirmado_id", length = 36)
    private String pagoConfirmadoId;
}
