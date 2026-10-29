package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.infrastructure.persistence.jpa.entity.RutinaCuidadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

interface RutinaCuidadoJpaDataRepository extends JpaRepository<RutinaCuidadoEntity, String> {
}
