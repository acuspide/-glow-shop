package com.example.ecommerce.application.usecase;

import com.example.ecommerce.application.PasarelaDePagos;
import com.example.ecommerce.application.ResultadoCobro;
import com.example.ecommerce.domain.entity.Pago;
import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.exception.PagoNoRegistrableException;
import com.example.ecommerce.domain.exception.PedidoAjenoException;
import com.example.ecommerce.domain.repository.PagoRepository;
import com.example.ecommerce.domain.repository.PedidoRepository;
import com.example.ecommerce.domain.valueobject.DetallePedido;
import com.example.ecommerce.domain.valueobject.EstadoPago;
import com.example.ecommerce.domain.valueobject.EstadoPedido;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcesarPagoDePedidoUseCaseTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private PasarelaDePagos pasarelaDePagos;

    @InjectMocks
    private ProcesarPagoDePedidoUseCase useCase;

    // Pedido de Ana: 2 labiales a $20.000 = $40.000, ya confirmado.
    private Pedido pedidoConfirmadoDeAna() {
        Pedido pedido = Pedido.crear("p-1", "u-100", "t-1",
                List.of(new DetallePedido("a-1", 2, new Precio(new BigDecimal("20000")))));
        pedido.confirmar();
        return pedido;
    }

    @Test
    void deberiaAprobarElPagoYRegistrarloEnElPedido() {

        // Arrange
        Pedido pedido = pedidoConfirmadoDeAna();
        when(pedidoRepository.obtenerPorId("p-1")).thenReturn(Optional.of(pedido));
        when(pasarelaDePagos.cobrar(any(), eq(MedioPago.NEQUI)))
                .thenReturn(new ResultadoCobro(true, "tx-1", null));

        // Act
        Pago pago = useCase.ejecutar("pa-1", "p-1", "u-100", MedioPago.NEQUI);

        // Assert
        assertEquals(EstadoPago.APROBADO, pago.getEstado());
        assertEquals("tx-1", pago.getTransaccionId());
        assertEquals(new BigDecimal("40000"), pago.getMonto().valor());
        assertEquals("pa-1", pedido.getPagoConfirmadoId());
        verify(pagoRepository).guardar(pago);
        verify(pedidoRepository).guardar(pedido);
    }

    @Test
    void deberiaGuardarElPagoRechazadoConSuMotivoSinTocarElPedido() {

        // Arrange
        Pedido pedido = pedidoConfirmadoDeAna();
        when(pedidoRepository.obtenerPorId("p-1")).thenReturn(Optional.of(pedido));
        when(pasarelaDePagos.cobrar(any(), eq(MedioPago.TARJETA_CREDITO)))
                .thenReturn(new ResultadoCobro(false, "tx-2", "Fondos insuficientes"));

        // Act
        Pago pago = useCase.ejecutar("pa-1", "p-1", "u-100", MedioPago.TARJETA_CREDITO);

        // Assert
        assertEquals(EstadoPago.RECHAZADO, pago.getEstado());
        assertEquals("Fondos insuficientes", pago.getMotivoRechazo());
        assertNull(pedido.getPagoConfirmadoId());
        assertEquals(EstadoPedido.CONFIRMADO, pedido.getEstado());
        verify(pagoRepository).guardar(pago);
    }

    @Test
    void deberiaLanzarExcepcionCuandoElPedidoNoExiste() {

        // Arrange
        when(pedidoRepository.obtenerPorId("p-99")).thenReturn(Optional.empty());

        // Act y Assert
        assertThrows(RecursoNoEncontradoException.class, () -> {
            useCase.ejecutar("pa-1", "p-99", "u-100", MedioPago.NEQUI);
        });

        verifyNoInteractions(pasarelaDePagos, pagoRepository);
    }

    @Test
    void noDebeCobrarUnPedidoDeOtroCliente() {

        // Arrange
        when(pedidoRepository.obtenerPorId("p-1")).thenReturn(Optional.of(pedidoConfirmadoDeAna()));

        // Act y Assert
        assertThrows(PedidoAjenoException.class, () -> {
            useCase.ejecutar("pa-1", "p-1", "u-200", MedioPago.NEQUI);
        });

        verifyNoInteractions(pasarelaDePagos, pagoRepository);
    }

    @Test
    void noDebeCobrarUnPedidoQueNoEstaConfirmado() {

        // Arrange
        Pedido pedidoPendiente = Pedido.crear("p-1", "u-100", "t-1",
                List.of(new DetallePedido("a-1", 2, new Precio(new BigDecimal("20000")))));
        when(pedidoRepository.obtenerPorId("p-1")).thenReturn(Optional.of(pedidoPendiente));

        // Act y Assert
        assertThrows(PagoNoRegistrableException.class, () -> {
            useCase.ejecutar("pa-1", "p-1", "u-100", MedioPago.NEQUI);
        });

        verifyNoInteractions(pasarelaDePagos, pagoRepository);
    }

    @Test
    void noDebeCobrarDosVecesUnPedidoYaPagado() {

        // Arrange
        Pedido pedido = pedidoConfirmadoDeAna();
        Pago primerPago = Pago.crear("pa-1", "p-1", new Precio(new BigDecimal("40000")), MedioPago.NEQUI);
        primerPago.aprobar("tx-1");
        pedido.registrarPago(primerPago);
        when(pedidoRepository.obtenerPorId("p-1")).thenReturn(Optional.of(pedido));

        // Act y Assert
        assertThrows(PagoNoRegistrableException.class, () -> {
            useCase.ejecutar("pa-2", "p-1", "u-100", MedioPago.NEQUI);
        });

        verifyNoInteractions(pasarelaDePagos, pagoRepository);
    }
}
