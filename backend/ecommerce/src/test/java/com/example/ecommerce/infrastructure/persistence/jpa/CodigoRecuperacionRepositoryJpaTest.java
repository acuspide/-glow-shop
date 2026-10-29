package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.domain.entity.CodigoRecuperacion;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.CodigoRecuperacionPersistenceMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import({CodigoRecuperacionRepositoryJpa.class, CodigoRecuperacionPersistenceMapper.class})
class CodigoRecuperacionRepositoryJpaTest {

    @Autowired
    private CodigoRecuperacionRepositoryJpa repositorio;

    @Test
    void guardaYReconstruyeElCodigo() {
        LocalDateTime expira = LocalDateTime.of(2026, 10, 28, 20, 0);
        repositorio.guardar(CodigoRecuperacion.reconstruir("u-1", "hash-1", expira, 2, false));

        CodigoRecuperacion leido = repositorio.obtenerPorUsuarioId("u-1").orElseThrow();

        assertEquals("hash-1", leido.getCodigoHash());
        assertEquals(expira, leido.getExpiraEn());
        assertEquals(2, leido.getIntentosFallidos());
    }

    @Test
    void unCodigoNuevoReemplazaAlAnteriorDelMismoUsuario() {
        LocalDateTime expira = LocalDateTime.of(2026, 10, 28, 20, 0);
        repositorio.guardar(CodigoRecuperacion.reconstruir("u-1", "hash-viejo", expira, 0, false));
        repositorio.guardar(CodigoRecuperacion.reconstruir("u-1", "hash-nuevo", expira, 0, false));

        assertEquals("hash-nuevo", repositorio.obtenerPorUsuarioId("u-1").orElseThrow().getCodigoHash());
        assertTrue(repositorio.obtenerPorUsuarioId("u-2").isEmpty());
    }
}
