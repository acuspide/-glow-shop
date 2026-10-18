package com.example.ecommerce.application.usecase;
import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultarInventarioCompletoUseCase {
    private final ArticuloRepository repository;

    public ConsultarInventarioCompletoUseCase(
            ArticuloRepository repository) {
        this.repository = repository;
    }

    public List<Articulo> ejecutar() {
        return repository.listarTodos();
    }
}
