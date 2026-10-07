package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.ArticuloNoEncontradoEnCarritoException;
import com.example.ecommerce.domain.exception.CantidadInvalidaException;
import com.example.ecommerce.domain.valueobject.ItemCarrito;
import com.example.ecommerce.domain.valueobject.Precio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Carrito {
    private final long id;
    private final long clienteId;
    private final List<ItemCarrito> items;

    private Carrito(long id, long clienteId) {
        this.id = id;
        this.clienteId = clienteId;
        this.items = new ArrayList<>();
    }
    public static Carrito crear(long id, long clienteId) {
        return new Carrito(id, clienteId);
    }
    public void agregarArticulo(Articulo articulo, int cantidad) {
        if (cantidad <= 0) {
            throw new CantidadInvalidaException();
        }

        Optional<ItemCarrito> itemExistente = buscarItem(articulo.getId());
        int cantidadTotal = cantidad + itemExistente.map(ItemCarrito::getCantidad).orElse(0);

        articulo.validarDisponibleParaVenta(cantidadTotal);

        itemExistente.ifPresent(items::remove);
        items.add(new ItemCarrito(articulo.getId(), cantidadTotal, articulo.getPrecio()));
    }

    public void eliminarItem(long articuloId) {
        ItemCarrito item = buscarItem(articuloId)
                .orElseThrow(ArticuloNoEncontradoEnCarritoException::new);
        items.remove(item);
    }

    public void vaciar() {
        items.clear();
    }

    public BigDecimal calcularTotal() {
        return items.stream()
                .map(ItemCarrito::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean estaVacio() {
        return items.isEmpty();
    }

    private Optional<ItemCarrito> buscarItem(long articuloId) {
        return items.stream()
                .filter(item -> item.getArticuloId() == articuloId)
                .findFirst();
    }

    public long getId() {
        return id;
    }

    public long getClienteId() {
        return clienteId;
    }

    public List<ItemCarrito> getItems() {
        return List.copyOf(items);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Carrito otro)) return false;
        return id == otro.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
