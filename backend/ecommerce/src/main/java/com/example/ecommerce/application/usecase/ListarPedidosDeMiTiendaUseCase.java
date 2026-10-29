package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.example.ecommerce.domain.repository.PedidoRepository;
import com.example.ecommerce.domain.repository.TiendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Listar pedidos de mi tienda (CU-72): el vendedor ve los pedidos que recibió su tienda
 * para saber qué preparar, enviar y entregar.
 */
@Service
@RequiredArgsConstructor
public class ListarPedidosDeMiTiendaUseCase {

    private final TiendaRepository tiendaRepository;
    private final PedidoRepository pedidoRepository;

    @Transactional(readOnly = true)
    public List<Pedido> ejecutar(String vendedorId) {
        Tienda tienda = tiendaRepository.obtenerPorVendedorId(vendedorId)
                .orElseThrow(() -> new RecursoNoEncontradoException("la tienda del vendedor", vendedorId));
        return pedidoRepository.obtenerPorTiendaId(tienda.getId());
    }
}
