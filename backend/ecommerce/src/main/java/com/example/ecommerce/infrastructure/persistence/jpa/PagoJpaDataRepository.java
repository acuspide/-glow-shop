package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.infrastructure.persistence.jpa.entity.PagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface PagoJpaDataRepository extends JpaRepository<PagoEntity, String> {

    List<PagoEntity> findByPedidoIdOrderByFechaPagoAsc(String pedidoId);
}
