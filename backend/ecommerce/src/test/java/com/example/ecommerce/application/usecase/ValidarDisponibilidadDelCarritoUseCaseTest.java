package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Carrito;
import com.example.ecommerce.domain.exception.PrecioDelArticuloCambioException;
import com.example.ecommerce.domain.repository.CarritoRepository;
import com.example.ecommerce.domain.service.VerificadorDeDisponibilidadDelCarrito;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidarDisponibilidadDelCarritoUseCaseTest {

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private VerificadorDeDisponibilidadDelCarrito verificador;

    @InjectMocks
    private ValidarDisponibilidadDelCarritoUseCase useCase;

    @Test
    void deberiaVerificarElCarritoDelCliente() {

        // Arrange
        Carrito carrito = Carrito.crear("c-1", "u-100");
        when(carritoRepository.obtenerPorClienteId("u-100")).thenReturn(Optional.of(carrito));

        // Act
        useCase.ejecutar("u-100");

        // Assert
        verify(verificador).verificarQuePuedePagarse(carrito);
    }

    @Test
    void deberiaLanzarExcepcionCuandoElClienteNoTieneCarrito() {

        // Arrange
        when(carritoRepository.obtenerPorClienteId("u-100")).thenReturn(Optional.empty());

        // Act y Assert
        assertThrows(RecursoNoEncontradoException.class, () -> {
            useCase.ejecutar("u-100");
        });

        verify(verificador, never()).verificarQuePuedePagarse(any());
    }

    @Test
    void deberiaPropagarElErrorCuandoElCarritoNoEstaDisponible() {

        // Arrange
        Carrito carrito = Carrito.crear("c-1", "u-100");
        when(carritoRepository.obtenerPorClienteId("u-100")).thenReturn(Optional.of(carrito));
        doThrow(new PrecioDelArticuloCambioException("a-1"))
                .when(verificador).verificarQuePuedePagarse(carrito);

        // Act y Assert
        assertThrows(PrecioDelArticuloCambioException.class, () -> {
            useCase.ejecutar("u-100");
        });
    }

    @Test
    void validarNoDebeModificarNiGuardarElCarrito() {

        // Arrange
        Carrito carrito = Carrito.crear("c-1", "u-100");
        when(carritoRepository.obtenerPorClienteId("u-100")).thenReturn(Optional.of(carrito));

        // Act
        useCase.ejecutar("u-100");

        // Assert
        verify(carritoRepository, never()).guardar(any());
    }
}
