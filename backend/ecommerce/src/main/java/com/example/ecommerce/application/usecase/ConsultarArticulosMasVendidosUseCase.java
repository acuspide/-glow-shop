package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.exception.ReglaDominioException;
import com.example.ecommerce.domain.repository.ArticuloVentas;
import com.example.ecommerce.domain.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultarArticulosMasVendidosUseCase {

    private static final int LIMITE_MAXIMO = 20;

    private final PedidoRepository pedidoRepository;

    // El JOIN, el GROUP BY y el SUM los hace la base de datos (ver PedidoJpaDataRepository).
    @Transactional(readOnly = true)
    public List<ArticuloVentas> ejecutar(int limite) {
        if (limite < 1 || limite > LIMITE_MAXIMO) {
            throw new ReglaDominioException("El límite debe estar entre 1 y " + LIMITE_MAXIMO);
        }
        return pedidoRepository.masVendidos(limite);
    }
}
