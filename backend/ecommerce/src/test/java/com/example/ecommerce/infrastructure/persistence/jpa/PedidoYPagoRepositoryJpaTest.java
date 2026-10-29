package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.domain.entity.Pago;
import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.valueobject.DetallePedido;
import com.example.ecommerce.domain.valueobject.EstadoPago;
import com.example.ecommerce.domain.valueobject.EstadoPedido;
import com.example.ecommerce.domain.valueobject.MedioPago;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.PagoPersistenceMapper;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.PedidoPersistenceMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** El ciclo de compra completo guardado en H2: pedido confirmado, pago aprobado y pedido entregado. */
@DataJpaTest
@Import({PedidoRepositoryJpa.class, PedidoPersistenceMapper.class,
        PagoRepositoryJpa.class, PagoPersistenceMapper.class})
class PedidoYPagoRepositoryJpaTest {

    @Autowired
    private PedidoRepositoryJpa pedidos;

    @Autowired
    private PagoRepositoryJpa pagos;

    private Pedido pedido(String id, String clienteId) {
        return Pedido.crear(id, clienteId, List.of(
                new DetallePedido("a-1", 2, new Precio(new BigDecimal("20000"))),
                new DetallePedido("a-2", 1, new Precio(new BigDecimal("15000")))));
    }

    @Test
    void guardaElPedidoConSusDetallesYPreciosCongelados() {
        Pedido pedido = pedido("p-1", "u-1");
        pedido.confirmar();
        pedidos.guardar(pedido);

        Pedido leido = pedidos.obtenerPorId("p-1").orElseThrow();

        assertEquals(EstadoPedido.CONFIRMADO, leido.getEstado());
        assertEquals(2, leido.getDetalles().size());
        assertEquals("a-1", leido.getDetalles().get(0).getArticuloId());
        assertEquals(0, new BigDecimal("55000").compareTo(leido.calcularTotal()));
    }

    @Test
    void elCicloDeCompraQuedaGuardado() {
        Pedido pedido = pedido("p-1", "u-1");
        pedido.confirmar();
        pedidos.guardar(pedido);

        Pago pago = Pago.crear("pa-1", "p-1", new Precio(pedido.calcularTotal()), MedioPago.NEQUI);
        pago.aprobar("TX-123");
        pagos.guardar(pago);

        pedido.registrarPago(pago);
        pedido.iniciarPreparacion();
        pedido.enviar();
        pedido.entregar();
        pedidos.guardar(pedido);

        Pedido leido = pedidos.obtenerPorId("p-1").orElseThrow();
        Pago pagoLeido = pagos.obtenerPorId("pa-1").orElseThrow();
        assertEquals(EstadoPedido.ENTREGADO, leido.getEstado());
        assertEquals("pa-1", leido.getPagoConfirmadoId());
        assertEquals(EstadoPago.APROBADO, pagoLeido.getEstado());
        assertEquals("TX-123", pagoLeido.getTransaccionId());
        assertEquals(MedioPago.NEQUI, pagoLeido.getMedioPago());
    }

    @Test
    void buscaLosPedidosDeUnClienteYLosPagosDeUnPedido() {
        pedidos.guardar(pedido("p-1", "u-1"));
        pedidos.guardar(pedido("p-2", "u-2"));
        pedidos.guardar(pedido("p-3", "u-1"));

        Pago rechazado = Pago.crear("pa-1", "p-1", new Precio(new BigDecimal("55000")), MedioPago.PSE);
        rechazado.rechazar("TX-1", "Fondos insuficientes");
        pagos.guardar(rechazado);
        pagos.guardar(Pago.crear("pa-2", "p-1", new Precio(new BigDecimal("55000")), MedioPago.PSE));

        assertEquals(2, pedidos.obtenerPorClienteId("u-1").size());
        assertEquals(3, pedidos.listarTodos().size());
        assertEquals(2, pagos.obtenerPorPedidoId("p-1").size());
        assertEquals("Fondos insuficientes", pagos.obtenerPorId("pa-1").orElseThrow().getMotivoRechazo());
    }
}
