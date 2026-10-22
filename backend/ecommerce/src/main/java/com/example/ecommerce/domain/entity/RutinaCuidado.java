package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.ArticuloNoPublicadoException;
import com.example.ecommerce.domain.exception.ArticuloSinDisponibilidadException;
import com.example.ecommerce.domain.exception.RutinaSinMinimoDeArticulosException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RutinaCuidado {
    private final String id;
    private final String nombre;
    private final List<Articulo> articulos;

    private RutinaCuidado(String id, String nombre, List<Articulo> articulos) {
        validarArticulos(articulos);

        this.id = id;
        this.nombre = nombre;
        this.articulos = List.copyOf(articulos);
    }

    public static RutinaCuidado crear(
            String id,
            String nombre,
            List<Articulo> articulos
    ) {
        return new RutinaCuidado(id, nombre, articulos);
    }

    private void validarArticulos(List<Articulo> articulos) {

        if (articulos == null || articulos.size() < 2) {
            throw new RutinaSinMinimoDeArticulosException();
        }

        for (Articulo articulo : articulos) {

            if (!articulo.isPublicado()) {
                throw new ArticuloNoPublicadoException();
            }

            if (articulo.getCantidadDisponible() <= 0) {
                throw new ArticuloSinDisponibilidadException();
            }
        }
    }
    public RutinaCuidado agregarArticulo(Articulo articulo) {
        List<Articulo> articulosActualizados = new ArrayList<>(articulos);
        articulosActualizados.add(articulo);

        return RutinaCuidado.crear(
                this.id,
                this.nombre,
                articulosActualizados
        );
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public List<Articulo> getArticulos() {
        return articulos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RutinaCuidado otra)) return false;
        return id.equals(otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
