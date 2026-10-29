package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.example.ecommerce.domain.valueobject.DetallePedido;
import com.example.ecommerce.domain.valueobject.EstadoTienda;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.infrastructure.persistence.PedidoRepositoryEnMemoria;
import com.example.ecommerce.infrastructure.persistence.TiendaRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListarPedidosDeMiTiendaUseCaseTest {

    private final TiendaRepositoryEnMemoria tiendas = new TiendaRepositoryEnMemoria();
    private final PedidoRepositoryEnMemoria pedidos = new PedidoRepositoryEnMemoria();
    private final ListarPedidosDeMiTiendaUseCase useCase = new ListarPedidosDeMiTiendaUseCase(tiendas, pedidos);

    private Pedido pedido(String id, String clienteId, String tiendaId) {
        return Pedido.crear(id, clienteId, tiendaId,
                List.of(new DetallePedido("a-1", 1, new Precio(new BigDecimal("20000")))));
    }

    @BeforeEach
    void prepararDatos() {
        tiendas.guardar(Tienda.reconstruir("t-1", "Glow", "", "v-1", EstadoTienda.ACTIVA));
        tiendas.guardar(Tienda.reconstruir("t-2", "Bloom", "", "v-2", EstadoTienda.ACTIVA));
        tiendas.guardar(Tienda.reconstruir("t-3", "Nueva", "", "v-3", EstadoTienda.ACTIVA));
        pedidos.guardar(pedido("p-1", "u-1", "t-1"));
        pedidos.guardar(pedido("p-2", "u-1", "t-2"));
        pedidos.guardar(pedido("p-3", "u-2", "t-1"));
    }

    @Test
    void elVendedorVeSoloLosPedidosDeSuTienda() {
        List<Pedido> deVera = useCase.ejecutar("v-1");

        assertEquals(2, deVera.size());
        assertTrue(deVera.stream().allMatch(p -> p.getTiendaId().equals("t-1")));
    }

    @Test
    void unaTiendaSinVentasRecibeUnaListaVacia() {
        assertTrue(useCase.ejecutar("v-3").isEmpty());
    }

    @Test
    void unVendedorSinTiendaRecibeNoEncontrado() {
        assertThrows(RecursoNoEncontradoException.class, () -> useCase.ejecutar("v-9"));
    }
}
