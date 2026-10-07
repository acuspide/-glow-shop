package com.example.ecommerce.domain.valueobject;

import com.example.ecommerce.domain.exception.CantidadInvalidaException;
import com.example.ecommerce.domain.exception.PrecioRequeridoException;

import java.math.BigDecimal;
import java.util.Objects;

public class ItemCarrito {
    private final long articuloId;
    private final int cantidad;
    private final Precio precioUnitario;

    public ItemCarrito(long articuloId, int cantidad, Precio precioUnitario) {
        validarCantidad(cantidad);
        validarPrecio(precioUnitario);
        this.articuloId = articuloId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    private void validarCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new CantidadInvalidaException();
        }
    }

    private void validarPrecio(Precio precioUnitario) {
        if (precioUnitario == null) {
            throw new PrecioRequeridoException();
        }
    }

    public BigDecimal subtotal() {
        return precioUnitario.valor().multiply(BigDecimal.valueOf(cantidad));
    }

    public long getArticuloId() {
        return articuloId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public Precio getPrecioUnitario() {
        return precioUnitario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemCarrito otro)) return false;
        return articuloId == otro.articuloId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(articuloId);
    }
}