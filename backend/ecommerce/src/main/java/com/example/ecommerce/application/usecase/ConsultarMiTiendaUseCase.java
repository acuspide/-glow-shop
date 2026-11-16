package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.example.ecommerce.domain.repository.TiendaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * El vendedor con sesión iniciada consulta su tienda: su id (para publicar artículos)
 * y su estado (PENDIENTE hasta que el administrador la apruebe).
 */
@Service
@RequiredArgsConstructor
public class ConsultarMiTiendaUseCase {

    private final TiendaRepository tiendaRepository;

    @Transactional(readOnly = true)
    public Tienda ejecutar(String vendedorId) {
        return tiendaRepository.obtenerPorVendedorId(vendedorId)
                .orElseThrow(() -> new RecursoNoEncontradoException("una tienda del vendedor", vendedorId));
    }
}
