package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.MarcaRequeridaException;

public class Marca {
    private String id;
    private String nombre;

    private Marca(String id, String nombre) {
        validarNombre(nombre);
        this.id = id;
        this.nombre = nombre;
    }
    public static Marca crear(String id, String nombre) {
        return new Marca(id, nombre);
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new MarcaRequeridaException();
        }
    }

    public String getNombre() {
        return nombre;
    }

    public String getId() {
        return id;
    }
}
