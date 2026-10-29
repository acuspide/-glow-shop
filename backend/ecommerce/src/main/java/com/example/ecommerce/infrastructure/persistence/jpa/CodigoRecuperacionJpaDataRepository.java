package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.infrastructure.persistence.jpa.entity.CodigoRecuperacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

interface CodigoRecuperacionJpaDataRepository extends JpaRepository<CodigoRecuperacionEntity, String> {
}
