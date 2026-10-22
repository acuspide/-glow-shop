package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.TiendaRequeridaException;

import java.util.Objects;

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

    public static Tienda reconstruir(String id, String nombre) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tienda otra)) return false;
        return id.equals(otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
