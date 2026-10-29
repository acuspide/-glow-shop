package com.example.ecommerce.infrastructure.persistence;

import com.example.ecommerce.domain.entity.CodigoRecuperacion;
import com.example.ecommerce.domain.repository.CodigoRecuperacionRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

// Se usa en las pruebas unitarias. En la aplicación corre el adaptador JPA (persistence/jpa).
public class CodigoRecuperacionRepositoryEnMemoria implements CodigoRecuperacionRepository {

    private final Map<String, CodigoRecuperacion> codigosPorUsuario = new ConcurrentHashMap<>();

    @Override
    public void guardar(CodigoRecuperacion codigo) {
        codigosPorUsuario.put(codigo.getUsuarioId(), codigo);
    }

    @Override
    public Optional<CodigoRecuperacion> obtenerPorUsuarioId(String usuarioId) {
        return Optional.ofNullable(codigosPorUsuario.get(usuarioId));
    }
}
