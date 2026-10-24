package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Pago;
import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.exception.PedidoAjenoException;
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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultarEstadoDePagoUseCaseTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private ConsultarEstadoDePagoUseCase useCase;

    // Pedido de Ana de $40.000 y un pago suyo rechazado por fondos insuficientes.
    private Pedido pedidoDeAna() {
        return Pedido.crear("p-1", "u-100",
                List.of(new DetallePedido("a-1", 2, new Precio(new BigDecimal("20000")))));
    }

    private Pago pagoRechazadoDeAna() {
        Pago pago = Pago.crear("pa-1", "p-1", new Precio(new BigDecimal("40000")), MedioPago.NEQUI);
        pago.rechazar("tx-1", "Fondos insuficientes");
        return pago;
    }

    @Test
    void deberiaMostrarElEstadoDelPagoALaClientaDuenaDelPedido() {

        // Arrange
        when(pagoRepository.obtenerPorId("pa-1")).thenReturn(Optional.of(pagoRechazadoDeAna()));
        when(pedidoRepository.obtenerPorId("p-1")).thenReturn(Optional.of(pedidoDeAna()));

        // Act
        Pago pago = useCase.ejecutar("pa-1", "u-100");

        // Assert
        assertEquals(EstadoPago.RECHAZADO, pago.getEstado());
        assertEquals("Fondos insuficientes", pago.getMotivoRechazo());
    }

    @Test
    void deberiaLanzarExcepcionCuandoElPagoNoExiste() {

        // Arrange
        when(pagoRepository.obtenerPorId("pa-99")).thenReturn(Optional.empty());

        // Act y Assert
        assertThrows(NoSuchElementException.class, () -> {
            useCase.ejecutar("pa-99", "u-100");
        });

        verifyNoInteractions(pedidoRepository);
    }

    @Test
    void noDebeMostrarElPagoDeOtraClienta() {

        // Arrange
        when(pagoRepository.obtenerPorId("pa-1")).thenReturn(Optional.of(pagoRechazadoDeAna()));
        when(pedidoRepository.obtenerPorId("p-1")).thenReturn(Optional.of(pedidoDeAna()));

        // Act y Assert
        assertThrows(PedidoAjenoException.class, () -> {
            useCase.ejecutar("pa-1", "u-200");
        });
    }

    @Test
    void consultarNoDebeModificarNiGuardarNada() {

        // Arrange
        when(pagoRepository.obtenerPorId("pa-1")).thenReturn(Optional.of(pagoRechazadoDeAna()));
        when(pedidoRepository.obtenerPorId("p-1")).thenReturn(Optional.of(pedidoDeAna()));

        // Act
        useCase.ejecutar("pa-1", "u-100");

        // Assert
        verify(pagoRepository, never()).guardar(any());
        verify(pedidoRepository, never()).guardar(any());
    }
}