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

public class AgregarArticuloARutinaUseCaseTest {
    private static UsuarioRepositoryEnMemoria usuariosConAdminYVendedora() {
        UsuarioRepositoryEnMemoria usuarios = new UsuarioRepositoryEnMemoria();
        usuarios.guardar(Usuario.administradorInicial("ad-1", "Admin", new Email("admin@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA));
        usuarios.guardar(Usuario.registrarVendedor("v-1", "Vera", new Email("vera@tienda.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA));
        return usuarios;
    }

    private RutinaCuidadoRepository rutinaRepository;
    private ArticuloRepository articuloRepository;
    private AgregarArticuloARutinaUseCase useCase;

    @BeforeEach
    void setUp() {
        rutinaRepository = mock(RutinaCuidadoRepository.class);
        articuloRepository = mock(ArticuloRepository.class);

        useCase = new AgregarArticuloARutinaUseCase(
                rutinaRepository,
                articuloRepository,
                usuariosConAdminYVendedora()
        );
    }

    @Test
    void debeAgregarArticuloAUnaRutinaExistente() {
        Articulo articulo1 = articuloDisponible();
        Articulo articulo2 = articuloDisponible();
        Articulo articuloNuevo = articuloDisponible();

        RutinaCuidado rutina = RutinaCuidado.crear(
                "r-1", "Rutina facial", List.of(articulo1, articulo2)
        );

        when(rutinaRepository.obtenerPorId("r-1"))
                .thenReturn(Optional.of(rutina));
        when(articuloRepository.obtenerPorId("a-3"))
                .thenReturn(Optional.of(articuloNuevo));

        RutinaCuidado resultado = useCase.ejecutar("r-1", "ad-1", "a-3");

        assertEquals("r-1", resultado.getId());
        assertEquals("Rutina facial", resultado.getNombre());
        assertEquals(3, resultado.getArticulos().size());
        assertTrue(resultado.getArticulos().contains(articuloNuevo));

        verify(rutinaRepository).guardar(resultado);
    }

    @Test
    void debeLanzarExcepcionSiLaRutinaNoExiste() {
        when(rutinaRepository.obtenerPorId("r-1"))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> useCase.ejecutar("r-1", "ad-1", "a-3")
        );

        verifyNoInteractions(articuloRepository);
        verify(rutinaRepository, never()).guardar(any());
    }

    @Test
    void debeLanzarExcepcionSiElArticuloNoExiste() {
        RutinaCuidado rutina = RutinaCuidado.crear(
                "r-1",
                "Rutina facial",
                List.of(articuloDisponible(), articuloDisponible())
        );

        when(rutinaRepository.obtenerPorId("r-1"))
                .thenReturn(Optional.of(rutina));
        when(articuloRepository.obtenerPorId("a-3"))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> useCase.ejecutar("r-1", "ad-1", "a-3")
        );

        verify(rutinaRepository, never()).guardar(any());
    }

    private Articulo articuloDisponible() {
        Articulo articulo = mock(Articulo.class);

        when(articulo.isPublicado()).thenReturn(true);
        when(articulo.getCantidadDisponible()).thenReturn(10);

        return articulo;
    }

    @Test
    void unVendedorNoPuedeAgregarArticulosALaRutina() {
        assertThrows(AccionSoloParaAdministradorException.class,
                () -> useCase.ejecutar("r-1", "v-1", "a-3"));

        verifyNoInteractions(rutinaRepository, articuloRepository);
    }

}
