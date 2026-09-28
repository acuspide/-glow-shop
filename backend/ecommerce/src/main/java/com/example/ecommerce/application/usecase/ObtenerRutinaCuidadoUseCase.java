package com.example.ecommerce.application.usecase;
import org.springframework.stereotype.Service;

import com.example.ecommerce.domain.entity.RutinaCuidado;
import com.example.ecommerce.domain.repository.RutinaCuidadoRepository;

@Service
public class ObtenerRutinaCuidadoUseCase {

    private final RutinaCuidadoRepository repository;

    public ObtenerRutinaCuidadoUseCase(RutinaCuidadoRepository repository) {
        this.repository = repository;
    }

    public RutinaCuidado ejecutar(long id) {
        return repository.obtenerPorId(id)
                .orElseThrow();
    }
}
