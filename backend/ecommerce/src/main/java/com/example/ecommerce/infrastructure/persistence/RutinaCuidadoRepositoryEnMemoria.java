package com.example.ecommerce.infrastructure.persistence;
import org.springframework.stereotype.Repository;

import com.example.ecommerce.domain.entity.RutinaCuidado;
import com.example.ecommerce.domain.repository.RutinaCuidadoRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class RutinaCuidadoRepositoryEnMemoria implements RutinaCuidadoRepository {
    private final Map<Long, RutinaCuidado> rutinas = new HashMap<>();

    @Override
    public Optional<RutinaCuidado> obtenerPorId(long id) {
        return Optional.ofNullable(rutinas.get(id));
    }

    @Override
    public void guardar(RutinaCuidado rutina) {
        rutinas.put(rutina.getId(), rutina);
    }
}
