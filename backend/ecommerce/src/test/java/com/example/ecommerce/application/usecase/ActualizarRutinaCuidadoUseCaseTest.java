package com.example.ecommerce.application.usecase;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.AccionSoloParaAdministradorException;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.infrastructure.persistence.UsuarioRepositoryEnMemoria;
import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
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
    private static UsuarioRepositoryEnMemoria usuariosConAdminYVendedora() {
        UsuarioRepositoryEnMemoria usuarios = new UsuarioRepositoryEnMemoria();
        usuarios.guardar(Usuario.administradorInicial("ad-1", "Admin", new Email("admin@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA));
        usuarios.guardar(Usuario.registrarVendedor("v-1", "Vera", new Email("vera@tienda.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA));
        return usuarios;
    }

    private RutinaCuidadoRepository rutinaRepository;
    private ArticuloRepository articuloRepository;
    private ActualizarRutinaCuidadoUseCase useCase;

    @BeforeEach
    void setUp() {
        rutinaRepository = mock(RutinaCuidadoRepository.class);
        articuloRepository = mock(ArticuloRepository.class);

        useCase = new ActualizarRutinaCuidadoUseCase(
                rutinaRepository,
                articuloRepository,
                usuariosConAdminYVendedora()
        );
    }

    @Test
    void debeActualizarUnaRutinaExistente() {
        String rutinaId = "r-1";

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
                "ad-1",
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
        when(rutinaRepository.obtenerPorId("r-99"))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> useCase.ejecutar(
                        "r-99",
                        "ad-1",
                        "Rutina actualizada",
                        List.of("a-10", "a-20")
                )
        );

        verify(rutinaRepository, never()).guardar(any());
        verifyNoInteractions(articuloRepository);
    }

    @Test
    void unVendedorNoPuedeEditarRutinas() {
        assertThrows(AccionSoloParaAdministradorException.class,
                () -> useCase.ejecutar("r-1", "v-1", "Rutina", List.of("a-10", "a-20")));

        verifyNoInteractions(rutinaRepository, articuloRepository);
    }

}
