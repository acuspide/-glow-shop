package com.example.ecommerce.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rutinas_cuidado")
@Getter @Setter
@NoArgsConstructor
public class RutinaCuidadoEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 100)
    private String nombre;

    // Una rutina agrupa artículos de una o varias marcas, en un orden (tabla rutina_articulos).
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "rutina_articulos",
            joinColumns = @JoinColumn(name = "rutina_id"),
            inverseJoinColumns = @JoinColumn(name = "articulo_id"))
    @OrderColumn(name = "orden")
    private List<ArticuloEntity> articulos = new ArrayList<>();
}
