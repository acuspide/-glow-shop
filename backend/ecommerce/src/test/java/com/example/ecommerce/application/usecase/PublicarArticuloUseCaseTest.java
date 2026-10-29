package com.example.ecommerce.application.usecase;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.Categoria;
import com.example.ecommerce.domain.entity.Inventario;
import com.example.ecommerce.domain.entity.Marca;
import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.AccionSoloParaVendedorException;
import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.example.ecommerce.domain.exception.TiendaAjenaException;
import com.example.ecommerce.domain.exception.TiendaNoActivaException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.CategoriaRepository;
import com.example.ecommerce.domain.repository.MarcaRepository;
import com.example.ecommerce.domain.repository.TiendaRepository;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.domain.valueobject.EstadoTienda;
import com.example.ecommerce.domain.valueobject.FechaVencimiento;
import com.example.ecommerce.domain.valueobject.NombreArticulo;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.domain.valueobject.TipoPiel;
import com.example.ecommerce.domain.valueobject.Tono;
import com.example.ecommerce.infrastructure.persistence.ArticuloRepositoryEnMemoria;
import com.example.ecommerce.infrastructure.persistence.CategoriaRepositoryEnMemoria;
import com.example.ecommerce.infrastructure.persistence.MarcaRepositoryEnMemoria;
import com.example.ecommerce.infrastructure.persistence.TiendaRepositoryEnMemoria;
import com.example.ecommerce.infrastructure.persistence.UsuarioRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PublicarArticuloUseCaseTest {

    private final ArticuloRepository articulos = new ArticuloRepositoryEnMemoria();
    private final TiendaRepository tiendas = new TiendaRepositoryEnMemoria();
    private final CategoriaRepository categorias = new CategoriaRepositoryEnMemoria();
    private final MarcaRepository marcas = new MarcaRepositoryEnMemoria();
    private final UsuarioRepository usuarios = new UsuarioRepositoryEnMemoria();

    private final PublicarArticuloUseCase useCase =
            new PublicarArticuloUseCase(articulos, tiendas, categorias, marcas, usuarios);

    @BeforeEach
    void prepararDatos() {
        usuarios.guardar(Usuario.registrarVendedor("v-1", "Vera", new Email("vera@tienda.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA));
        usuarios.guardar(Usuario.registrarVendedor("v-2", "Vale", new Email("vale@tienda.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA));
        usuarios.guardar(Usuario.registrarCliente("u-1", "Ana", new Email("ana@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA));
        tiendas.guardar(Tienda.reconstruir("t-1", "Glow Natural", "", "v-1", EstadoTienda.ACTIVA));
        tiendas.guardar(Tienda.reconstruir("t-2", "Tienda nueva", "", "v-2", EstadoTienda.PENDIENTE));
        categorias.guardar(Categoria.crear("cat-1", "Labios"));
        marcas.guardar(Marca.crear("m-1", "Maybelline"));
    }

    private Articulo publicar(String vendedorId, String tiendaId, String categoriaId, String marcaId) {
        return useCase.ejecutar(
                "a-1", vendedorId,
                new NombreArticulo("Labial"),
                new Precio(new BigDecimal("20000")),
                categoriaId, marcaId,
                Tono.OSCURO,
                List.of(TipoPiel.NORMAL),
                Inventario.crear(10),
                new FechaVencimiento(LocalDate.now().plusYears(1)),
                tiendaId);
    }

    @Test
    void elVendedorPublicaEnSuTiendaActivaConCategoriaYMarcaRegistradas() {
        Articulo articulo = publicar("v-1", "t-1", "cat-1", "m-1");

        assertTrue(articulo.isPublicado());
        assertEquals("Labios", articulo.getCategoria().getNombre());
        assertEquals("Maybelline", articulo.getMarca().getNombre());
        assertEquals("Glow Natural", articulo.getTienda().getNombre());
        assertSame(articulo, articulos.obtenerPorId("a-1").orElseThrow());
    }

    @Test
    void noSePuedePublicarEnUnaTiendaPendiente() {
        assertThrows(TiendaNoActivaException.class, () -> publicar("v-2", "t-2", "cat-1", "m-1"));
        assertTrue(articulos.obtenerPorId("a-1").isEmpty());
    }

    @Test
    void noSePuedePublicarEnLaTiendaDeOtroVendedor() {
        assertThrows(TiendaAjenaException.class, () -> publicar("v-2", "t-1", "cat-1", "m-1"));
        assertTrue(articulos.obtenerPorId("a-1").isEmpty());
    }

    @Test
    void unClienteNoPuedePublicar() {
        assertThrows(AccionSoloParaVendedorException.class, () -> publicar("u-1", "t-1", "cat-1", "m-1"));
    }

    @Test
    void laTiendaLaCategoriaYLaMarcaDebenExistir() {
        RecursoNoEncontradoException sinTienda = assertThrows(RecursoNoEncontradoException.class,
                () -> publicar("v-1", "t-9", "cat-1", "m-1"));
        RecursoNoEncontradoException sinCategoria = assertThrows(RecursoNoEncontradoException.class,
                () -> publicar("v-1", "t-1", "cat-9", "m-1"));
        RecursoNoEncontradoException sinMarca = assertThrows(RecursoNoEncontradoException.class,
                () -> publicar("v-1", "t-1", "cat-1", "m-9"));

        assertEquals("No se encontró la tienda con id: t-9", sinTienda.getMessage());
        assertEquals("No se encontró la categoría con id: cat-9", sinCategoria.getMessage());
        assertEquals("No se encontró la marca con id: m-9", sinMarca.getMessage());
        assertTrue(articulos.obtenerPorId("a-1").isEmpty());
    }
}
