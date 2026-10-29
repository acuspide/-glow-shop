package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.infrastructure.persistence.jpa.entity.CalificacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface CalificacionJpaDataRepository extends JpaRepository<CalificacionEntity, String> {

    // Si la eliminó, puede volver a calificar: solo cuentan las no eliminadas.
    boolean existsByClienteIdAndArticuloIdAndEliminadaFalse(String clienteId, String articuloId);

    List<CalificacionEntity> findByArticuloId(String articuloId);
}
