package com.example.ecommerce.application.usecase;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.domain.entity.Categoria;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.AccionSoloParaAdministradorException;
import com.example.ecommerce.domain.exception.CategoriaDuplicadaException;
import com.example.ecommerce.domain.service.VerificadorDeCategoriaUnica;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.infrastructure.persistence.CategoriaRepositoryEnMemoria;
import com.example.ecommerce.infrastructure.persistence.UsuarioRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CrearCategoriaUseCaseTest {

    private final CategoriaRepositoryEnMemoria categorias = new CategoriaRepositoryEnMemoria();
    private final UsuarioRepositoryEnMemoria usuarios = new UsuarioRepositoryEnMemoria();
    private final CrearCategoriaUseCase useCase =
            new CrearCategoriaUseCase(categorias, usuarios, new VerificadorDeCategoriaUnica(categorias));

    @BeforeEach
    void prepararUsuarios() {
        usuarios.guardar(Usuario.administradorInicial("ad-1", "Admin", new Email("admin@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA));
        usuarios.guardar(Usuario.registrarVendedor("v-1", "Vera", new Email("vera@tienda.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA));
    }

    @Test
    void elAdministradorCreaLaCategoria() {
        Categoria categoria = useCase.ejecutar("cat-1", "ad-1", "  Labios ");

        assertEquals("Labios", categoria.getNombre());
        assertTrue(categorias.obtenerPorId("cat-1").isPresent());
    }

    @Test
    void unVendedorNoPuedeCrearCategorias() {
        assertThrows(AccionSoloParaAdministradorException.class, () -> useCase.ejecutar("cat-1", "v-1", "Labios"));
        assertTrue(categorias.obtenerPorId("cat-1").isEmpty());
    }

    @Test
    void unUsuarioQueNoExisteNoPuedeCrearCategorias() {
        assertThrows(AccionSoloParaAdministradorException.class, () -> useCase.ejecutar("cat-1", "no-existe", "Labios"));
    }

    @Test
    void noSePuedeRepetirElNombreSinImportarMayusculas() {
        useCase.ejecutar("cat-1", "ad-1", "Labios");

        assertThrows(CategoriaDuplicadaException.class, () -> useCase.ejecutar("cat-2", "ad-1", "labios"));
    }
}
