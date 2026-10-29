package com.example.ecommerce.domain.entity;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.domain.exception.AccionSoloParaAdministradorException;
import com.example.ecommerce.domain.exception.AccionSoloParaVendedorException;
import com.example.ecommerce.domain.exception.DescripcionTiendaMuyLargaException;
import com.example.ecommerce.domain.exception.NombreTiendaRequeridoException;
import com.example.ecommerce.domain.exception.TiendaAjenaException;
import com.example.ecommerce.domain.exception.TiendaNoActivaException;
import com.example.ecommerce.domain.exception.TiendaNoPendienteException;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.domain.valueobject.EstadoTienda;
import com.example.ecommerce.domain.valueobject.RolUsuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TiendaTest {

    private final Usuario vera = Usuario.registrarVendedor("v-1", "Vera",
            new Email("vera@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);
    private final Usuario admin = Usuario.administradorInicial("ad-1", "Admin",
            new Email("admin@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);
    private final Usuario ana = Usuario.registrarCliente("u-1", "Ana",
            new Email("ana@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);

    @Test
    void unVendedorRegistraSuTiendaYQuedaPendiente() {
        Tienda tienda = Tienda.registrar("t-1", "  Glow Natural ", " Cosmética vegana ", vera);

        assertEquals("Glow Natural", tienda.getNombre());
        assertEquals("Cosmética vegana", tienda.getDescripcion());
        assertEquals("v-1", tienda.getVendedorId());
        assertEquals(EstadoTienda.PENDIENTE, tienda.getEstado());
    }

    @Test
    void laDescripcionEsOpcional() {
        Tienda tienda = Tienda.registrar("t-1", "Glow Natural", null, vera);

        assertEquals("", tienda.getDescripcion());
    }

    @Test
    void soloUnVendedorPuedeRegistrarUnaTienda() {
        assertThrows(AccionSoloParaVendedorException.class,
                () -> Tienda.registrar("t-1", "Glow Natural", "", ana));
    }

    @Test
    void unVendedorDesactivadoNoPuedeRegistrarUnaTienda() {
        Usuario inactivo = Usuario.reconstruir("v-2", "Vale", new Email("vale@correo.com"), "hash",
                DatosPrueba.TELEFONO, DatosPrueba.FECHA, RolUsuario.VENDEDOR, false);

        assertThrows(AccionSoloParaVendedorException.class,
                () -> Tienda.registrar("t-1", "Glow Natural", "", inactivo));
    }

    @Test
    void elNombreEsObligatorio() {
        assertThrows(NombreTiendaRequeridoException.class,
                () -> Tienda.registrar("t-1", "   ", "", vera));
    }

    @Test
    void laDescripcionNoPuedeSuperar500Caracteres() {
        assertThrows(DescripcionTiendaMuyLargaException.class,
                () -> Tienda.registrar("t-1", "Glow Natural", "x".repeat(501), vera));
    }

    @Test
    void unAdministradorApruebaUnaTiendaPendiente() {
        Tienda tienda = Tienda.registrar("t-1", "Glow Natural", "", vera);

        tienda.aprobarPor(admin);

        assertEquals(EstadoTienda.ACTIVA, tienda.getEstado());
    }

    @Test
    void soloUnAdministradorPuedeAprobar() {
        Tienda tienda = Tienda.registrar("t-1", "Glow Natural", "", vera);

        assertThrows(AccionSoloParaAdministradorException.class, () -> tienda.aprobarPor(vera));
        assertEquals(EstadoTienda.PENDIENTE, tienda.getEstado());
    }

    @Test
    void noSePuedeAprobarUnaTiendaQueYaEstaActiva() {
        Tienda tienda = Tienda.reconstruir("t-1", "Glow Natural", "", "v-1", EstadoTienda.ACTIVA);

        assertThrows(TiendaNoPendienteException.class, () -> tienda.aprobarPor(admin));
    }

    @Test
    void unaTiendaActivaPermiteQueSuDuenoPublique() {
        Tienda tienda = Tienda.reconstruir("t-1", "Glow Natural", "", "v-1", EstadoTienda.ACTIVA);

        assertDoesNotThrow(() -> tienda.verificarQuePuedePublicar("v-1"));
    }

    @Test
    void unaTiendaPendienteNoPuedePublicar() {
        Tienda tienda = Tienda.registrar("t-1", "Glow Natural", "", vera);

        assertThrows(TiendaNoActivaException.class, () -> tienda.verificarQuePuedePublicar("v-1"));
    }

    @Test
    void otroVendedorNoPuedePublicarEnUnaTiendaAjena() {
        Tienda tienda = Tienda.reconstruir("t-1", "Glow Natural", "", "v-1", EstadoTienda.ACTIVA);

        assertThrows(TiendaAjenaException.class, () -> tienda.verificarQuePuedePublicar("v-2"));
    }

    @Test
    void dosTiendasSonIgualesSiTienenElMismoId() {
        Tienda a = Tienda.reconstruir("t-1", "Glow", "", "v-1", EstadoTienda.ACTIVA);
        Tienda b = Tienda.reconstruir("t-1", "Otro nombre", "", "v-9", EstadoTienda.PENDIENTE);

        assertEquals(a, b);
    }

    @Test
    void soloElVendedorDuenoPasaLaVerificacionDeLaTienda() {
        Tienda tienda = Tienda.reconstruir("t-1", "Glow", "", "v-1", EstadoTienda.PENDIENTE);

        assertDoesNotThrow(() -> tienda.verificarQueEsDe("v-1"));
        assertThrows(TiendaAjenaException.class, () -> tienda.verificarQueEsDe("v-2"));
    }
}
