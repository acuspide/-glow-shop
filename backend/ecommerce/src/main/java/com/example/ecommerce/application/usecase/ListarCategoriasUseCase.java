package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Categoria;
import com.example.ecommerce.domain.repository.CategoriaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Lista las categorias para que el vendedor elija una al publicar un artículo. */
@Service
@RequiredArgsConstructor
public class ListarCategoriasUseCase {

    private final CategoriaRepository repository;

    @Transactional(readOnly = true)
    public List<Categoria> ejecutar() {
        return repository.listarTodas();
    }
}
