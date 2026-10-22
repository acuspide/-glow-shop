package com.example.ecommerce.infrastructure.persistence;
import org.springframework.stereotype.Repository;

import com.example.ecommerce.domain.entity.RutinaCuidado;
import com.example.ecommerce.domain.repository.RutinaCuidadoRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class RutinaCuidadoRepositoryEnMemoria implements RutinaCuidadoRepository {
    private final Map<String, RutinaCuidado> rutinas = new HashMap<>();

    @Override
    public Optional<RutinaCuidado> obtenerPorId(String id) {
        return Optional.ofNullable(rutinas.get(id));
    }

    @Override
    public void guardar(RutinaCuidado rutina) {
        rutinas.put(rutina.getId(), rutina);
    }

    @Override
    public List<RutinaCuidado> listarTodas() {
        return List.copyOf(rutinas.values());
    }
}
