package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertarStockBajoUseCase {
    private static final int LIMITE_STOCK_BAJO = 5;

    private final ArticuloRepository repository;

    @Transactional(readOnly = true)
    public List<Articulo> ejecutar() {
        return repository.listarTodos()
                .stream()
                .filter(articulo ->
                        articulo.getCantidadDisponible() < LIMITE_STOCK_BAJO)
                .toList();
    }
}
