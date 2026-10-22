package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.exception.ReglaDominioException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.valueobject.CriteriosBusquedaArticulo;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuscarArticulosUseCase {
    private final ArticuloRepository repository;

    @Transactional(readOnly = true)
    public List<Articulo> ejecutar(CriteriosBusquedaArticulo criterios) {
        return repository.listarTodos()
                .stream()
                .filter(this::estaDisponibleParaConsulta)
                .filter(criterios::admite)
                .toList();
    }

    private boolean estaDisponibleParaConsulta(Articulo articulo) {
        try {
            articulo.validarDisponibleParaConsulta();
            return true;
        } catch (ReglaDominioException e) {
            return false;
        }
    }

}
