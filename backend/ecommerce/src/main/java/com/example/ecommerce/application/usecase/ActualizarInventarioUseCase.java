package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActualizarInventarioUseCase {

    private final ArticuloRepository repository;

    @Transactional
    public Articulo aumentar(String id, int cantidad) {

        Articulo articulo = repository.obtenerPorId(id)
                .orElseThrow(() -> new ArticuloNoEncontradoException(id));

        articulo.aumentarInventario(cantidad);

        repository.guardar(articulo);

        return articulo;
    }

    @Transactional
    public Articulo disminuir(String id, int cantidad) {

        Articulo articulo = repository.obtenerPorId(id)
                .orElseThrow(() -> new ArticuloNoEncontradoException(id));

        articulo.disminuirInventario(cantidad);

        repository.guardar(articulo);

        return articulo;
    }
}
