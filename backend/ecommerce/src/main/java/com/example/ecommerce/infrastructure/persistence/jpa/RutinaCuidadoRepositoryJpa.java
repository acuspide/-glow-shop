package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.domain.entity.RutinaCuidado;
import com.example.ecommerce.domain.repository.RutinaCuidadoRepository;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.RutinaCuidadoPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RutinaCuidadoRepositoryJpa implements RutinaCuidadoRepository {

    private final RutinaCuidadoJpaDataRepository dataRepository;
    private final ArticuloJpaDataRepository articuloDataRepository;
    private final RutinaCuidadoPersistenceMapper mapper;

    @Override
    public Optional<RutinaCuidado> obtenerPorId(String id) {
        return dataRepository.findById(id).map(mapper::toDomain);
    }

    // Los artículos ya están guardados: la rutina solo los enlaza por id.
    @Override
    public void guardar(RutinaCuidado rutina) {
        dataRepository.save(mapper.toEntity(rutina, rutina.getArticulos().stream()
                .map(articulo -> articuloDataRepository.getReferenceById(articulo.getId()))
                .toList()));
    }

    @Override
    public List<RutinaCuidado> listarTodas() {
        return dataRepository.findAll().stream().map(mapper::toDomain).toList();
    }
}
