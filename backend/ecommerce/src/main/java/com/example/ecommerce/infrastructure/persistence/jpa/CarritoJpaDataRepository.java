package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.infrastructure.persistence.jpa.entity.CarritoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface CarritoJpaDataRepository extends JpaRepository<CarritoEntity, String> {

    Optional<CarritoEntity> findByClienteId(String clienteId);
}
