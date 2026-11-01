package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.domain.repository.ArticuloVentas;
import com.example.ecommerce.domain.valueobject.EstadoPedido;
import com.example.ecommerce.infrastructure.persistence.jpa.entity.PedidoEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

interface PedidoJpaDataRepository extends JpaRepository<PedidoEntity, String> {

    List<PedidoEntity> findByClienteId(String clienteId);

    List<PedidoEntity> findByTiendaId(String tiendaId);

    /**
     * Ranking de artículos más vendidos: une los pedidos con sus detalles y con el artículo,
     * agrupa por artículo y suma las unidades. Solo cuentan los pedidos en los estados dados
     * y los artículos que no están eliminados.
     */
    @Query("""
            SELECT new com.example.ecommerce.domain.repository.ArticuloVentas(a.id, a.nombre, SUM(d.cantidad))
            FROM PedidoEntity p
            JOIN p.detalles d
            JOIN ArticuloEntity a ON a.id = d.articuloId
            WHERE p.estado IN :estados
              AND a.eliminado = false
            GROUP BY a.id, a.nombre
            ORDER BY SUM(d.cantidad) DESC, a.nombre ASC
            """)
    List<ArticuloVentas> masVendidos(@Param("estados") Collection<EstadoPedido> estados, Pageable pageable);
}
