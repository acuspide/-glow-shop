package com.example.ecommerce.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Un código de recuperación vigente por usuario: el id de la fila es el id del usuario. */
@Entity
@Table(name = "codigos_recuperacion")
@Getter @Setter
@NoArgsConstructor
public class CodigoRecuperacionEntity {

    @Id
    @Column(name = "usuario_id", length = 36)
    private String usuarioId;

    @Column(name = "codigo_hash", nullable = false, length = 200)
    private String codigoHash;

    @Column(name = "expira_en", nullable = false)
    private LocalDateTime expiraEn;

    @Column(name = "intentos_fallidos", nullable = false)
    private int intentosFallidos;

    @Column(nullable = false)
    private boolean usado;
}
