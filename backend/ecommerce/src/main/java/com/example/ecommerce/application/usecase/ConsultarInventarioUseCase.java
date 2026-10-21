package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import org.springframework.stereotype.Service;

@Service
public class ConsultarInventarioUseCase {
    private final ArticuloRepository repository;

    public ConsultarInventarioUseCase(ArticuloRepository repository) {
        this.repository = repository;
    }

    public Articulo ejecutar(long id) {
        return repository.obtenerPorId(id)
                .orElseThrow(() -> new ArticuloNoEncontradoException(id));
    }
}
