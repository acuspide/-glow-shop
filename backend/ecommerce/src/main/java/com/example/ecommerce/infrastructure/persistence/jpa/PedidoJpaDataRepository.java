package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.infrastructure.persistence.jpa.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface PedidoJpaDataRepository extends JpaRepository<PedidoEntity, String> {

    List<PedidoEntity> findByClienteId(String clienteId);

    List<PedidoEntity> findByTiendaId(String tiendaId);
}
