package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Pago;
import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.exception.PagoNoReintentableException;
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
import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReintentarPagoUseCaseTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProcesarPagoDePedidoUseCase procesarPagoDePedidoUseCase;

    @InjectMocks
    private ReintentarPagoUseCase useCase;

    private Pedido pedidoConfirmadoDeAna() {
        Pedido pedido = Pedido.crear("p-1", "u-100",
                List.of(new DetallePedido("a-1", 2, new Precio(new BigDecimal("20000")))));
        pedido.confirmar();
        return pedido;
    }

    // Primer intento de Ana con Nequi, rechazado por fondos insuficientes.
    private Pago pagoRechazado() {
        Pago pago = Pago.crear("pa-1", "p-1", new Precio(new BigDecimal("40000")), MedioPago.NEQUI);
        pago.rechazar("tx-1", "Fondos insuficientes");
        return pago;
    }

    @Test
    void deberiaReintentarConElMismoMedioCuandoNoSeEligeOtro() {

        // Arrange
        Pago nuevoPago = Pago.crear("pa-2", "p-1", new Precio(new BigDecimal("40000")), MedioPago.NEQUI);
        when(pagoRepository.obtenerPorId("pa-1")).thenReturn(Optional.of(pagoRechazado()));
        when(pedidoRepository.obtenerPorId("p-1")).thenReturn(Optional.of(pedidoConfirmadoDeAna()));
        when(procesarPagoDePedidoUseCase.ejecutar("pa-2", "p-1", "u-100", MedioPago.NEQUI))
                .thenReturn(nuevoPago);

        // Act
        Pago resultado = useCase.ejecutar("pa-2", "pa-1", "u-100", null);

        // Assert
        assertEquals(nuevoPago, resultado);
        verify(procesarPagoDePedidoUseCase).ejecutar("pa-2", "p-1", "u-100", MedioPago.NEQUI);
    }

    @Test
    void deberiaReintentarConOtroMedioCuandoLaClientaLoElige() {

        // Arrange
        when(pagoRepository.obtenerPorId("pa-1")).thenReturn(Optional.of(pagoRechazado()));
        when(pedidoRepository.obtenerPorId("p-1")).thenReturn(Optional.of(pedidoConfirmadoDeAna()));

        // Act
        useCase.ejecutar("pa-2", "pa-1", "u-100", MedioPago.PSE);

        // Assert
        verify(procesarPagoDePedidoUseCase).ejecutar("pa-2", "p-1", "u-100", MedioPago.PSE);
    }

    @Test
    void elPagoRechazadoDebeQuedarIntactoEnElHistorial() {

        // Arrange
        Pago pagoRechazado = pagoRechazado();
        when(pagoRepository.obtenerPorId("pa-1")).thenReturn(Optional.of(pagoRechazado));
        when(pedidoRepository.obtenerPorId("p-1")).thenReturn(Optional.of(pedidoConfirmadoDeAna()));

        // Act
        useCase.ejecutar("pa-2", "pa-1", "u-100", MedioPago.PSE);

        // Assert
        assertEquals(EstadoPago.RECHAZADO, pagoRechazado.getEstado());
        assertEquals("Fondos insuficientes", pagoRechazado.getMotivoRechazo());
        verify(pagoRepository, never()).guardar(any());
    }

    @Test
    void noDebeReintentarUnPagoQueNoFueRechazado() {

        // Arrange
        Pago pagoAprobado = Pago.crear("pa-1", "p-1", new Precio(new BigDecimal("40000")), MedioPago.NEQUI);
        pagoAprobado.aprobar("tx-1");
        when(pagoRepository.obtenerPorId("pa-1")).thenReturn(Optional.of(pagoAprobado));
        when(pedidoRepository.obtenerPorId("p-1")).thenReturn(Optional.of(pedidoConfirmadoDeAna()));

        // Act y Assert
        assertThrows(PagoNoReintentableException.class, () -> {
            useCase.ejecutar("pa-2", "pa-1", "u-100", null);
        });

        verifyNoInteractions(procesarPagoDePedidoUseCase);
    }

    @Test
    void noDebeReintentarElPagoDeOtraClienta() {

        // Arrange
        when(pagoRepository.obtenerPorId("pa-1")).thenReturn(Optional.of(pagoRechazado()));
        when(pedidoRepository.obtenerPorId("p-1")).thenReturn(Optional.of(pedidoConfirmadoDeAna()));

        // Act y Assert
        assertThrows(PedidoAjenoException.class, () -> {
            useCase.ejecutar("pa-2", "pa-1", "u-200", null);
        });

        verifyNoInteractions(procesarPagoDePedidoUseCase);
    }

    @Test
    void deberiaLanzarExcepcionCuandoElPagoNoExiste() {

        // Arrange
        when(pagoRepository.obtenerPorId("pa-99")).thenReturn(Optional.empty());

        // Act y Assert
        assertThrows(RecursoNoEncontradoException.class, () -> {
            useCase.ejecutar("pa-2", "pa-99", "u-100", null);
        });

        verifyNoInteractions(pedidoRepository, procesarPagoDePedidoUseCase);
    }
}

