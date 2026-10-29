package com.example.ecommerce.infrastructure.persistence.jpa.entity;

import com.example.ecommerce.domain.valueobject.EstadoTienda;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tiendas")
@Getter @Setter
@NoArgsConstructor
public class TiendaEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    // RN08: un vendedor administra una sola tienda (también lo exige la base de datos).
    @Column(name = "vendedor_id", nullable = false, unique = true, length = 36)
    private String vendedorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoTienda estado;
}
