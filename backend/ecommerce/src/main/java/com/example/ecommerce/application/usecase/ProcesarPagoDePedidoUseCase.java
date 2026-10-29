package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;

import com.example.ecommerce.application.PasarelaDePagos;
import com.example.ecommerce.application.ResultadoCobro;
import com.example.ecommerce.domain.entity.Pago;
import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.repository.PagoRepository;
import com.example.ecommerce.domain.repository.PedidoRepository;
import com.example.ecommerce.domain.valueobject.MedioPago;
import com.example.ecommerce.domain.valueobject.Precio;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProcesarPagoDePedidoUseCase {

    private final PedidoRepository pedidoRepository;
    private final PagoRepository pagoRepository;
    private final PasarelaDePagos pasarelaDePagos;

    @Transactional
    public Pago ejecutar(String pagoId, String pedidoId, String clienteId, MedioPago medioPago) {
        Pedido pedido = pedidoRepository.obtenerPorId(pedidoId).orElseThrow(() -> new RecursoNoEncontradoException("el pedido", pedidoId));

        // Todo se verifica ANTES de cobrar, para no cobrar un pedido ajeno o ya pagado.
        pedido.verificarQuePerteneceA(clienteId);
        pedido.verificarQueAceptaPago();

        // El monto sale del pedido, nunca de afuera (RN54).
        Pago pago = Pago.crear(pagoId, pedidoId, new Precio(pedido.calcularTotal()), medioPago);

        ResultadoCobro resultado = pasarelaDePagos.cobrar(pago.getMonto(), medioPago);

        pago.registrarResultado(resultado.aprobado(), resultado.transaccionId(), resultado.motivoRechazo());
        pedido.registrarSiFueAprobado(pago);

        pedidoRepository.guardar(pedido);
        pagoRepository.guardar(pago);

        return pago;
    }
}