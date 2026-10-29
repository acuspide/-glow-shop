package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.infrastructure.persistence.jpa.entity.TiendaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

interface TiendaJpaDataRepository extends JpaRepository<TiendaEntity, String> {

    boolean existsByVendedorId(String vendedorId);
}
