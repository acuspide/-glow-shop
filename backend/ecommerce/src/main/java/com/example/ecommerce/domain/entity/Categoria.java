package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.CategoriaRequeridaException;

import java.util.Objects;

public class Categoria {

    private String id;
    private String nombre;

    private Categoria(String id, String nombre) {
        validarNombre(nombre);
        this.id = id;
        this.nombre = nombre.trim();
    }
    public static Categoria crear(String id, String nombre) {
        return new Categoria(id, nombre);
    }

    public static Categoria reconstruir(String id, String nombre) {
        return new Categoria(id, nombre);
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new CategoriaRequeridaException();
        }
    }
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Categoria otra)) return false;
        return id.equals(otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
