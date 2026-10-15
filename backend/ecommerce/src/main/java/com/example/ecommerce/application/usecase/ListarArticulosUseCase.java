package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.exception.ReglaDominioException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarArticulosUseCase {
    private final ArticuloRepository repository;

    public ListarArticulosUseCase(ArticuloRepository repository) {
        this.repository = repository;
    }

    public List<Articulo> ejecutar() {
        return repository.listarTodos()
                .stream()
                .filter(this::esDisponibleParaConsulta)
                .toList();
    }

    private boolean esDisponibleParaConsulta(Articulo articulo) {
        try {
            articulo.validarDisponibleParaConsulta();
            return true;
        } catch (ReglaDominioException e) {
            return false;
        }
    }
}
