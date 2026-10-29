package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.domain.entity.Calificacion;
import com.example.ecommerce.domain.repository.CalificacionRepository;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.CalificacionPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CalificacionRepositoryJpa implements CalificacionRepository {

    private final CalificacionJpaDataRepository dataRepository;
    private final CalificacionPersistenceMapper mapper;

    @Override
    public void guardar(Calificacion calificacion) {
        dataRepository.save(mapper.toEntity(calificacion));
    }

    @Override
    public Optional<Calificacion> obtenerPorId(String id) {
        return dataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existePorClienteYArticulo(String clienteId, String articuloId) {
        return dataRepository.existsByClienteIdAndArticuloIdAndEliminadaFalse(clienteId, articuloId);
    }

    // Devuelve también las eliminadas: ResumenCalificaciones del dominio decide cuáles mostrar.
    @Override
    public List<Calificacion> listarPorArticulo(String articuloId) {
        return dataRepository.findByArticuloId(articuloId).stream().map(mapper::toDomain).toList();
    }
}
