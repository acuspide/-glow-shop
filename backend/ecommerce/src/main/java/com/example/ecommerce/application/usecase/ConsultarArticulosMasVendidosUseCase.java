package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.repository.ArticuloVentas;
import com.example.ecommerce.domain.repository.PedidoRepository;
import com.example.ecommerce.domain.valueobject.LimiteRanking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultarArticulosMasVendidosUseCase {

    private final PedidoRepository pedidoRepository;

    // El JOIN, el GROUP BY y el SUM los hace la base de datos (ver PedidoJpaDataRepository).
    @Transactional(readOnly = true)
    public List<ArticuloVentas> ejecutar(int limite) {
        // El rango del límite lo valida el Value Object LimiteRanking.
        LimiteRanking limiteRanking = new LimiteRanking(limite);
        return pedidoRepository.masVendidos(limiteRanking.valor());
    }
}
