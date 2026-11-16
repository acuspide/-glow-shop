package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.example.ecommerce.domain.repository.TiendaRepository;
import com.example.ecommerce.domain.valueobject.EstadoTienda;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultarMiTiendaUseCaseTest {

    @Mock
    private TiendaRepository tiendaRepository;

    @InjectMocks
    private ConsultarMiTiendaUseCase useCase;

    @Test
    void deberiaRetornarLaTiendaDelVendedor() {

        // Arrange
        Tienda tienda = Tienda.reconstruir("t-1", "Glow Natural", "Maquillaje", "v-1", EstadoTienda.ACTIVA);
        when(tiendaRepository.obtenerPorVendedorId("v-1")).thenReturn(Optional.of(tienda));

        // Act
        Tienda resultado = useCase.ejecutar("v-1");

        // Assert
        assertEquals("t-1", resultado.getId());
        assertEquals(EstadoTienda.ACTIVA, resultado.getEstado());
    }

    @Test
    void deberiaLanzarExcepcionSiElVendedorNoTieneTienda() {

        // Arrange
        when(tiendaRepository.obtenerPorVendedorId("v-1")).thenReturn(Optional.empty());

        // Act y Assert
        assertThrows(RecursoNoEncontradoException.class, () -> useCase.ejecutar("v-1"));
    }

    @Test
    void consultarNoDebeGuardarNada() {

        // Arrange
        Tienda tienda = Tienda.reconstruir("t-1", "Glow Natural", "Maquillaje", "v-1", EstadoTienda.ACTIVA);
        when(tiendaRepository.obtenerPorVendedorId("v-1")).thenReturn(Optional.of(tienda));

        // Act
        useCase.ejecutar("v-1");

        // Assert
        verify(tiendaRepository, never()).guardar(any());
    }
}