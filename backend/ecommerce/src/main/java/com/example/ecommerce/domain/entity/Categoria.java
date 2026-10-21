package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.CategoriaRequeridaException;

public class Categoria {

    private long id;
    private String nombre;

    private Categoria(long id, String nombre) {
        validarNombre(nombre);
        this.id = id;
        this.nombre = nombre;
    }
    public static Categoria crear(long id, String nombre) {
        return new Categoria(id, nombre);
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new CategoriaRequeridaException();
        }
    }
    public long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}
