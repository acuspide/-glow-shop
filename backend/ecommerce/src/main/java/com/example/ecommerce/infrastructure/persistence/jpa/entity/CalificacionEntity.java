package com.example.ecommerce.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "calificaciones")
@Getter @Setter
@NoArgsConstructor
public class CalificacionEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "articulo_id", nullable = false, length = 36)
    private String articuloId;

    @Column(name = "cliente_id", nullable = false, length = 36)
    private String clienteId;

    @Column(nullable = false)
    private int puntuacion;

    @Column(length = 500)
    private String comentario;

    @Column(nullable = false)
    private LocalDateTime fecha;

    // Baja lógica: la calificación eliminada no se borra de la tabla.
    @Column(nullable = false)
    private boolean eliminada;
}
