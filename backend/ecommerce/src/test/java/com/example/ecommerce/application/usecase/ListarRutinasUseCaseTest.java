package com.example.ecommerce.application.usecase;
import com.example.ecommerce.domain.entity.RutinaCuidado;
import com.example.ecommerce.domain.repository.RutinaCuidadoRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
public class ListarRutinasUseCaseTest {
    @Test
    void debeRetornarTodasLasRutinas() {
        RutinaCuidadoRepository repository =
                mock(RutinaCuidadoRepository.class);

        RutinaCuidado rutina1 = mock(RutinaCuidado.class);
        RutinaCuidado rutina2 = mock(RutinaCuidado.class);

        when(repository.listarTodas())
                .thenReturn(List.of(rutina1, rutina2));

        ListarRutinasUseCase useCase =
                new ListarRutinasUseCase(repository);

        List<RutinaCuidado> resultado = useCase.ejecutar();

        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(rutina1));
        assertTrue(resultado.contains(rutina2));

        verify(repository).listarTodas();
    }

    @Test
    void debeRetornarListaVaciaCuandoNoHayRutinas() {
        RutinaCuidadoRepository repository =
                mock(RutinaCuidadoRepository.class);

        when(repository.listarTodas()).thenReturn(List.of());

        ListarRutinasUseCase useCase =
                new ListarRutinasUseCase(repository);

        List<RutinaCuidado> resultado = useCase.ejecutar();

        assertTrue(resultado.isEmpty());

        verify(repository).listarTodas();
    }
}
