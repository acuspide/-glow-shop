package com.example.ecommerce.infrastructure.persistence;

import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.domain.valueobject.EstadoTienda;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TiendaRepositoryEnMemoriaTest {

    private final TiendaRepositoryEnMemoria repositorio = new TiendaRepositoryEnMemoria();

    @Test
    void guardaYEncuentraPorIdYPorVendedor() {
        Tienda tienda = Tienda.reconstruir("t-1", "Glow", "", "v-1", EstadoTienda.PENDIENTE);
        repositorio.guardar(tienda);

        assertSame(tienda, repositorio.obtenerPorId("t-1").orElseThrow());
        assertTrue(repositorio.existePorVendedorId("v-1"));
        assertFalse(repositorio.existePorVendedorId("v-2"));
        assertTrue(repositorio.obtenerPorId("t-9").isEmpty());
    }
}
