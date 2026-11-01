package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.domain.valueobject.TipoPiel;
import com.example.ecommerce.domain.valueobject.Tono;
import com.example.ecommerce.infrastructure.persistence.jpa.entity.ArticuloEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

interface ArticuloJpaDataRepository extends JpaRepository<ArticuloEntity, String> {

    /**
     * Artículos disponibles para la venta que cumplen los filtros opcionales.
     * Disponible = publicado, no eliminado (baja lógica), no vencido y con stock.
     * Los filtros en null se ignoran. El texto llega ya recortado ("" si no se busca nada).
     */
    @Query("""
            SELECT a FROM ArticuloEntity a
            WHERE a.eliminado = false
              AND a.publicado = true
              AND a.cantidadDisponible > 0
              AND a.fechaVencimiento >= CURRENT_DATE
              AND LOWER(a.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
              AND (:categoriaId IS NULL OR a.categoria.id = :categoriaId)
              AND (:marcaId IS NULL OR a.marca.id = :marcaId)
              AND (:tiendaId IS NULL OR a.tienda.id = :tiendaId)
              AND (:precioMinimo IS NULL OR a.precio >= :precioMinimo)
              AND (:precioMaximo IS NULL OR a.precio <= :precioMaximo)
              AND (:tono IS NULL OR a.tono = :tono)
              AND (:tipoPiel IS NULL OR :tipoPiel MEMBER OF a.tiposPiel)
            """)
    Page<ArticuloEntity> buscar(@Param("texto") String texto,
                                @Param("categoriaId") String categoriaId,
                                @Param("marcaId") String marcaId,
                                @Param("tiendaId") String tiendaId,
                                @Param("precioMinimo") BigDecimal precioMinimo,
                                @Param("precioMaximo") BigDecimal precioMaximo,
                                @Param("tono") Tono tono,
                                @Param("tipoPiel") TipoPiel tipoPiel,
                                Pageable pageable);
}
