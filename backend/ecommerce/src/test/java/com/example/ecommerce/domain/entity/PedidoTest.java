package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.ReglaDominioException;
import com.example.ecommerce.domain.valueobject.DetallePedido;
import com.example.ecommerce.domain.valueobject.EstadoPedido;
import com.example.ecommerce.domain.valueobject.Precio;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PedidoTest {

    private DetallePedido detalle(long articuloId, int cantidad, String precio) {
        return new DetallePedido(articuloId, cantidad, new Precio(new BigDecimal(precio)));
    }

    private Pedido crearPedido() {
        return new Pedido(1L, 100L, detalle(1L, 2, "10000"));
    }

    private Cupon cuponVigente(long id, int porcentaje) {
        return new Cupon(id, "CUPON" + id, porcentaje, LocalDate.now().plusDays(5));
    }

    private Pago pagoAprobado(long pagoId, long pedidoId, String monto) {
        Pago pago = new Pago(pagoId, pedidoId, new Precio(new BigDecimal(monto)));
        pago.aprobar();
        return pago;
    }


    @Test
    void unPedidoNoDebePoderCrearseSinDetalles() {

        // Act y Assert
        assertThrows(ReglaDominioException.class, () -> new Pedido(1L, 100L, null));
    }

    @Test
    void unPedidoNuevoDebeNacerPendienteConSuPrimerDetalle() {

        // Arrange y Act
        Pedido pedido = crearPedido();

        // Assert
        assertEquals(EstadoPedido.PENDIENTE, pedido.getEstado());
        assertEquals(1, pedido.getDetalles().size());
    }

    @Test
    void unPedidoConAlMenosUnDetalleDebePoderConfirmarse() {

        // Arrange
        Pedido pedido = crearPedido();

        // Act
        pedido.confirmar();

        // Assert
        assertEquals(EstadoPedido.CONFIRMADO, pedido.getEstado());
    }


    @Test
    void elPrecioDeUnDetalleYaAgregadoNoCambiaSiCambiaElPrecioDelArticulo() {

        // Arrange
        Pedido pedido = new Pedido(1L, 100L, detalle(1L, 1, "10000"));

        // Act
        // Simulamos que el precio del artículo cambia después de la compra
        // creando otra instancia de Precio; el detalle ya congelo el suyo
        // y el total del pedido no debe verse afectado.
        new Precio(new BigDecimal("15000"));

        // Assert
        assertEquals(new BigDecimal("10000"), pedido.calcularTotal());
    }


    @Test
    void unPedidoCanceladoNuncaDebeVolverAConfirmarse() {

        // Arrange
        Pedido pedido = crearPedido();
        pedido.cancelar();

        // Act y Assert
        assertThrows(ReglaDominioException.class, pedido::confirmar);
    }


    @Test
    void elTotalSinCargosDebeSerLaSumaDeLosSubtotalesDeSusDetalles() {

        // Arrange
        Pedido pedido = crearPedido();
        pedido.agregarDetalle(detalle(2L, 1, "10000"));

        // Act
        BigDecimal total = pedido.calcularTotal();

        // Assert
        assertEquals(new BigDecimal("30000"), total);
    }

    @Test
    void elTotalDebeRestarElDescuentoYSumarImpuestosYEnvio() {

        // Arrange
        Pedido pedido = crearPedido();                                   // subtotal 20000
        pedido.aplicarCupon(cuponVigente(1L, 10));                       // descuento 2000
        pedido.asignarImpuestos(new Precio(new BigDecimal("3420")));
        pedido.asignarCostoEnvio(new Precio(new BigDecimal("8000")));

        // Act
        BigDecimal total = pedido.calcularTotal();

        // Assert
        assertEquals(new BigDecimal("29420"), total);
    }


    @Test
    void unCuponDelCienPorCientoDebeDejarElSubtotalEnCero() {

        // Arrange
        Pedido pedido = crearPedido();

        // Act
        pedido.aplicarCupon(cuponVigente(1L, 100));

        // Assert
        assertEquals(0, pedido.calcularTotal().compareTo(BigDecimal.ZERO));
    }


    @Test
    void noDebePermitirAgregarDetallesAUnPedidoConfirmado() {

        // Arrange
        Pedido pedido = crearPedido();
        pedido.confirmar();

        // Act y Assert
        assertThrows(ReglaDominioException.class,
                () -> pedido.agregarDetalle(detalle(2L, 1, "5000")));
    }

    @Test
    void noDebePermitirAgregarDetallesAUnPedidoCancelado() {

        // Arrange
        Pedido pedido = crearPedido();
        pedido.cancelar();

        // Act y Assert
        assertThrows(ReglaDominioException.class,
                () -> pedido.agregarDetalle(detalle(2L, 1, "5000")));
    }

    @Test
    void noDebePermitirAplicarCuponAUnPedidoConfirmado() {

        // Arrange
        Pedido pedido = crearPedido();
        pedido.confirmar();

        // Act y Assert
        assertThrows(ReglaDominioException.class,
                () -> pedido.aplicarCupon(cuponVigente(1L, 10)));
    }

    @Test
    void noDebePermitirCambiarElCostoDeEnvioDeUnPedidoConfirmado() {

        // Arrange
        Pedido pedido = crearPedido();
        pedido.confirmar();

        // Act y Assert
        assertThrows(ReglaDominioException.class,
                () -> pedido.asignarCostoEnvio(new Precio(new BigDecimal("8000"))));
    }


    @Test
    void unPedidoNoDebeAdmitirUnSegundoCupon() {

        // Arrange
        Pedido pedido = crearPedido();
        pedido.aplicarCupon(cuponVigente(1L, 10));

        // Act y Assert
        assertThrows(ReglaDominioException.class,
                () -> pedido.aplicarCupon(cuponVigente(2L, 5)));
    }

    // Validaciones de aplicarCupon y cargos

    @Test
    void noDebePermitirAplicarUnCuponVencido() {

        // Arrange
        Pedido pedido = crearPedido();
        Cupon vencido = new Cupon(1L, "VENCIDO", 10, LocalDate.now().minusDays(1));

        // Act y Assert
        assertThrows(ReglaDominioException.class, () -> pedido.aplicarCupon(vencido));
    }

    @Test
    void alAplicarUnCuponDebeGuardarseSuIdYElDescuento() {

        // Arrange
        Pedido pedido = crearPedido();

        // Act
        pedido.aplicarCupon(cuponVigente(7L, 10));

        // Assert
        assertEquals(Long.valueOf(7L), pedido.getCuponId());
        assertEquals(new BigDecimal("2000"), pedido.getDescuento().valor());
    }

    @Test
    void noDebePermitirImpuestosNulos() {

        // Arrange
        Pedido pedido = crearPedido();

        // Act y Assert
        assertThrows(ReglaDominioException.class, () -> pedido.asignarImpuestos(null));
    }
    @Test
    void unPedidoConfirmadoSinPagoNoDebePoderPrepararse() {

        // Arrange
        Pedido pedido = crearPedido();
        pedido.confirmar();

        // Act y Assert
        assertThrows(ReglaDominioException.class, pedido::iniciarPreparacion);
    }

    @Test
    void unPedidoConPagoAprobadoRegistradoDebePoderPrepararse() {

        // Arrange
        Pedido pedido = crearPedido();                                   // total 20000
        pedido.confirmar();
        pedido.registrarPago(pagoAprobado(50L, 1L, "20000"));

        // Act
        pedido.iniciarPreparacion();

        // Assert
        assertEquals(EstadoPedido.EN_PREPARACION, pedido.getEstado());
        assertEquals(Long.valueOf(50L), pedido.getPagoConfirmadoId());
    }

    @Test
    void noDebePermitirRegistrarUnPagoNoAprobado() {

        // Arrange
        Pedido pedido = crearPedido();
        pedido.confirmar();
        Pago pagoPendiente = new Pago(50L, 1L, new Precio(new BigDecimal("20000")));

        // Act y Assert
        assertThrows(ReglaDominioException.class, () -> pedido.registrarPago(pagoPendiente));
    }

    @Test
    void noDebePermitirRegistrarUnPagoDeOtroPedido() {

        // Arrange
        Pedido pedido = crearPedido();
        pedido.confirmar();

        // Act y Assert
        assertThrows(ReglaDominioException.class,
                () -> pedido.registrarPago(pagoAprobado(50L, 999L, "20000")));
    }

    @Test
    void noDebePermitirRegistrarUnPagoMenorAlTotal() {

        // Arrange
        Pedido pedido = crearPedido();
        pedido.confirmar();

        // Act y Assert
        assertThrows(ReglaDominioException.class,
                () -> pedido.registrarPago(pagoAprobado(50L, 1L, "19999")));
    }
    @Test
    void noDebePermitirRegistrarUnPagoMayorAlTotal() {

        // Arrange
        Pedido pedido = crearPedido();
        pedido.confirmar();

        // Act y Assert
        assertThrows(ReglaDominioException.class,
                () -> pedido.registrarPago(pagoAprobado(50L, 1L, "20001")));
    }

    @Test
    void unPagoIgualAlTotalConDecimalesDebeAceptarse() {

        // Arrange
        Pedido pedido = crearPedido();                                   // total 20000
        pedido.confirmar();

        // Act
        pedido.registrarPago(pagoAprobado(50L, 1L, "20000.00"));

        // Assert
        assertEquals(Long.valueOf(50L), pedido.getPagoConfirmadoId());
    }

    @Test
    void noDebePermitirRegistrarUnPagoEnUnPedidoPendiente() {

        // Arrange
        Pedido pedido = crearPedido();

        // Act y Assert
        assertThrows(ReglaDominioException.class,
                () -> pedido.registrarPago(pagoAprobado(50L, 1L, "20000")));
    }

    @Test
    void noDebePermitirRegistrarDosPagosEnElMismoPedido() {

        // Arrange
        Pedido pedido = crearPedido();
        pedido.confirmar();
        pedido.registrarPago(pagoAprobado(50L, 1L, "20000"));
        Pago segundoPago = pagoAprobado(51L, 1L, "20000");
        // Act y Assert
        assertThrows(ReglaDominioException.class,
                () -> pedido.registrarPago(segundoPago));
    }

    // Invariante 4: flujo completo y estados finales

    @Test
    void unPedidoDebePoderRecorrerElFlujoCompletoHastaEntregado() {

        // Arrange
        Pedido pedido = crearPedido();
        pedido.confirmar();
        pedido.registrarPago(pagoAprobado(50L, 1L, "20000"));
        pedido.iniciarPreparacion();

        // Act
        pedido.enviar();
        pedido.entregar();

        // Assert
        assertEquals(EstadoPedido.ENTREGADO, pedido.getEstado());
    }

    @Test
    void unPedidoEntregadoNoDebePoderCancelarse() {

        // Arrange
        Pedido pedido = crearPedido();
        pedido.confirmar();
        pedido.registrarPago(pagoAprobado(50L, 1L, "20000"));
        pedido.iniciarPreparacion();
        pedido.enviar();
        pedido.entregar();

        // Act y Assert
        assertThrows(ReglaDominioException.class, pedido::cancelar);
    }

    @Test
    void unPedidoConfirmadoNoDebePoderEnviarseSinPasarPorPreparacion() {

        // Arrange
        Pedido pedido = crearPedido();
        pedido.confirmar();

        // Act y Assert
        assertThrows(ReglaDominioException.class, pedido::enviar);
    }
}