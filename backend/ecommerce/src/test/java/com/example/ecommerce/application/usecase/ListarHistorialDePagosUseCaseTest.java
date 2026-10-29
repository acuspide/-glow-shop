package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Pago;
import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.repository.PagoRepository;
import com.example.ecommerce.domain.repository.PedidoRepository;
import com.example.ecommerce.domain.valueobject.DetallePedido;
import com.example.ecommerce.domain.valueobject.EstadoPago;
import com.example.ecommerce.domain.valueobject.MedioPago;
import com.example.ecommerce.domain.valueobject.Precio;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarHistorialDePagosUseCaseTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PagoRepository pagoRepository;

    @InjectMocks
    private ListarHistorialDePagosUseCase useCase;

    private Pedido pedido(String id) {
        return Pedido.crear(id, "u-100", "t-1",
                List.of(new DetallePedido("a-1", 1, new Precio(new BigDecimal("20000")))));
    }

    // Se usa reconstruir para fijar fechas exactas y poder comprobar el orden.
    private Pago pago(String id, String pedidoId, MedioPago medio, EstadoPago estado, String fecha) {
        return Pago.reconstruir(id, pedidoId, new Precio(new BigDecimal("20000")), medio,
                LocalDateTime.parse(fecha), estado, "tx-" + id,
                estado == EstadoPago.RECHAZADO ? "Fondos insuficientes" : null);
    }

    @Test
    void deberiaListarLosPagosDeTodosLosPedidosDeLaClienta() {

        // Arrange
        when(pedidoRepository.obtenerPorClienteId("u-100")).thenReturn(List.of(pedido("p-1"), pedido("p-2")));
        when(pagoRepository.obtenerPorPedidoId("p-1")).thenReturn(List.of(
                pago("pa-1", "p-1", MedioPago.NEQUI, EstadoPago.RECHAZADO, "2026-10-20T10:00"),
                pago("pa-2", "p-1", MedioPago.TARJETA_CREDITO, EstadoPago.APROBADO, "2026-10-20T10:05")));
        when(pagoRepository.obtenerPorPedidoId("p-2")).thenReturn(List.of(
                pago("pa-3", "p-2", MedioPago.PSE, EstadoPago.APROBADO, "2026-10-21T09:00")));

        // Act
        List<Pago> historial = useCase.ejecutar("u-100");

        // Assert
        assertEquals(3, historial.size());
    }

    @Test
    void deberiaOrdenarDelMasRecienteAlMasAntiguo() {

        // Arrange
        when(pedidoRepository.obtenerPorClienteId("u-100")).thenReturn(List.of(pedido("p-1"), pedido("p-2")));
        when(pagoRepository.obtenerPorPedidoId("p-1")).thenReturn(List.of(
                pago("pa-1", "p-1", MedioPago.NEQUI, EstadoPago.RECHAZADO, "2026-10-20T10:00"),
                pago("pa-2", "p-1", MedioPago.TARJETA_CREDITO, EstadoPago.APROBADO, "2026-10-20T10:05")));
        when(pagoRepository.obtenerPorPedidoId("p-2")).thenReturn(List.of(
                pago("pa-3", "p-2", MedioPago.PSE, EstadoPago.APROBADO, "2026-10-21T09:00")));

        // Act
        List<Pago> historial = useCase.ejecutar("u-100");

        // Assert
        assertEquals("pa-3", historial.get(0).getId());   // 21/10 09:00
        assertEquals("pa-2", historial.get(1).getId());   // 20/10 10:05
        assertEquals("pa-1", historial.get(2).getId());   // 20/10 10:00
    }

    @Test
    void deberiaIncluirLosPagosRechazadosConSuMotivo() {

        // Arrange
        when(pedidoRepository.obtenerPorClienteId("u-100")).thenReturn(List.of(pedido("p-1")));
        when(pagoRepository.obtenerPorPedidoId("p-1")).thenReturn(List.of(
                pago("pa-1", "p-1", MedioPago.NEQUI, EstadoPago.RECHAZADO, "2026-10-20T10:00")));

        // Act
        List<Pago> historial = useCase.ejecutar("u-100");

        // Assert
        assertEquals(EstadoPago.RECHAZADO, historial.get(0).getEstado());
        assertEquals("Fondos insuficientes", historial.get(0).getMotivoRechazo());
    }

    @Test
    void deberiaRetornarUnaListaVaciaSiLaClientaNoTienePedidos() {

        // Arrange
        when(pedidoRepository.obtenerPorClienteId("u-100")).thenReturn(List.of());

        // Act
        List<Pago> historial = useCase.ejecutar("u-100");

        // Assert
        assertTrue(historial.isEmpty());
        verifyNoInteractions(pagoRepository);
    }

    @Test
    void listarNoDebeModificarNiGuardarNada() {

        // Arrange
        when(pedidoRepository.obtenerPorClienteId("u-100")).thenReturn(List.of(pedido("p-1")));
        when(pagoRepository.obtenerPorPedidoId("p-1")).thenReturn(List.of(
                pago("pa-1", "p-1", MedioPago.NEQUI, EstadoPago.APROBADO, "2026-10-20T10:00")));

        // Act
        useCase.ejecutar("u-100");

        // Assert
        verify(pagoRepository, never()).guardar(any());
        verify(pedidoRepository, never()).guardar(any());
    }
}
