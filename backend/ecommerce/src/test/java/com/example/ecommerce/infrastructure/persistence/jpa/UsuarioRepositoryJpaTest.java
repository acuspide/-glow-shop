package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.domain.valueobject.RolUsuario;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.UsuarioPersistenceMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Prueba el adaptador contra una base H2 real (no contra un mock). */
@DataJpaTest
@Import({UsuarioRepositoryJpa.class, UsuarioPersistenceMapper.class})
class UsuarioRepositoryJpaTest {

    @Autowired
    private UsuarioRepositoryJpa repositorio;

    private Usuario ana() {
        return Usuario.registrarCliente("u-1", "Ana", new Email("Ana@Correo.com"), "hash",
                DatosPrueba.TELEFONO, DatosPrueba.FECHA);
    }

    @Test
    void guardaYReconstruyeElUsuarioConTodosSusDatos() {
        repositorio.guardar(ana());

        Usuario leido = repositorio.obtenerPorId("u-1").orElseThrow();

        assertEquals("Ana", leido.getNombre());
        assertEquals("ana@correo.com", leido.getEmail().getValor());
        assertEquals("hash", leido.getContrasenaHash());
        assertEquals(DatosPrueba.TELEFONO, leido.getTelefono());
        assertEquals(DatosPrueba.FECHA, leido.getFechaNacimiento());
        assertEquals(RolUsuario.CLIENTE, leido.getRol());
        assertTrue(leido.isActivo());
    }

    @Test
    void buscaPorEmailSinImportarMayusculas() {
        repositorio.guardar(ana());

        assertTrue(repositorio.existePorEmail(new Email("ANA@correo.com")));
        assertEquals("u-1", repositorio.buscarPorEmail(new Email("ana@CORREO.com")).orElseThrow().getId());
        assertFalse(repositorio.existePorEmail(new Email("otra@correo.com")));
    }

    @Test
    void guardarDeNuevoActualizaElUsuario() {
        Usuario ana = ana();
        repositorio.guardar(ana);

        ana.desactivar();
        repositorio.guardar(ana);

        assertFalse(repositorio.obtenerPorId("u-1").orElseThrow().isActivo());
        assertEquals(1, repositorio.listarTodos().size());
    }
}
