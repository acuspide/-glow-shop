package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.MarcaRequeridaException;

import java.util.Objects;

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

    public static Marca reconstruir(String id, String nombre) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Marca otra)) return false;
        return id.equals(otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
