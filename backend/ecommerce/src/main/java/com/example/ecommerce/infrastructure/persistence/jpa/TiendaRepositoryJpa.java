package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.domain.repository.TiendaRepository;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.TiendaPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TiendaRepositoryJpa implements TiendaRepository {

    private final TiendaJpaDataRepository dataRepository;
    private final TiendaPersistenceMapper mapper;

    @Override
    public void guardar(Tienda tienda) {
        dataRepository.save(mapper.toEntity(tienda));
    }

    @Override
    public Optional<Tienda> obtenerPorId(String id) {
        return dataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existePorVendedorId(String vendedorId) {
        return dataRepository.existsByVendedorId(vendedorId);
    }

    @Override
    public Optional<Tienda> obtenerPorVendedorId(String vendedorId) {
        return dataRepository.findByVendedorId(vendedorId).map(mapper::toDomain);
    }
}
