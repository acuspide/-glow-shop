package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.infrastructure.persistence.jpa.entity.CategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

interface CategoriaJpaDataRepository extends JpaRepository<CategoriaEntity, String> {

    boolean existsByNombreIgnoreCase(String nombre);
}
