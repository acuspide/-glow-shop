package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertarStockBajoUseCase {
    private static final int LIMITE_STOCK_BAJO = 5;

    private final ArticuloRepository repository;

    public AlertarStockBajoUseCase(ArticuloRepository repository) {
        this.repository = repository;
    }

    public List<Articulo> ejecutar() {
        return repository.listarTodos()
                .stream()
                .filter(articulo ->
                        articulo.getCantidadDisponible() < LIMITE_STOCK_BAJO)
                .toList();
    }
}
