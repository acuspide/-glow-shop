package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Marca;
import com.example.ecommerce.domain.repository.MarcaRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarMarcasUseCaseTest {

    @Mock
    private MarcaRepository repository;

    @InjectMocks
    private ListarMarcasUseCase useCase;

    @Test
    void deberiaRetornarTodasLasMarcas() {

        // Arrange
        when(repository.listarTodas()).thenReturn(List.of(
                Marca.crear("x-1", "Uno"), Marca.crear("x-2", "Dos")));

        // Act
        List<Marca> resultado = useCase.ejecutar();

        // Assert
        assertEquals(2, resultado.size());
    }

    @Test
    void deberiaRetornarUnaListaVaciaSiNoHayMarcas() {

        // Arrange
        when(repository.listarTodas()).thenReturn(List.of());

        // Act y Assert
        assertTrue(useCase.ejecutar().isEmpty());
    }

    @Test
    void listarNoDebeGuardarNada() {

        // Arrange
        when(repository.listarTodas()).thenReturn(List.of());

        // Act
        useCase.ejecutar();

        // Assert
        verify(repository, never()).guardar(any());
    }
}