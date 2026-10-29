package com.example.ecommerce.application.usecase;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.domain.entity.Marca;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.AccionSoloParaAdministradorException;
import com.example.ecommerce.domain.exception.MarcaDuplicadaException;
import com.example.ecommerce.domain.service.VerificadorDeMarcaUnica;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.infrastructure.persistence.MarcaRepositoryEnMemoria;
import com.example.ecommerce.infrastructure.persistence.UsuarioRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CrearMarcaUseCaseTest {

    private final MarcaRepositoryEnMemoria marcas = new MarcaRepositoryEnMemoria();
    private final UsuarioRepositoryEnMemoria usuarios = new UsuarioRepositoryEnMemoria();
    private final CrearMarcaUseCase useCase =
            new CrearMarcaUseCase(marcas, usuarios, new VerificadorDeMarcaUnica(marcas));

    @BeforeEach
    void prepararUsuarios() {
        usuarios.guardar(Usuario.administradorInicial("ad-1", "Admin", new Email("admin@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA));
        usuarios.guardar(Usuario.registrarVendedor("v-1", "Vera", new Email("vera@tienda.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA));
    }

    @Test
    void elAdministradorCreaLaMarca() {
        Marca marca = useCase.ejecutar("m-1", "ad-1", "  Maybelline ");

        assertEquals("Maybelline", marca.getNombre());
        assertTrue(marcas.obtenerPorId("m-1").isPresent());
    }

    @Test
    void unVendedorNoPuedeCrearMarcas() {
        assertThrows(AccionSoloParaAdministradorException.class, () -> useCase.ejecutar("m-1", "v-1", "Maybelline"));
        assertTrue(marcas.obtenerPorId("m-1").isEmpty());
    }

    @Test
    void unUsuarioQueNoExisteNoPuedeCrearMarcas() {
        assertThrows(AccionSoloParaAdministradorException.class, () -> useCase.ejecutar("m-1", "no-existe", "Maybelline"));
    }

    @Test
    void noSePuedeRepetirElNombreSinImportarMayusculas() {
        useCase.ejecutar("m-1", "ad-1", "Maybelline");

        assertThrows(MarcaDuplicadaException.class, () -> useCase.ejecutar("m-2", "ad-1", "MAYBELLINE"));
    }
}
