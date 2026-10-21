package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConsultarInventarioCompletoUseCaseTest {
    @Mock
    private ArticuloRepository repository;

    @InjectMocks
    private ConsultarInventarioCompletoUseCase useCase;

    @Test
    void debeRetornarTodosLosArticulosDelRepositorio() {
        List<Articulo> articulos = List.of(
                org.mockito.Mockito.mock(Articulo.class),
                org.mockito.Mockito.mock(Articulo.class)
        );

        when(repository.listarTodos()).thenReturn(articulos);

        List<Articulo> resultado = useCase.ejecutar();

        assertEquals(2, resultado.size());
        assertEquals(articulos, resultado);
        verify(repository).listarTodos();
    }

    @Test
    void debeRetornarListaVaciaCuandoNoHayArticulos() {
        when(repository.listarTodos()).thenReturn(List.of());

        List<Articulo> resultado = useCase.ejecutar();

        assertTrue(resultado.isEmpty());
        verify(repository).listarTodos();
    }
}
