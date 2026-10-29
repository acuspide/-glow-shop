package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.domain.entity.Pago;
import com.example.ecommerce.domain.repository.PagoRepository;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.PagoPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PagoRepositoryJpa implements PagoRepository {

    private final PagoJpaDataRepository dataRepository;
    private final PagoPersistenceMapper mapper;

    @Override
    public Optional<Pago> obtenerPorId(String id) {
        return dataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void guardar(Pago pago) {
        dataRepository.save(mapper.toEntity(pago));
    }

    // Los intentos de pago de un pedido, del más antiguo al más reciente (reintentos).
    @Override
    public List<Pago> obtenerPorPedidoId(String pedidoId) {
        return dataRepository.findByPedidoIdOrderByFechaPagoAsc(pedidoId).stream().map(mapper::toDomain).toList();
    }
}
