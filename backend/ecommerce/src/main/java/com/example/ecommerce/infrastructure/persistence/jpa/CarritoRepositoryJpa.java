package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.domain.entity.Carrito;
import com.example.ecommerce.domain.repository.CarritoRepository;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.CarritoPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CarritoRepositoryJpa implements CarritoRepository {

    private final CarritoJpaDataRepository dataRepository;
    private final CarritoPersistenceMapper mapper;

    @Override
    public Optional<Carrito> obtenerPorId(String id) {
        return dataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Carrito> obtenerPorClienteId(String clienteId) {
        return dataRepository.findByClienteId(clienteId).map(mapper::toDomain);
    }

    // Guardar reemplaza los ítems: si el carrito se vació o se quitó un artículo, la tabla queda igual.
    @Override
    public void guardar(Carrito carrito) {
        dataRepository.save(mapper.toEntity(carrito));
    }
}
