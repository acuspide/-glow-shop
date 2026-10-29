package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.TiendaRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Alertar stock bajo: el vendedor ve qué artículos de SU tienda tienen menos de 5 unidades.
 */
@Service
@RequiredArgsConstructor
public class AlertarStockBajoUseCase {
    private static final int LIMITE_STOCK_BAJO = 5;

    private final ArticuloRepository repository;
    private final TiendaRepository tiendaRepository;

    @Transactional(readOnly = true)
    public List<Articulo> ejecutar(String vendedorId) {
        Tienda tienda = tiendaRepository.obtenerPorVendedorId(vendedorId)
                .orElseThrow(() -> new RecursoNoEncontradoException("la tienda del vendedor", vendedorId));

        return repository.listarTodos()
                .stream()
                .filter(articulo -> articulo.perteneceATienda(tienda.getId()))
                .filter(articulo ->
                        articulo.getCantidadDisponible() < LIMITE_STOCK_BAJO)
                .toList();
    }
}
