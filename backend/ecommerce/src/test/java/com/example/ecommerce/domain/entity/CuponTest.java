package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.ReglaDominioException;
import com.example.ecommerce.domain.valueobject.Precio;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CuponTest {

    @Test
    void unCuponDebeCalcularElDescuentoSegunSuPorcentaje() {

        // Arrange
        Cupon cupon = new Cupon(1L, "BIENVENIDA10", 10, LocalDate.now().plusDays(5));

        // Act
        Precio descuento = cupon.calcularDescuento(new BigDecimal("50000"));

        // Assert
        assertEquals(new BigDecimal("5000"), descuento.valor());
    }

    @Test
    void unCuponConFechaFuturaDebeEstarVigente() {

        // Arrange
        Cupon cupon = new Cupon(1L, "BIENVENIDA10", 10, LocalDate.now().plusDays(5));

        // Act y Assert
        assertTrue(cupon.estaVigente());
    }

    @Test
    void unCuponDebeSeguirVigenteElDiaQueExpira() {

        // Arrange
        Cupon cupon = new Cupon(1L, "BIENVENIDA10", 10, LocalDate.now());

        // Act y Assert
        assertTrue(cupon.estaVigente());
    }

    @Test
    void unCuponConFechaPasadaNoDebeEstarVigente() {

        // Arrange
        Cupon cupon = new Cupon(1L, "BIENVENIDA10", 10, LocalDate.now().minusDays(1));

        // Act y Assert
        assertFalse(cupon.estaVigente());
    }

    @Test
    void unCuponConPorcentajeCeroOMayorACienDebeLanzarReglaDominioException() {

        // Arrange
        LocalDate fecha = LocalDate.now().plusDays(5);

        // Act y Assert
        assertThrows(ReglaDominioException.class, () -> new Cupon(1L, "X", 0, fecha));
        assertThrows(ReglaDominioException.class, () -> new Cupon(1L, "X", 101, fecha));
    }

    @Test
    void unCuponSinCodigoDebeLanzarReglaDominioException() {

        // Arrange
        LocalDate fecha = LocalDate.now().plusDays(5);

        // Act y Assert
        assertThrows(ReglaDominioException.class, () -> new Cupon(1L, " ", 10, fecha));
    }

    @Test
    void unCuponSinFechaDeExpiracionDebeLanzarReglaDominioException() {

        // Act y Assert
        assertThrows(ReglaDominioException.class, () -> new Cupon(1L, "BIENVENIDA10", 10, null));
    }
}
