package com.example.ecommerce.infrastructure.persistence;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Generador de ids temporal mientras el repositorio sea en memoria.
 * Con JPA/MariaDB lo genera la base de datos y esta clase desaparece.
 */
@Component
public class SecuenciaIdUsuario {

    private final AtomicLong actual = new AtomicLong(0);

    public long siguiente() {
        return actual.incrementAndGet();
    }
}
