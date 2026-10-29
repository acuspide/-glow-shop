package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;

import com.example.ecommerce.domain.entity.Pago;
import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.repository.PagoRepository;
import com.example.ecommerce.domain.repository.PedidoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CU-77. Por ahora solo la clienta dueña del pedido puede consultar su pago.
 * El actor Vendedor se habilita cuando exista la relación vendedor-tienda.
 */
@Service
@RequiredArgsConstructor
public class ConsultarEstadoDePagoUseCase {

    private final PagoRepository pagoRepository;
    private final PedidoRepository pedidoRepository;

    @Transactional(readOnly = true)
    public Pago ejecutar(String pagoId, String clienteId) {
        Pago pago = pagoRepository.obtenerPorId(pagoId).orElseThrow(() -> new RecursoNoEncontradoException("el pago", pagoId));
        Pedido pedido = pedidoRepository.obtenerPorId(pago.getPedidoId()).orElseThrow(() -> new RecursoNoEncontradoException("el pedido", pago.getPedidoId()));
        pedido.verificarQuePerteneceA(clienteId);
        return pago;
    }
}