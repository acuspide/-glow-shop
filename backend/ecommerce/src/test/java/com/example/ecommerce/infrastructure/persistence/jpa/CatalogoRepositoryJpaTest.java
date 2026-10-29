package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.domain.entity.Categoria;
import com.example.ecommerce.domain.entity.Marca;
import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.domain.valueobject.EstadoTienda;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.CategoriaPersistenceMapper;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.MarcaPersistenceMapper;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.TiendaPersistenceMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Categorías, marcas y tiendas contra una base H2 real. */
@DataJpaTest
@Import({CategoriaRepositoryJpa.class, CategoriaPersistenceMapper.class,
        MarcaRepositoryJpa.class, MarcaPersistenceMapper.class,
        TiendaRepositoryJpa.class, TiendaPersistenceMapper.class})
class CatalogoRepositoryJpaTest {

    @Autowired
    private CategoriaRepositoryJpa categorias;

    @Autowired
    private MarcaRepositoryJpa marcas;

    @Autowired
    private TiendaRepositoryJpa tiendas;

    @Test
    void guardaCategoriasYDetectaNombresRepetidosSinImportarMayusculas() {
        categorias.guardar(Categoria.crear("cat-1", "Labios"));

        assertEquals("Labios", categorias.obtenerPorId("cat-1").orElseThrow().getNombre());
        assertTrue(categorias.existePorNombre(" labios "));
        assertFalse(categorias.existePorNombre("Rostro"));
    }

    @Test
    void guardaMarcasYDetectaNombresRepetidosSinImportarMayusculas() {
        marcas.guardar(Marca.crear("m-1", "Maybelline"));

        assertEquals("Maybelline", marcas.obtenerPorId("m-1").orElseThrow().getNombre());
        assertTrue(marcas.existePorNombre("MAYBELLINE"));
        assertFalse(marcas.existePorNombre("Nivea"));
    }

    @Test
    void guardaLaTiendaYConservaSuEstadoAlAprobarla() {
        Tienda tienda = Tienda.reconstruir("t-1", "Glow Natural", "Vegana", "v-1", EstadoTienda.PENDIENTE);
        tiendas.guardar(tienda);

        tiendas.guardar(Tienda.reconstruir("t-1", "Glow Natural", "Vegana", "v-1", EstadoTienda.ACTIVA));

        Tienda leida = tiendas.obtenerPorId("t-1").orElseThrow();
        assertEquals(EstadoTienda.ACTIVA, leida.getEstado());
        assertEquals("v-1", leida.getVendedorId());
        assertEquals("Vegana", leida.getDescripcion());
        assertTrue(tiendas.existePorVendedorId("v-1"));
        assertFalse(tiendas.existePorVendedorId("v-2"));
    }
}
