package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Pago;
import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.repository.PagoRepository;
import com.example.ecommerce.domain.repository.PedidoRepository;
import com.example.ecommerce.domain.valueobject.MedioPago;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CU-78. El pago rechazado no se modifica: queda en el historial y se procesa
 * un pago nuevo para el mismo pedido, con el mismo medio o con otro.
 */
@Service
@RequiredArgsConstructor
public class ReintentarPagoUseCase {

    private final PagoRepository pagoRepository;
    private final PedidoRepository pedidoRepository;
    private final ProcesarPagoDePedidoUseCase procesarPagoDePedidoUseCase;

    @Transactional
    public Pago ejecutar(String nuevoPagoId, String pagoRechazadoId, String clienteId, MedioPago medioPago) {
        Pago pagoRechazado = pagoRepository.obtenerPorId(pagoRechazadoId).orElseThrow();
        Pedido pedido = pedidoRepository.obtenerPorId(pagoRechazado.getPedidoId()).orElseThrow();

        pedido.verificarQuePerteneceA(clienteId);
        pagoRechazado.verificarQueSePuedeReintentar();

        // Si no se elige otro medio, se reintenta con el mismo.
        MedioPago medio = (medioPago != null) ? medioPago : pagoRechazado.getMedioPago();

        return procesarPagoDePedidoUseCase.ejecutar(nuevoPagoId, pedido.getId(), clienteId, medio);
    }
}