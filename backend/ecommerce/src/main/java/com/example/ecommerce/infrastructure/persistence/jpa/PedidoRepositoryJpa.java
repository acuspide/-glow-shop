package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.repository.PedidoRepository;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.PedidoPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PedidoRepositoryJpa implements PedidoRepository {

    private final PedidoJpaDataRepository dataRepository;
    private final PedidoPersistenceMapper mapper;

    @Override
    public Optional<Pedido> obtenerPorId(String id) {
        return dataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void guardar(Pedido pedido) {
        dataRepository.save(mapper.toEntity(pedido));
    }

    @Override
    public List<Pedido> listarTodos() {
        return dataRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Pedido> obtenerPorClienteId(String clienteId) {
        return dataRepository.findByClienteId(clienteId).stream().map(mapper::toDomain).toList();
    }
}
