package com.example.ecommerce.infrastructure.rest.mapper;

import com.example.ecommerce.application.dto.response.PedidoResponse;
import com.example.ecommerce.domain.entity.Pago;
import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.valueobject.DetallePedido;
import com.example.ecommerce.domain.valueobject.MedioPago;
import com.example.ecommerce.domain.valueobject.Precio;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PedidoMapperTest {

    private final PedidoMapper mapper = new PedidoMapper();

    private Pedido pedidoConfirmado() {
        Pedido pedido = Pedido.crear("p-1", "u-100", "t-1",
                List.of(new DetallePedido("a-1", 2, new Precio(new BigDecimal("20000")))));
        pedido.confirmar();
        return pedido;
    }

    @Test
    void unPedidoSinPagoDebeResponderPagadoFalse() {

        // Act
        PedidoResponse response = mapper.toResponse(pedidoConfirmado());

        // Assert
        assertFalse(response.pagado());
        assertEquals("CONFIRMADO", response.estado());
    }

    @Test
    void unPedidoConPagoAprobadoDebeResponderPagadoTrue() {

        // Arrange
        Pedido pedido = pedidoConfirmado();
        Pago pago = Pago.crear("pa-1", "p-1", new Precio(pedido.calcularTotal()), MedioPago.NEQUI);
        pago.aprobar("tx-1");
        pedido.registrarSiFueAprobado(pago);

        // Act
        PedidoResponse response = mapper.toResponse(pedido);

        // Assert
        assertTrue(response.pagado());
    }
}