package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.domain.entity.CodigoRecuperacion;
import com.example.ecommerce.domain.repository.CodigoRecuperacionRepository;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.CodigoRecuperacionPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CodigoRecuperacionRepositoryJpa implements CodigoRecuperacionRepository {

    private final CodigoRecuperacionJpaDataRepository dataRepository;
    private final CodigoRecuperacionPersistenceMapper mapper;

    @Override
    public void guardar(CodigoRecuperacion codigo) {
        dataRepository.save(mapper.toEntity(codigo)); // reemplaza el código anterior del mismo usuario
    }

    @Override
    public Optional<CodigoRecuperacion> obtenerPorUsuarioId(String usuarioId) {
        return dataRepository.findById(usuarioId).map(mapper::toDomain);
    }
}
