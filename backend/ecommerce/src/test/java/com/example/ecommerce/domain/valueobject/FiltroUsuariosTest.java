package com.example.ecommerce.domain.valueobject;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.domain.entity.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FiltroUsuariosTest {

    private final Usuario clienteActivo = Usuario.registrarCliente("u-1", "Ana", new Email("ana@correo.com"), "hash",
            DatosPrueba.TELEFONO, DatosPrueba.FECHA);
    private final Usuario vendedorInactivo = Usuario.reconstruir("u-2", "Vera", new Email("vera@tienda.com"), "hash",
            DatosPrueba.TELEFONO, DatosPrueba.FECHA, RolUsuario.VENDEDOR, false);

    @Test
    void sinCriteriosAdmiteATodos() {
        FiltroUsuarios filtro = new FiltroUsuarios(null, null);

        assertTrue(filtro.admite(clienteActivo));
        assertTrue(filtro.admite(vendedorInactivo));
    }

    @Test
    void filtraPorRol() {
        FiltroUsuarios filtro = new FiltroUsuarios(RolUsuario.VENDEDOR, null);

        assertFalse(filtro.admite(clienteActivo));
        assertTrue(filtro.admite(vendedorInactivo));
    }

    @Test
    void filtraPorEstado() {
        FiltroUsuarios soloActivos = new FiltroUsuarios(null, true);

        assertTrue(soloActivos.admite(clienteActivo));
        assertFalse(soloActivos.admite(vendedorInactivo));
    }

    @Test
    void combinaRolYEstado() {
        FiltroUsuarios vendedoresActivos = new FiltroUsuarios(RolUsuario.VENDEDOR, true);

        assertFalse(vendedoresActivos.admite(clienteActivo));
        assertFalse(vendedoresActivos.admite(vendedorInactivo));
    }
}
