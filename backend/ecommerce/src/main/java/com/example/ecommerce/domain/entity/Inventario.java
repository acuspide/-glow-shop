package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.CantidadInvalidaException;
import com.example.ecommerce.domain.exception.StockNegativoException;

public class Inventario {
    private int cantidadDisponible;

    public Inventario(int cantidadDisponible) {
        validarCantidad(cantidadDisponible);
        this.cantidadDisponible = cantidadDisponible;
    }

    public void aumentar(int cantidad) {
        validarCantidadPositiva(cantidad);
        this.cantidadDisponible += cantidad;
    }

    public void disminuir(int cantidad) {
        validarCantidadPositiva(cantidad);

        int nuevaCantidad = this.cantidadDisponible - cantidad;
        validarCantidad(nuevaCantidad);

        this.cantidadDisponible = nuevaCantidad;
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    private void validarCantidad(int cantidadDisponible) {
        if (cantidadDisponible < 0) {
            throw new StockNegativoException();
        }
    }

    private void validarCantidadPositiva(int cantidad) {
        if (cantidad <= 0) {
            throw new CantidadInvalidaException();
        }
    }
}
