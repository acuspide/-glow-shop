package com.example.ecommerce.domain.service;

import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.domain.exception.VendedorYaTieneTiendaException;
import com.example.ecommerce.domain.valueobject.EstadoTienda;
import com.example.ecommerce.infrastructure.persistence.TiendaRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VerificadorDeTiendaUnicaPorVendedorTest {

    private final TiendaRepositoryEnMemoria tiendas = new TiendaRepositoryEnMemoria();
    private final VerificadorDeTiendaUnicaPorVendedor verificador = new VerificadorDeTiendaUnicaPorVendedor(tiendas);

    @Test
    void unVendedorSinTiendaPasaLaVerificacion() {
        assertDoesNotThrow(() -> verificador.verificarQueNoTengaTienda("v-1"));
    }

    @Test
    void unVendedorConTiendaNoPasaLaVerificacion() {
        tiendas.guardar(Tienda.reconstruir("t-1", "Glow", "", "v-1", EstadoTienda.PENDIENTE));

        assertThrows(VendedorYaTieneTiendaException.class, () -> verificador.verificarQueNoTengaTienda("v-1"));
        assertDoesNotThrow(() -> verificador.verificarQueNoTengaTienda("v-2"));
    }
}
