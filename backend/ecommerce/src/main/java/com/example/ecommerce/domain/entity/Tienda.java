package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.TiendaRequeridaException;

public class Tienda {

    private String id;
    private String nombre;

    private Tienda(String id, String nombre) {
        validarNombre(nombre);
        this.id = id;
        this.nombre = nombre;
    }
    public static Tienda crear(String id, String nombre) {
        return new Tienda(id, nombre);
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new TiendaRequeridaException();
        }
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}
