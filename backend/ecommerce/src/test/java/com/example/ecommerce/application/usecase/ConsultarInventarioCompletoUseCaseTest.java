package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.Categoria;
import com.example.ecommerce.domain.entity.Inventario;
import com.example.ecommerce.domain.entity.Marca;
import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.example.ecommerce.domain.valueobject.EstadoTienda;
import com.example.ecommerce.domain.valueobject.FechaVencimiento;
import com.example.ecommerce.domain.valueobject.NombreArticulo;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.domain.valueobject.TipoPiel;
import com.example.ecommerce.domain.valueobject.Tono;
import com.example.ecommerce.infrastructure.persistence.ArticuloRepositoryEnMemoria;
import com.example.ecommerce.infrastructure.persistence.TiendaRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConsultarInventarioCompletoUseCaseTest {

    private static final Tienda TIENDA_DE_VERA = Tienda.reconstruir("t-1", "Glow Natural", "", "v-1", EstadoTienda.ACTIVA);
    private static final Tienda TIENDA_DE_LINA = Tienda.reconstruir("t-2", "Lina Beauty", "", "v-2", EstadoTienda.ACTIVA);

    private final ArticuloRepositoryEnMemoria articulos = new ArticuloRepositoryEnMemoria();
    private final TiendaRepositoryEnMemoria tiendas = new TiendaRepositoryEnMemoria();
    private final ConsultarInventarioCompletoUseCase useCase =
            new ConsultarInventarioCompletoUseCase(articulos, tiendas);

    @BeforeEach
    void prepararTiendas() {
        tiendas.guardar(TIENDA_DE_VERA);
        tiendas.guardar(TIENDA_DE_LINA);
    }

    private Articulo articulo(String id, Tienda tienda) {
        return Articulo.crear(
                id,
                new NombreArticulo("Labial"),
                new Precio(new BigDecimal("20000")),
                Categoria.crear("cat-1", "Labios"),
                Marca.crear("m-1", "Maybelline"),
                Tono.MEDIO,
                List.of(TipoPiel.NORMAL),
                Inventario.crear(10),
                new FechaVencimiento(LocalDate.of(2030, 12, 31)),
                tienda
        );
    }

    @Test
    void debeRetornarSoloLosArticulosDeLaTiendaDelVendedor() {
        articulos.guardar(articulo("a-1", TIENDA_DE_VERA));
        articulos.guardar(articulo("a-2", TIENDA_DE_VERA));
        articulos.guardar(articulo("a-3", TIENDA_DE_LINA));

        List<Articulo> resultado = useCase.ejecutar("v-1");

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(a -> a.perteneceATienda("t-1")));
    }

    @Test
    void debeRetornarListaVaciaCuandoLaTiendaNoTieneArticulos() {
        articulos.guardar(articulo("a-3", TIENDA_DE_LINA));

        assertTrue(useCase.ejecutar("v-1").isEmpty());
    }

    @Test
    void unVendedorSinTiendaNoTieneInventario() {
        assertThrows(RecursoNoEncontradoException.class, () -> useCase.ejecutar("v-sin-tienda"));
    }
}
