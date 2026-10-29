package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.Categoria;
import com.example.ecommerce.domain.entity.Inventario;
import com.example.ecommerce.domain.entity.Marca;
import com.example.ecommerce.domain.entity.RutinaCuidado;
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
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.RutinaCuidadoPersistenceMapper;
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

@DataJpaTest
@Import({RutinaCuidadoRepositoryJpa.class, RutinaCuidadoPersistenceMapper.class,
        ArticuloRepositoryJpa.class, ArticuloPersistenceMapper.class,
        CategoriaRepositoryJpa.class, CategoriaPersistenceMapper.class,
        MarcaRepositoryJpa.class, MarcaPersistenceMapper.class,
        TiendaRepositoryJpa.class, TiendaPersistenceMapper.class})
class RutinaCuidadoRepositoryJpaTest {

    @Autowired
    private RutinaCuidadoRepositoryJpa rutinas;
    @Autowired
    private ArticuloRepositoryJpa articulos;
    @Autowired
    private CategoriaRepositoryJpa categorias;
    @Autowired
    private MarcaRepositoryJpa marcas;
    @Autowired
    private TiendaRepositoryJpa tiendas;

    private Articulo limpiador;
    private Articulo serum;
    private Articulo crema;

    private Articulo articulo(String id, String nombre, Categoria categoria, Marca marca, Tienda tienda) {
        Articulo articulo = Articulo.crear(id, new NombreArticulo(nombre), new Precio(new BigDecimal("30000")),
                categoria, marca, Tono.CLARO, List.of(TipoPiel.MIXTA), Inventario.crear(5),
                new FechaVencimiento(LocalDate.of(2030, 1, 1)), tienda);
        articulo.publicar();
        articulos.guardar(articulo);
        return articulo;
    }

    @BeforeEach
    void prepararArticulos() {
        Categoria rostro = Categoria.crear("cat-1", "Rostro");
        Marca marca = Marca.crear("m-1", "Cosrx");
        Tienda tienda = Tienda.reconstruir("t-1", "Glow", "", "v-1", EstadoTienda.ACTIVA);
        categorias.guardar(rostro);
        marcas.guardar(marca);
        tiendas.guardar(tienda);
        limpiador = articulo("a-1", "Limpiador", rostro, marca, tienda);
        serum = articulo("a-2", "Serum", rostro, marca, tienda);
        crema = articulo("a-3", "Crema", rostro, marca, tienda);
    }

    @Test
    void guardaLaRutinaConSusArticulosEnOrden() {
        rutinas.guardar(RutinaCuidado.crear("r-1", "Rutina de noche", List.of(limpiador, serum)));

        RutinaCuidado leida = rutinas.obtenerPorId("r-1").orElseThrow();

        assertEquals("Rutina de noche", leida.getNombre());
        assertEquals(List.of("a-1", "a-2"), leida.getArticulos().stream().map(Articulo::getId).toList());
        assertEquals("Serum", leida.getArticulos().get(1).getNombre().valor());
    }

    @Test
    void agregarUnArticuloActualizaLaRutinaGuardada() {
        RutinaCuidado rutina = RutinaCuidado.crear("r-1", "Rutina de noche", List.of(limpiador, serum));
        rutinas.guardar(rutina);

        rutinas.guardar(rutina.agregarArticulo(crema));

        assertEquals(3, rutinas.obtenerPorId("r-1").orElseThrow().getArticulos().size());
        assertEquals(1, rutinas.listarTodas().size());
    }
}
