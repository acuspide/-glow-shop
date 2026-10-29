package com.example.ecommerce.infrastructure.persistence.jpa.entity;

import com.example.ecommerce.domain.valueobject.TipoPiel;
import com.example.ecommerce.domain.valueobject.Tono;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "articulos")
@Getter @Setter
@NoArgsConstructor
public class ArticuloEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    // Relaciones reales con llave foránea: el artículo pertenece a una categoría, una marca y una tienda.
    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaEntity categoria;

    @ManyToOne(optional = false)
    @JoinColumn(name = "marca_id", nullable = false)
    private MarcaEntity marca;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tienda_id", nullable = false)
    private TiendaEntity tienda;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Tono tono;

    // Un artículo sirve para uno o varios tipos de piel: tabla articulo_tipos_piel.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "articulo_tipos_piel", joinColumns = @JoinColumn(name = "articulo_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_piel", length = 20)
    private List<TipoPiel> tiposPiel = new ArrayList<>();

    @Column(name = "cantidad_disponible", nullable = false)
    private int cantidadDisponible;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    // Baja lógica: un artículo eliminado no se borra de la tabla.
    @Column(nullable = false)
    private boolean eliminado;

    @Column(nullable = false)
    private boolean publicado;
}
