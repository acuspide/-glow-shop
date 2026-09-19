package com.example.ecommerce.domain.entity;


import java.util.List;

public class Articulo {
    private long id;
    private NombreArticulo nombre;
    private Precio precio;
    private Categoria categoria;
    private Marca marca;
    private Tono tono;
    private List<TipoPiel> tiposPiel;
    private Inventario inventario;
    private FechaVencimiento fechaVencimiento;
    private Tienda tienda;
    private boolean eliminado;
    private boolean publicado;

    public Articulo(
            long id,
            NombreArticulo nombre,
            Precio precio,
            Categoria categoria,
            Marca marca,
            Tono tono,
            List<TipoPiel> tiposPiel,
            Inventario inventario,
            FechaVencimiento fechaVencimiento,
            Tienda tienda){

        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.marca = marca;
        this.tono = tono;
        this.tiposPiel = tiposPiel;
        this.inventario = inventario;
        this.fechaVencimiento = fechaVencimiento;
        this.tienda = tienda;
        this.eliminado = false;
        this.publicado = false;

    }

    public void publicar(){
        validarPuedePublicarse();
        this.publicado = true;
    }

    private void validarPuedePublicarse(){
        if (categoria == null){
            throw new IllegalStateException("el articulo debe tener una categoria");
        }
        if (marca == null) {
            throw new IllegalStateException(
                    "El artículo debe tener una marca"
            );
        }

        if (precio == null) {
            throw new IllegalStateException(
                    "El artículo debe tener un precio"
            );
        }

        if (inventario == null) {
            throw new IllegalStateException(
                    "El artículo debe tener inventario"
            );
        }

        if (fechaVencimiento == null) {
            throw new IllegalStateException(
                    "El artículo debe tener fecha de vencimiento"
            );
        }
    }


}
