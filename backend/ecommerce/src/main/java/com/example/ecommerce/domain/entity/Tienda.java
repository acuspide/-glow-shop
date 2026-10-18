package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.TiendaRequeridaException;

public class Tienda {

    private long id;
    private String nombre;

    private Tienda(long id, String nombre) {
        validarNombre(nombre);
        this.id = id;
        this.nombre = nombre;
    }
    public static Tienda crear(long id, String nombre) {
        return new Tienda(id, nombre);
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new TiendaRequeridaException();
        }
    }

    public long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}
