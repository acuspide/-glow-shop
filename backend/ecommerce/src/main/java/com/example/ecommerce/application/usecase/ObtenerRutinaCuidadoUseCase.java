package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerce.domain.entity.RutinaCuidado;
import com.example.ecommerce.domain.repository.RutinaCuidadoRepository;

@Service
@RequiredArgsConstructor
public class ObtenerRutinaCuidadoUseCase {

    private final RutinaCuidadoRepository repository;

    @Transactional(readOnly = true)
    public RutinaCuidado ejecutar(String id) {
        return repository.obtenerPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("la rutina", id));
    }
}
