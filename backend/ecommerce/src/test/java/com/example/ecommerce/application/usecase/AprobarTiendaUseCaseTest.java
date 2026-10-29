package com.example.ecommerce.application.usecase;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.AccionSoloParaAdministradorException;
import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.example.ecommerce.domain.exception.TiendaNoPendienteException;
import com.example.ecommerce.domain.repository.TiendaRepository;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.domain.valueobject.EstadoTienda;
import com.example.ecommerce.infrastructure.persistence.TiendaRepositoryEnMemoria;
import com.example.ecommerce.infrastructure.persistence.UsuarioRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AprobarTiendaUseCaseTest {

    private final UsuarioRepository usuarios = new UsuarioRepositoryEnMemoria();
    private final TiendaRepository tiendas = new TiendaRepositoryEnMemoria();
    private final AprobarTiendaUseCase useCase = new AprobarTiendaUseCase(usuarios, tiendas);

    @BeforeEach
    void prepararDatos() {
        usuarios.guardar(Usuario.administradorInicial("ad-1", "Admin", new Email("admin@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA));
        usuarios.guardar(Usuario.registrarVendedor("v-1", "Vera", new Email("vera@tienda.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA));
        tiendas.guardar(Tienda.reconstruir("t-1", "Glow Natural", "", "v-1", EstadoTienda.PENDIENTE));
    }

    @Test
    void elAdministradorApruebaLaTiendaYQuedaActivaEnElRepositorio() {
        Tienda tienda = useCase.ejecutar("t-1", "ad-1");

        assertEquals(EstadoTienda.ACTIVA, tienda.getEstado());
        assertEquals(EstadoTienda.ACTIVA, tiendas.obtenerPorId("t-1").orElseThrow().getEstado());
    }

    @Test
    void unVendedorNoPuedeAprobarTiendas() {
        assertThrows(AccionSoloParaAdministradorException.class, () -> useCase.ejecutar("t-1", "v-1"));
        assertEquals(EstadoTienda.PENDIENTE, tiendas.obtenerPorId("t-1").orElseThrow().getEstado());
    }

    @Test
    void unaTiendaQueNoExisteRetornaNoEncontrada() {
        assertThrows(RecursoNoEncontradoException.class, () -> useCase.ejecutar("t-9", "ad-1"));
    }

    @Test
    void noSePuedeAprobarDosVeces() {
        useCase.ejecutar("t-1", "ad-1");

        assertThrows(TiendaNoPendienteException.class, () -> useCase.ejecutar("t-1", "ad-1"));
    }

    @Test
    void unUsuarioQueNoExisteNoPuedeAprobar() {
        assertThrows(AccionSoloParaAdministradorException.class, () -> useCase.ejecutar("t-1", "no-existe"));
    }
}
