package com.example.ecommerce.domain.service;

import com.example.ecommerce.domain.exception.VendedorYaTieneTiendaException;
import com.example.ecommerce.domain.repository.TiendaRepository;

/**
 * Servicio de dominio para RN08: cada vendedor administra una sola tienda.
 * La regla necesita mirar todas las tiendas (el repositorio), por eso no cabe en la entidad Tienda.
 */
public class VerificadorDeTiendaUnicaPorVendedor {

    private final TiendaRepository tiendaRepository;

    public VerificadorDeTiendaUnicaPorVendedor(TiendaRepository tiendaRepository) {
        this.tiendaRepository = tiendaRepository;
    }

    public void verificarQueNoTengaTienda(String vendedorId) {
        if (tiendaRepository.existePorVendedorId(vendedorId)) {
            throw new VendedorYaTieneTiendaException();
        }
    }
}
