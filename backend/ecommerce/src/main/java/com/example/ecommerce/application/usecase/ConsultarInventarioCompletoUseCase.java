package com.example.ecommerce.application.usecase;
import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultarInventarioCompletoUseCase {
    private final ArticuloRepository repository;

    @Transactional(readOnly = true)
    public List<Articulo> ejecutar() {
        return repository.listarTodos();
    }
}
