package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.Categoria;
import com.example.ecommerce.domain.entity.Inventario;
import com.example.ecommerce.domain.entity.Marca;
import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.domain.valueobject.EstadoTienda;
import com.example.ecommerce.domain.valueobject.FechaVencimiento;
import com.example.ecommerce.domain.valueobject.NombreArticulo;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.domain.valueobject.TipoPiel;
import com.example.ecommerce.domain.valueobject.Tono;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.ArticuloPersistenceMapper;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.CategoriaPersistenceMapper;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.MarcaPersistenceMapper;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.TiendaPersistenceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import({ArticuloRepositoryJpa.class, ArticuloPersistenceMapper.class,
        CategoriaRepositoryJpa.class, CategoriaPersistenceMapper.class,
        MarcaRepositoryJpa.class, MarcaPersistenceMapper.class,
        TiendaRepositoryJpa.class, TiendaPersistenceMapper.class})
class ArticuloRepositoryJpaTest {

    private static final LocalDate VENCE = LocalDate.of(2030, 12, 31);

    @Autowired
    private ArticuloRepositoryJpa articulos;
    @Autowired
    private CategoriaRepositoryJpa categorias;
    @Autowired
    private MarcaRepositoryJpa marcas;
    @Autowired
    private TiendaRepositoryJpa tiendas;

    private Categoria labios;
    private Marca maybelline;
    private Tienda glow;

    @BeforeEach
    void prepararCatalogo() {
        labios = Categoria.crear("cat-1", "Labios");
        maybelline = Marca.crear("m-1", "Maybelline");
        glow = Tienda.reconstruir("t-1", "Glow Natural", "", "v-1", EstadoTienda.ACTIVA);
        categorias.guardar(labios);
        marcas.guardar(maybelline);
        tiendas.guardar(glow);
    }

    private Articulo labial() {
        Articulo articulo = Articulo.crear("a-1", new NombreArticulo("Labial mate"),
                new Precio(new BigDecimal("35000.00")), labios, maybelline, Tono.MEDIO,
                List.of(TipoPiel.SECA, TipoPiel.MIXTA), Inventario.crear(10),
                new FechaVencimiento(VENCE), glow);
        articulo.publicar();
        return articulo;
    }

    @Test
    void guardaYReconstruyeElArticuloConSusRelaciones() {
        articulos.guardar(labial());

        Articulo leido = articulos.obtenerPorId("a-1").orElseThrow();

        assertEquals("Labial mate", leido.getNombre().valor());
        assertEquals(0, new BigDecimal("35000").compareTo(leido.getPrecio().valor()));
        assertEquals("Labios", leido.getCategoria().getNombre());
        assertEquals("Maybelline", leido.getMarca().getNombre());
        assertEquals("Glow Natural", leido.getTienda().getNombre());
        assertEquals(EstadoTienda.ACTIVA, leido.getTienda().getEstado());
        assertEquals(Tono.MEDIO, leido.getTono());
        assertEquals(List.of(TipoPiel.SECA, TipoPiel.MIXTA), leido.getTiposPiel());
        assertEquals(10, leido.getCantidadDisponible());
        assertEquals(VENCE, leido.getFechaVencimiento().fecha());
        assertTrue(leido.isPublicado());
        assertFalse(leido.isEliminado());
    }

    @Test
    void guardarDeNuevoActualizaInventarioYBajaLogica() {
        Articulo articulo = labial();
        articulos.guardar(articulo);

        articulo.disminuirInventario(3);
        articulo.eliminar();
        articulos.guardar(articulo);

        Articulo leido = articulos.obtenerPorId("a-1").orElseThrow();
        assertEquals(7, leido.getCantidadDisponible());
        assertTrue(leido.isEliminado());
        assertEquals(1, articulos.listarTodos().size());
    }
}
