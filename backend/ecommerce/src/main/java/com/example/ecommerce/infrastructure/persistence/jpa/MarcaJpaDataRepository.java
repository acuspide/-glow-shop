package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.infrastructure.persistence.jpa.entity.MarcaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

interface MarcaJpaDataRepository extends JpaRepository<MarcaEntity, String> {

    boolean existsByNombreIgnoreCase(String nombre);
}
