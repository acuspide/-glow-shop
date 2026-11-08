package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.exception.ParametroInvalidoException;
import com.example.ecommerce.domain.repository.ArticuloVentas;
import com.example.ecommerce.domain.repository.PedidoRepository;
import com.example.ecommerce.domain.valueobject.DetallePedido;
import com.example.ecommerce.domain.valueobject.EstadoPedido;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.infrastructure.persistence.PedidoRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsultarArticulosMasVendidosUseCaseTest {

    private Pedido pedido(String id, EstadoPedido estado, DetallePedido... detalles) {
        Precio cero = new Precio(BigDecimal.ZERO);
        return Pedido.reconstruir(id, "u-1", "t-1", List.of(detalles), estado, null, cero, cero, cero, null);
    }

    private DetallePedido detalle(String articuloId, int cantidad) {
        return new DetallePedido(articuloId, cantidad, new Precio(new BigDecimal("10000")));
    }

    private PedidoRepository repositorioConVentas() {
        PedidoRepository repository = new PedidoRepositoryEnMemoria();
        repository.guardar(pedido("p-1", EstadoPedido.ENTREGADO, detalle("a-1", 2), detalle("a-2", 1)));
        repository.guardar(pedido("p-2", EstadoPedido.ENTREGADO, detalle("a-1", 3)));
        repository.guardar(pedido("p-3", EstadoPedido.ENTREGADO, detalle("a-3", 4)));
        repository.guardar(pedido("p-4", EstadoPedido.CANCELADO, detalle("a-2", 50)));
        repository.guardar(pedido("p-5", EstadoPedido.PENDIENTE, detalle("a-3", 50)));
        return repository;
    }

    @Test
    void ordenaPorUnidadesVendidasDeMayorAMenor() {
        ConsultarArticulosMasVendidosUseCase useCase =
                new ConsultarArticulosMasVendidosUseCase(repositorioConVentas());

        List<ArticuloVentas> ranking = useCase.ejecutar(10);

        assertEquals(List.of("a-1", "a-3", "a-2"),
                ranking.stream().map(ArticuloVentas::articuloId).toList());
        assertEquals(List.of(5L, 4L, 1L),
                ranking.stream().map(ArticuloVentas::unidadesVendidas).toList());
    }

    @Test
    void noCuentaPedidosCanceladosNiPendientes() {
        ConsultarArticulosMasVendidosUseCase useCase =
                new ConsultarArticulosMasVendidosUseCase(repositorioConVentas());

        ArticuloVentas segundo = useCase.ejecutar(10).get(1);

        assertEquals("a-3", segundo.articuloId());
        assertEquals(4L, segundo.unidadesVendidas());
    }

    @Test
    void respetaElLimite() {
        ConsultarArticulosMasVendidosUseCase useCase =
                new ConsultarArticulosMasVendidosUseCase(repositorioConVentas());

        assertEquals(2, useCase.ejecutar(2).size());
    }

    @Test
    void devuelveListaVaciaSinVentas() {
        ConsultarArticulosMasVendidosUseCase useCase =
                new ConsultarArticulosMasVendidosUseCase(new PedidoRepositoryEnMemoria());

        assertTrue(useCase.ejecutar(5).isEmpty());
    }

    @Test
    void noAceptaUnLimiteFueraDeRango() {
        ConsultarArticulosMasVendidosUseCase useCase =
                new ConsultarArticulosMasVendidosUseCase(new PedidoRepositoryEnMemoria());

        assertThrows(ParametroInvalidoException.class, () -> useCase.ejecutar(0));
        assertThrows(ParametroInvalidoException.class, () -> useCase.ejecutar(21));
    }
}
