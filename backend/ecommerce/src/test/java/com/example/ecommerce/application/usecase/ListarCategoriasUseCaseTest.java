package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Categoria;
import com.example.ecommerce.domain.repository.CategoriaRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarCategoriasUseCaseTest {

    @Mock
    private CategoriaRepository repository;

    @InjectMocks
    private ListarCategoriasUseCase useCase;

    @Test
    void deberiaRetornarTodasLasCategorias() {

        // Arrange
        when(repository.listarTodas()).thenReturn(List.of(
                Categoria.crear("x-1", "Uno"), Categoria.crear("x-2", "Dos")));

        // Act
        List<Categoria> resultado = useCase.ejecutar();

        // Assert
        assertEquals(2, resultado.size());
    }

    @Test
    void deberiaRetornarUnaListaVaciaSiNoHayCategorias() {

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
