package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.domain.entity.Carrito;
import com.example.ecommerce.domain.valueobject.ItemCarrito;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.CarritoPersistenceMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import({CarritoRepositoryJpa.class, CarritoPersistenceMapper.class})
class CarritoRepositoryJpaTest {

    @Autowired
    private CarritoRepositoryJpa repositorio;

    private Carrito carritoDeAna() {
        return Carrito.reconstruir("c-1", "u-1", List.of(
                new ItemCarrito("a-1", 2, new Precio(new BigDecimal("20000"))),
                new ItemCarrito("a-2", 1, new Precio(new BigDecimal("50000")))));
    }

    @Test
    void guardaElCarritoConSusItemsYLoEncuentraPorCliente() {
        repositorio.guardar(carritoDeAna());

        Carrito leido = repositorio.obtenerPorClienteId("u-1").orElseThrow();

        assertEquals("c-1", leido.getId());
        assertEquals(2, leido.getItems().size());
        assertEquals("a-1", leido.getItems().get(0).getArticuloId());
        assertEquals(0, new BigDecimal("90000").compareTo(leido.calcularTotal()));
        assertTrue(repositorio.obtenerPorClienteId("u-2").isEmpty());
    }

    @Test
    void quitarYVaciarSeReflejanEnLaBaseDeDatos() {
        Carrito carrito = carritoDeAna();
        repositorio.guardar(carrito);

        carrito.quitarArticulo("a-1");
        repositorio.guardar(carrito);
        assertEquals(1, repositorio.obtenerPorId("c-1").orElseThrow().getItems().size());

        carrito.vaciar();
        repositorio.guardar(carrito);
        assertTrue(repositorio.obtenerPorId("c-1").orElseThrow().estaVacio());
    }
}
