package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
import com.example.ecommerce.domain.repository.ArticuloRepository;

public class ActualizarInventarioUseCase {

    private final ArticuloRepository repository;

    public ActualizarInventarioUseCase(ArticuloRepository repository) {
        this.repository = repository;
    }
    public Articulo aumentar(long id, int cantidad) {

        Articulo articulo = repository.obtenerPorId(id)
                .orElseThrow(() -> new ArticuloNoEncontradoException(id));

        articulo.aumentarInventario(cantidad);

        repository.guardar(articulo);

        return articulo;
    }

    public Articulo disminuir(long id, int cantidad) {

        Articulo articulo = repository.obtenerPorId(id)
                .orElseThrow(() -> new ArticuloNoEncontradoException(id));

        articulo.disminuirInventario(cantidad);

        repository.guardar(articulo);

        return articulo;
    }
}
