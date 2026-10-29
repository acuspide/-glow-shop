package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Pago;
import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.domain.exception.PagoNoRegistradoException;
import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.example.ecommerce.domain.exception.TiendaAjenaException;
import com.example.ecommerce.domain.valueobject.DetallePedido;
import com.example.ecommerce.domain.valueobject.EstadoPedido;
import com.example.ecommerce.domain.valueobject.EstadoTienda;
import com.example.ecommerce.domain.valueobject.MedioPago;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.infrastructure.persistence.PedidoRepositoryEnMemoria;
import com.example.ecommerce.infrastructure.persistence.TiendaRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ActualizarEstadoPedidoUseCaseTest {

    private final PedidoRepositoryEnMemoria pedidos = new PedidoRepositoryEnMemoria();
    private final TiendaRepositoryEnMemoria tiendas = new TiendaRepositoryEnMemoria();
    private final ActualizarEstadoPedidoUseCase useCase = new ActualizarEstadoPedidoUseCase(pedidos, tiendas);

    private Pedido pedido(String id) {
        Pedido pedido = Pedido.crear(id, "u-1", "t-1",
                List.of(new DetallePedido("a-1", 2, new Precio(new BigDecimal("20000")))));
        pedido.confirmar();
        return pedido;
    }

    @BeforeEach
    void prepararDatos() {
        tiendas.guardar(Tienda.reconstruir("t-1", "Glow", "", "v-1", EstadoTienda.ACTIVA));
        Pedido pagado = pedido("p-1");
        Pago pago = Pago.crear("pa-1", "p-1", new Precio(pagado.calcularTotal()), MedioPago.NEQUI);
        pago.aprobar("TX-1");
        pagado.registrarPago(pago);
        pedidos.guardar(pagado);
        pedidos.guardar(pedido("p-2")); // confirmado pero sin pagar
    }

    @Test
    void elVendedorDeLaTiendaDespachaYEntregaSuPedido() {
        useCase.ejecutar("p-1", "v-1", EstadoPedido.EN_PREPARACION);
        useCase.ejecutar("p-1", "v-1", EstadoPedido.ENVIADO);
        Pedido entregado = useCase.ejecutar("p-1", "v-1", EstadoPedido.ENTREGADO);

        assertEquals(EstadoPedido.ENTREGADO, entregado.getEstado());
        assertEquals(EstadoPedido.ENTREGADO, pedidos.obtenerPorId("p-1").orElseThrow().getEstado());
    }

    @Test
    void otroVendedorNoPuedeTocarElPedido() {
        assertThrows(TiendaAjenaException.class, () -> useCase.ejecutar("p-1", "v-2", EstadoPedido.EN_PREPARACION));
        assertEquals(EstadoPedido.CONFIRMADO, pedidos.obtenerPorId("p-1").orElseThrow().getEstado());
    }

    @Test
    void unPedidoSinPagarNoSePuedePreparar() {
        assertThrows(PagoNoRegistradoException.class, () -> useCase.ejecutar("p-2", "v-1", EstadoPedido.EN_PREPARACION));
    }

    @Test
    void unPedidoQueNoExisteRetornaNoEncontrado() {
        assertThrows(RecursoNoEncontradoException.class, () -> useCase.ejecutar("p-9", "v-1", EstadoPedido.ENVIADO));
    }
}
