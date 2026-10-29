package com.example.ecommerce.application.usecase;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.AccionSoloParaVendedorException;
import com.example.ecommerce.domain.exception.VendedorYaTieneTiendaException;
import com.example.ecommerce.domain.repository.TiendaRepository;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.service.VerificadorDeTiendaUnicaPorVendedor;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.domain.valueobject.EstadoTienda;
import com.example.ecommerce.infrastructure.persistence.TiendaRepositoryEnMemoria;
import com.example.ecommerce.infrastructure.persistence.UsuarioRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrarTiendaUseCaseTest {

    private final UsuarioRepository usuarios = new UsuarioRepositoryEnMemoria();
    private final TiendaRepository tiendas = new TiendaRepositoryEnMemoria();
    private final RegistrarTiendaUseCase useCase = new RegistrarTiendaUseCase(
            usuarios, tiendas, new VerificadorDeTiendaUnicaPorVendedor(tiendas));

    @BeforeEach
    void prepararUsuarios() {
        usuarios.guardar(Usuario.registrarVendedor("v-1", "Vera", new Email("vera@tienda.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA));
        usuarios.guardar(Usuario.registrarCliente("u-1", "Ana", new Email("ana@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA));
    }

    @Test
    void elVendedorRegistraSuTiendaPendienteYQuedaGuardada() {
        Tienda tienda = useCase.ejecutar("t-1", "v-1", "Glow Natural", "Cosmética vegana");

        assertEquals(EstadoTienda.PENDIENTE, tienda.getEstado());
        assertEquals("v-1", tienda.getVendedorId());
        assertSame(tienda, tiendas.obtenerPorId("t-1").orElseThrow());
    }

    @Test
    void unVendedorNoPuedeRegistrarUnaSegundaTienda() {
        useCase.ejecutar("t-1", "v-1", "Glow Natural", "");

        assertThrows(VendedorYaTieneTiendaException.class,
                () -> useCase.ejecutar("t-2", "v-1", "Otra tienda", ""));
        assertTrue(tiendas.obtenerPorId("t-2").isEmpty());
    }

    @Test
    void unClienteNoPuedeRegistrarUnaTienda() {
        assertThrows(AccionSoloParaVendedorException.class,
                () -> useCase.ejecutar("t-1", "u-1", "Glow Natural", ""));
        assertTrue(tiendas.obtenerPorId("t-1").isEmpty());
    }

    @Test
    void unUsuarioQueNoExisteNoPuedeRegistrarUnaTienda() {
        assertThrows(AccionSoloParaVendedorException.class,
                () -> useCase.ejecutar("t-1", "no-existe", "Glow Natural", ""));
    }
}
