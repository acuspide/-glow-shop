package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import org.springframework.stereotype.Service;

@Service
public class MostrarDetalleArticuloUseCase {
    private final ArticuloRepository repository;

    public MostrarDetalleArticuloUseCase(ArticuloRepository repository) {
        this.repository = repository;
    }

    public Articulo ejecutar(long id) {
        Articulo articulo = repository.obtenerPorId(id)
                .orElseThrow(() -> new ArticuloNoEncontradoException(id));

        articulo.validarDisponibleParaConsulta();

        return articulo;
    }
}
