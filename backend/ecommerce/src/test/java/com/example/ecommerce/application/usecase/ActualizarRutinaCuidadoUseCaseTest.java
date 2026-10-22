package com.example.ecommerce.application.usecase;
import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.RutinaCuidado;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.RutinaCuidadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ActualizarRutinaCuidadoUseCaseTest {
    private RutinaCuidadoRepository rutinaRepository;
    private ArticuloRepository articuloRepository;
    private ActualizarRutinaCuidadoUseCase useCase;

    @BeforeEach
    void setUp() {
        rutinaRepository = mock(RutinaCuidadoRepository.class);
        articuloRepository = mock(ArticuloRepository.class);

        useCase = new ActualizarRutinaCuidadoUseCase(
                rutinaRepository,
                articuloRepository
        );
    }

    @Test
    void debeActualizarUnaRutinaExistente() {
        long rutinaId = 1L;

        Articulo articulo1 = mock(Articulo.class);
        Articulo articulo2 = mock(Articulo.class);

        when(rutinaRepository.obtenerPorId(rutinaId))
                .thenReturn(Optional.of(mock(RutinaCuidado.class)));

        when(articuloRepository.obtenerPorId("a-10"))
                .thenReturn(Optional.of(articulo1));

        when(articuloRepository.obtenerPorId("a-20"))
                .thenReturn(Optional.of(articulo2));

        when(articulo1.isPublicado()).thenReturn(true);
        when(articulo1.getCantidadDisponible()).thenReturn(5);

        when(articulo2.isPublicado()).thenReturn(true);
        when(articulo2.getCantidadDisponible()).thenReturn(8);

        RutinaCuidado resultado = useCase.ejecutar(
                rutinaId,
                "Rutina actualizada",
                List.of("a-10", "a-20")
        );

        assertEquals(rutinaId, resultado.getId());
        assertEquals("Rutina actualizada", resultado.getNombre());
        assertEquals(2, resultado.getArticulos().size());

        verify(rutinaRepository).guardar(resultado);
    }

    @Test
    void debeLanzarExcepcionCuandoLaRutinaNoExiste() {
        when(rutinaRepository.obtenerPorId(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                java.util.NoSuchElementException.class,
                () -> useCase.ejecutar(
                        99L,
                        "Rutina actualizada",
                        List.of("a-10", "a-20")
                )
        );

        verify(rutinaRepository, never()).guardar(any());
        verifyNoInteractions(articuloRepository);
    }
}
