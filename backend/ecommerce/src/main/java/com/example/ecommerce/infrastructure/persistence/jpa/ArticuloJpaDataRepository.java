package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.infrastructure.persistence.jpa.entity.ArticuloEntity;
import org.springframework.data.jpa.repository.JpaRepository;

interface ArticuloJpaDataRepository extends JpaRepository<ArticuloEntity, String> {
}
