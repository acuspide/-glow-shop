package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Marca;
import com.example.ecommerce.domain.repository.MarcaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Lista las marcas para que el vendedor elija una al publicar un artículo. */
@Service
@RequiredArgsConstructor
public class ListarMarcasUseCase {

    private final MarcaRepository repository;

    @Transactional(readOnly = true)
    public List<Marca> ejecutar() {
        return repository.listarTodas();
    }
}
