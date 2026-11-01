package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.repository.ArticuloVentas;
import com.example.ecommerce.domain.repository.Pagina;
import com.example.ecommerce.domain.valueobject.CriteriosBusquedaArticulo;
import com.example.ecommerce.domain.valueobject.TipoPiel;
import com.example.ecommerce.domain.valueobject.Tono;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.ArticuloPersistenceMapper;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.CategoriaPersistenceMapper;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.MarcaPersistenceMapper;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.PedidoPersistenceMapper;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.TiendaPersistenceMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba las consultas JPQL contra H2 con los datos de data.sql: el filtrado, el conteo, la paginación
 * y el ranking los hace la base de datos.
 *
 * Datos de data.sql: 10 artículos, de los cuales art-gloss-2023 está eliminado y sin publicar.
 */
@DataJpaTest
@Import({ArticuloRepositoryJpa.class, ArticuloPersistenceMapper.class,
        CategoriaPersistenceMapper.class, MarcaPersistenceMapper.class, TiendaPersistenceMapper.class,
        PedidoRepositoryJpa.class, PedidoPersistenceMapper.class})
class BusquedaYRankingRepositoryJpaTest {

    @Autowired
    private ArticuloRepositoryJpa articulos;

    @Autowired
    private PedidoRepositoryJpa pedidos;

    private CriteriosBusquedaArticulo sinFiltros() {
        return new CriteriosBusquedaArticulo(null, null, null, null, null, null, null, null);
    }

    private CriteriosBusquedaArticulo conTexto(String texto) {
        return new CriteriosBusquedaArticulo(texto, null, null, null, null, null, null, null);
    }

    // ---------- Listado paginado ----------

    @Test
    void noMuestraLosArticulosEliminados() {
        Pagina<Articulo> pagina = articulos.buscar(sinFiltros(), 0, 50);

        assertEquals(9, pagina.totalElementos());
        assertFalse(pagina.contenido().stream().anyMatch(a -> a.getId().equals("art-gloss-2023")));
    }

    @Test
    void paginaYDevuelveTotalDeElementosYDePaginas() {
        Pagina<Articulo> primera = articulos.buscar(sinFiltros(), 0, 4);
        Pagina<Articulo> ultima = articulos.buscar(sinFiltros(), 2, 4);

        assertEquals(9, primera.totalElementos());
        assertEquals(3, primera.totalPaginas());
        assertEquals(4, primera.contenido().size());
        assertEquals(1, ultima.contenido().size());
        assertEquals(2, ultima.numero());
        assertEquals(4, ultima.tamano());
    }

    @Test
    void ordenaPorNombre() {
        List<String> nombres = articulos.buscar(sinFiltros(), 0, 50).contenido().stream()
                .map(a -> a.getNombre().valor())
                .toList();

        assertEquals(nombres.stream().sorted().toList(), nombres);
    }

    @Test
    void buscaPorTextoSinImportarMayusculasNiEspaciosDeLosBordes() {
        Pagina<Articulo> pagina = articulos.buscar(conTexto("  LABIAL "), 0, 10);

        assertEquals(1, pagina.totalElementos());
        assertEquals("art-labial-mate", pagina.contenido().get(0).getId());
    }

    @Test
    void elTextoNoEncuentraArticulosEliminados() {
        assertEquals(0, articulos.buscar(conTexto("brillo"), 0, 10).totalElementos());
    }

    @Test
    void filtraPorCategoria() {
        CriteriosBusquedaArticulo criterios = new CriteriosBusquedaArticulo(
                null, "cat-cuidado-facial", null, null, null, null, null, null);

        Pagina<Articulo> pagina = articulos.buscar(criterios, 0, 10);

        assertEquals(4, pagina.totalElementos());
        assertTrue(pagina.contenido().stream()
                .allMatch(a -> a.getCategoria().getId().equals("cat-cuidado-facial")));
    }

    @Test
    void filtraPorRangoDePrecioIncluyendoLosLimites() {
        CriteriosBusquedaArticulo criterios = new CriteriosBusquedaArticulo(
                null, null, null, null, new BigDecimal("35000"), new BigDecimal("52000"), null, null);

        List<String> ids = articulos.buscar(criterios, 0, 10).contenido().stream()
                .map(Articulo::getId)
                .sorted()
                .toList();

        assertEquals(List.of("art-base-fit", "art-labial-mate", "art-limpiador", "art-mascara"), ids);
    }

    @Test
    void filtraPorTonoYTipoDePiel() {
        CriteriosBusquedaArticulo criterios = new CriteriosBusquedaArticulo(
                null, null, null, null, null, null, Tono.CLARO, TipoPiel.SENSIBLE);

        List<String> ids = articulos.buscar(criterios, 0, 10).contenido().stream()
                .map(Articulo::getId)
                .sorted()
                .toList();

        assertEquals(List.of("art-hidratante", "art-protector"), ids);
    }

    @Test
    void combinaTextoCategoriaYMarca() {
        CriteriosBusquedaArticulo criterios = new CriteriosBusquedaArticulo(
                "crema", "cat-cuidado-facial", "marca-cetaphil", "tienda-lina-beauty",
                null, null, null, null);

        Pagina<Articulo> pagina = articulos.buscar(criterios, 0, 10);

        assertEquals(1, pagina.totalElementos());
        assertEquals("art-hidratante", pagina.contenido().get(0).getId());
    }

    @Test
    void devuelvePaginaVaciaCuandoNadaCoincide() {
        Pagina<Articulo> pagina = articulos.buscar(conTexto("no existe"), 0, 10);

        assertEquals(0, pagina.totalElementos());
        assertEquals(0, pagina.totalPaginas());
        assertTrue(pagina.contenido().isEmpty());
    }

    // ---------- Ranking con JOIN, GROUP BY y SUM ----------

    @Test
    void rankingSumaLasUnidadesEntregadasPorArticulo() {
        List<ArticuloVentas> ranking = pedidos.masVendidos(10);

        // data.sql: unidades entregadas = labial 3, protector 2, base 1, sérum 1, hidratante 1.
        // Los empates se ordenan por nombre.
        assertEquals(List.of("art-labial-mate", "art-protector", "art-base-fit", "art-hidratante", "art-serum-vitc"),
                ranking.stream().map(ArticuloVentas::articuloId).toList());
        assertEquals(List.of(3L, 2L, 1L, 1L, 1L),
                ranking.stream().map(ArticuloVentas::unidadesVendidas).toList());
        assertEquals("Labial mate Ruby", ranking.get(0).nombre());
    }

    @Test
    void rankingRespetaElLimite() {
        List<ArticuloVentas> ranking = pedidos.masVendidos(2);

        assertEquals(2, ranking.size());
        assertEquals("art-labial-mate", ranking.get(0).articuloId());
        assertEquals("art-protector", ranking.get(1).articuloId());
    }

    @Test
    void rankingNoIncluyePedidosQueNoEstanEntregados() {
        // ped-004 (ENVIADO) lleva el limpiador y ped-005 (CONFIRMADO) otro labial: no cuentan.
        List<String> ids = pedidos.masVendidos(10).stream().map(ArticuloVentas::articuloId).toList();

        assertFalse(ids.contains("art-limpiador"));
        assertEquals(3L, pedidos.masVendidos(1).get(0).unidadesVendidas());
    }
}
