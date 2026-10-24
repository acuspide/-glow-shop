package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Pago;
import com.example.ecommerce.domain.repository.PagoRepository;
import com.example.ecommerce.domain.repository.PedidoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/**
 * CU-79. Todos los pagos de la clienta (aprobados, rechazados y reembolsados),
 * del más reciente al más antiguo. Un pago no sabe de quién es; su pedido sí.
 */
@Service
@RequiredArgsConstructor
public class ListarHistorialDePagosUseCase {

    private final PedidoRepository pedidoRepository;
    private final PagoRepository pagoRepository;

    @Transactional(readOnly = true)
    public List<Pago> ejecutar(String clienteId) {
        return pedidoRepository.obtenerPorClienteId(clienteId).stream()
                .flatMap(pedido -> pagoRepository.obtenerPorPedidoId(pedido.getId()).stream())
                .sorted(Comparator.comparing(Pago::getFechaPago).reversed())
                .toList();
    }
}