package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.PedidoAjenoException;
import com.example.ecommerce.domain.exception.PedidoSinDetallesException;
import com.example.ecommerce.domain.exception.ReglaDominioException;
import com.example.ecommerce.domain.exception.TiendaRequeridaException;
import com.example.ecommerce.domain.valueobject.DetallePedido;
import com.example.ecommerce.domain.valueobject.EstadoPedido;
import com.example.ecommerce.domain.valueobject.Precio;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.example.ecommerce.domain.exception.PagoNoRegistrableException;
import com.example.ecommerce.domain.valueobject.MedioPago;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
public class PedidoTest {

    private DetallePedido detalle(String articuloId, int cantidad, String precio) {
        return new DetallePedido(articuloId, cantidad, new Precio(new BigDecimal(precio)));
    }

    private Pedido crearPedido() {
        return Pedido.crear("p-1", "u-100", "t-1", List.of(detalle("a-1", 2, "10000")));
    }

    private Cupon cuponVigente(String id, int porcentaje) {
        return Cupon.crear(id, "CUPON" + id, porcentaje, LocalDate.now().plusDays(5));
    }

    private Pago pagoAprobado(String pagoId, String pedidoId, String monto) {
        Pago pago = Pago.crear(pagoId, pedidoId, new Precio(new BigDecimal(monto)), MedioPago.TARJETA_CREDITO);
        pago.aprobar("tx-" + pagoId);
        return pago;
    }


    @Test
    void unPedidoNoDebePoderCrearseSinDetalles() {

        // Act y Assert
        assertThrows(PedidoSinDetallesException.class, () -> Pedido.crear("p-1", "u-100", "t-1", List.of()));
    }

    @Test
    void unPedidoNoDebePoderCrearseConListaDeDetallesNula() {

        // Act y Assert
        assertThrows(PedidoSinDetallesException.class, () -> Pedido.crear("p-1", "u-100", "t-1", null));
    }

    @Test
    void unPedidoDebeCrearseConTodosSusDetalles() {

        // Arrange
        List<DetallePedido> detalles = List.of(detalle("a-1", 2, "10000"), detalle("a-2", 1, "5000"));

        // Act
        Pedido pedido = Pedido.crear("p-1", "u-100", "t-1", detalles);

        // Assert
        assertEquals(2, pedido.getDetalles().size());
        assertEquals(new BigDecimal("25000"), pedido.calcularTotal());
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
        Pedido pedido = Pedido.crear("p-1", "u-100", "t-1", List.of(detalle("a-1", 1, "10000")));

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
        pedido.agregarDetalle(detalle("a-2", 1, "10000"));

        // Act
        BigDecimal total = pedido.calcularTotal();

        // Assert
        assertEquals(new BigDecimal("30000"), total);
    }

    @Test
    void elTotalDebeRestarElDescuentoYSumarImpuestosYEnvio() {

        // Arrange
        Pedido pedido = crearPedido();                                   // subtotal 20000
        pedido.aplicarCupon(cuponVigente("cu-1", 10));                       // descuento 2000
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
        pedido.aplicarCupon(cuponVigente("cu-1", 100));

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
                () -> pedido.agregarDetalle(detalle("a-2", 1, "5000")));
    }

    @Test
    void noDebePermitirAgregarDetallesAUnPedidoCancelado() {

        // Arrange
        Pedido pedido = crearPedido();
        pedido.cancelar();

        // Act y Assert
        assertThrows(ReglaDominioException.class,
                () -> pedido.agregarDetalle(detalle("a-2", 1, "5000")));
    }

    @Test
    void noDebePermitirAplicarCuponAUnPedidoConfirmado() {

        // Arrange
        Pedido pedido = crearPedido();
        pedido.confirmar();

        // Act y Assert
        assertThrows(ReglaDominioException.class,
                () -> pedido.aplicarCupon(cuponVigente("cu-1", 10)));
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
        pedido.aplicarCupon(cuponVigente("cu-1", 10));

        // Act y Assert
        assertThrows(ReglaDominioException.class,
                () -> pedido.aplicarCupon(cuponVigente("cu-2", 5)));
    }

    // Validaciones de aplicarCupon y cargos

    @Test
    void noDebePermitirAplicarUnCuponVencido() {

        // Arrange
        Pedido pedido = crearPedido();
        Cupon vencido = Cupon.crear("cu-1", "VENCIDO", 10, LocalDate.now().minusDays(1));

        // Act y Assert
        assertThrows(ReglaDominioException.class, () -> pedido.aplicarCupon(vencido));
    }

    @Test
    void alAplicarUnCuponDebeGuardarseSuIdYElDescuento() {

        // Arrange
        Pedido pedido = crearPedido();

        // Act
        pedido.aplicarCupon(cuponVigente("cu-7", 10));

        // Assert
        assertEquals("cu-7", pedido.getCuponId());
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
        pedido.registrarPago(pagoAprobado("pa-50", "p-1", "20000"));

        // Act
        pedido.iniciarPreparacion();

        // Assert
        assertEquals(EstadoPedido.EN_PREPARACION, pedido.getEstado());
        assertEquals("pa-50", pedido.getPagoConfirmadoId());
    }

    @Test
    void noDebePermitirRegistrarUnPagoNoAprobado() {

        // Arrange
        Pedido pedido = crearPedido();
        pedido.confirmar();
        Pago pagoPendiente = Pago.crear("pa-50", "p-1", new Precio(new BigDecimal("20000")), MedioPago.TARJETA_CREDITO);

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
                () -> pedido.registrarPago(pagoAprobado("pa-50", "p-999", "20000")));
    }

    @Test
    void noDebePermitirRegistrarUnPagoMenorAlTotal() {

        // Arrange
        Pedido pedido = crearPedido();
        pedido.confirmar();

        // Act y Assert
        assertThrows(ReglaDominioException.class,
                () -> pedido.registrarPago(pagoAprobado("pa-50", "p-1", "19999")));
    }

    @Test
    void noDebePermitirRegistrarUnPagoMayorAlTotal() {

        // Arrange
        Pedido pedido = crearPedido();
        pedido.confirmar();

        // Act y Assert
        assertThrows(ReglaDominioException.class,
                () -> pedido.registrarPago(pagoAprobado("pa-50", "p-1", "20001")));
    }

    @Test
    void unPagoIgualAlTotalConDecimalesDebeAceptarse() {

        // Arrange
        Pedido pedido = crearPedido();                                   // total 20000
        pedido.confirmar();

        // Act
        pedido.registrarPago(pagoAprobado("pa-50", "p-1", "20000.00"));

        // Assert
        assertEquals("pa-50", pedido.getPagoConfirmadoId());
    }

    @Test
    void noDebePermitirRegistrarUnPagoEnUnPedidoPendiente() {

        // Arrange
        Pedido pedido = crearPedido();

        // Act y Assert
        assertThrows(ReglaDominioException.class,
                () -> pedido.registrarPago(pagoAprobado("pa-50", "p-1", "20000")));
    }

    @Test
    void noDebePermitirRegistrarDosPagosEnElMismoPedido() {

        // Arrange
        Pedido pedido = crearPedido();
        pedido.confirmar();
        pedido.registrarPago(pagoAprobado("pa-50", "p-1", "20000"));
        Pago segundoPago = pagoAprobado("pa-51", "p-1", "20000");
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
        pedido.registrarPago(pagoAprobado("pa-50", "p-1", "20000"));
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
        pedido.registrarPago(pagoAprobado("pa-50", "p-1", "20000"));
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
    @Test
    void unPedidoConfirmadoSinPagoDebeAceptarUnPago() {

        // Arrange
        Pedido pedido = crearPedido();
        pedido.confirmar();

        // Act y Assert
        assertDoesNotThrow(pedido::verificarQueAceptaPago);
    }

    @Test
    void unPedidoPendienteNoDebeAceptarUnPago() {

        // Arrange
        Pedido pedido = crearPedido();

        // Act y Assert
        assertThrows(PagoNoRegistrableException.class, pedido::verificarQueAceptaPago);
    }

    @Test
    void unPedidoYaPagadoNoDebeAceptarOtroPago() {

        // Arrange
        Pedido pedido = crearPedido();
        pedido.confirmar();
        pedido.registrarPago(pagoAprobado("pa-50", "p-1", "20000"));

        // Act y Assert
        assertThrows(PagoNoRegistrableException.class, pedido::verificarQueAceptaPago);
    }
    @Test
    void unPedidoDebePertenecerAlClienteQueLoCreo() {

        // Arrange
        Pedido pedido = crearPedido();

        // Act y Assert
        assertDoesNotThrow(() -> pedido.verificarQuePerteneceA(pedido.getClienteId()));
    }

    @Test
    void unPedidoNoDebePertenecerAOtroCliente() {

        // Arrange
        Pedido pedido = crearPedido();

        // Act y Assert
        assertThrows(PedidoAjenoException.class, () -> pedido.verificarQuePerteneceA("otro-cliente"));
    }

    @Test
    void unPedidoGuardaLaTiendaALaQuePertenece() {
        assertEquals("t-1", crearPedido().getTiendaId());
    }

    @Test
    void unPedidoNoPuedeCrearseSinTienda() {
        assertThrows(TiendaRequeridaException.class,
                () -> Pedido.crear("p-1", "u-100", " ", List.of(detalle("a-1", 1, "10000"))));
    }

    // Pedido confirmado y pagado (total 20000), listo para que el vendedor lo despache.
    private Pedido pedidoPagado() {
        Pedido pedido = crearPedido();
        pedido.confirmar();
        pedido.registrarPago(pagoAprobado("pa-50", "p-1", "20000"));
        return pedido;
    }

    @Test
    void elVendedorAvanzaElPedidoHastaEntregado() {
        Pedido pedido = pedidoPagado();

        pedido.avanzarA(EstadoPedido.EN_PREPARACION);
        pedido.avanzarA(EstadoPedido.ENVIADO);
        pedido.avanzarA(EstadoPedido.ENTREGADO);

        assertEquals(EstadoPedido.ENTREGADO, pedido.getEstado());
    }

    @Test
    void noSePuedePrepararUnPedidoSinPago() {
        Pedido pedido = crearPedido();
        pedido.confirmar();

        assertThrows(ReglaDominioException.class, () -> pedido.avanzarA(EstadoPedido.EN_PREPARACION));
        assertEquals(EstadoPedido.CONFIRMADO, pedido.getEstado());
    }

    @Test
    void noSePuedeSaltarDePreparadoAEntregado() {
        Pedido pedido = pedidoPagado();
        pedido.avanzarA(EstadoPedido.EN_PREPARACION);

        assertThrows(ReglaDominioException.class, () -> pedido.avanzarA(EstadoPedido.ENTREGADO));
    }

    @Test
    void elVendedorNoPuedeElegirEstadosQueNoSonDeDespacho() {
        Pedido pedido = pedidoPagado();

        assertThrows(ReglaDominioException.class, () -> pedido.avanzarA(EstadoPedido.CANCELADO));
        assertThrows(ReglaDominioException.class, () -> pedido.avanzarA(EstadoPedido.PENDIENTE));
        assertEquals(EstadoPedido.CONFIRMADO, pedido.getEstado());
    }
}
