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
 * Consultar inventario: el vendedor ve el inventario de los artículos de SU tienda.
 */
@Service
@RequiredArgsConstructor
public class ConsultarInventarioCompletoUseCase {
    private final ArticuloRepository repository;
    private final TiendaRepository tiendaRepository;

    @Transactional(readOnly = true)
    public List<Articulo> ejecutar(String vendedorId) {
        Tienda tienda = tiendaRepository.obtenerPorVendedorId(vendedorId)
                .orElseThrow(() -> new RecursoNoEncontradoException("la tienda del vendedor", vendedorId));

        return repository.listarTodos()
                .stream()
                .filter(articulo -> articulo.perteneceATienda(tienda.getId()))
                .toList();
    }
}
