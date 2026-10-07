package com.example.ecommerce.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EstadoPedidoTest {

    @Test
    void unPedidoPendienteDebePoderConfirmarse() {

        // Arrange
        EstadoPedido estado = EstadoPedido.PENDIENTE;

        // Act
        boolean puedeConfirmarse = estado.puedeTransicionarA(EstadoPedido.CONFIRMADO);

        // Assert
        assertTrue(puedeConfirmarse);
    }

    @Test
    void unPedidoCanceladoNuncaDebeVolverAConfirmado() {

        // Arrange
        EstadoPedido estado = EstadoPedido.CANCELADO;

        // Act
        boolean puedeConfirmarse = estado.puedeTransicionarA(EstadoPedido.CONFIRMADO);

        // Assert
        assertFalse(puedeConfirmarse);
    }

    @Test
    void unPedidoCanceladoNoDebePoderTransicionarANingunEstado() {

        // Arrange
        EstadoPedido estado = EstadoPedido.CANCELADO;

        // Act
        // No necesitamos una accion adicional, se prueban ambas
        // transiciones directamente en el assert.

        // Assert
        assertFalse(estado.puedeTransicionarA(EstadoPedido.PENDIENTE));
        assertFalse(estado.puedeTransicionarA(EstadoPedido.CANCELADO));
    }
    @Test
    void unPedidoConfirmadoDebePoderPasarAEnPreparacion() {

        // Arrange
        EstadoPedido estado = EstadoPedido.CONFIRMADO;

        // Act
        boolean puedePrepararse = estado.puedeTransicionarA(EstadoPedido.EN_PREPARACION);

        // Assert
        assertTrue(puedePrepararse);
    }

    @Test
    void unPedidoPendienteNoDebePasarDirectamenteAEnPreparacion() {

        // Arrange
        EstadoPedido estado = EstadoPedido.PENDIENTE;

        // Act
        boolean puedePrepararse = estado.puedeTransicionarA(EstadoPedido.EN_PREPARACION);

        // Assert
        assertFalse(puedePrepararse);
    }

    @Test
    void unPedidoEnviadoNoDebePoderCancelarse() {

        // Arrange
        EstadoPedido estado = EstadoPedido.ENVIADO;

        // Act
        boolean puedeCancelarse = estado.puedeTransicionarA(EstadoPedido.CANCELADO);

        // Assert
        assertFalse(puedeCancelarse);
    }

    @Test
    void unPedidoEntregadoNoDebePoderTransicionarANingunEstado() {

        // Arrange
        EstadoPedido estado = EstadoPedido.ENTREGADO;

        // Act y Assert
        for (EstadoPedido destino : EstadoPedido.values()) {
            assertFalse(estado.puedeTransicionarA(destino));
        }
    }

    @Test
    void entregadoYCanceladoDebenSerEstadosFinales() {

        // Arrange, Act y Assert
        assertTrue(EstadoPedido.ENTREGADO.esFinal());
        assertTrue(EstadoPedido.CANCELADO.esFinal());
        assertFalse(EstadoPedido.PENDIENTE.esFinal());
    }
}
