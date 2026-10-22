package com.example.ecommerce.domain.entity;


import com.example.ecommerce.domain.exception.*;
import com.example.ecommerce.domain.valueobject.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class Articulo {
    private final String id;
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

    private Articulo(
            String id,
            NombreArticulo nombre,
            Precio precio,
            Categoria categoria,
            Marca marca,
            Tono tono,
            List<TipoPiel> tiposPiel,
            Inventario inventario,
            FechaVencimiento fechaVencimiento,
            Tienda tienda,
            boolean eliminado,
            boolean publicado){

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
        this.eliminado = eliminado;
        this.publicado = publicado;

    }
    public static Articulo crear(
            String id,
            NombreArticulo nombre,
            Precio precio,
            Categoria categoria,
            Marca marca,
            Tono tono,
            List<TipoPiel> tiposPiel,
            Inventario inventario,
            FechaVencimiento fechaVencimiento,
            Tienda tienda
    ) {
        return new Articulo(
                id,
                nombre,
                precio,
                categoria,
                marca,
                tono,
                tiposPiel,
                inventario,
                fechaVencimiento,
                tienda,
                false,
                false
        );
    }

    public static Articulo reconstruir(
            String id,
            NombreArticulo nombre,
            Precio precio,
            Categoria categoria,
            Marca marca,
            Tono tono,
            List<TipoPiel> tiposPiel,
            Inventario inventario,
            FechaVencimiento fechaVencimiento,
            Tienda tienda,
            boolean eliminado,
            boolean publicado
    ) {
        return new Articulo(id, nombre, precio, categoria, marca, tono, tiposPiel,
                inventario, fechaVencimiento, tienda, eliminado, publicado);
    }

    public void publicar(){
        validarPuedePublicarse();
        this.publicado = true;
    }
    public void aumentarInventario(int cantidad) {
        inventario.aumentar(cantidad);
    }

    public void disminuirInventario(int cantidad) {
        inventario.disminuir(cantidad);
    }


    public String getId() {
        return id;
    }


    public NombreArticulo getNombre() {
        return nombre;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public Marca getMarca() {
        return marca;
    }

    public Tono getTono() {
        return tono;
    }

    public List<TipoPiel> getTiposPiel() {
        return tiposPiel;
    }

    public FechaVencimiento getFechaVencimiento() {
        return fechaVencimiento;
    }

    public Tienda getTienda() {
        return tienda;
    }


    public int getCantidadDisponible() {
        return inventario.getCantidadDisponible();
    }

    public boolean isPublicado() {
        return publicado;
    }


    private void validarPuedePublicarse(){
        if (categoria == null){
            throw new CategoriaRequeridaException();
        }
        if (marca == null) {
            throw new MarcaRequeridaException();
        }

        if (precio == null) {
            throw new PrecioRequeridoException();
        }

        if (inventario == null) {
            throw new CantidadDisponibleRequeridaException();
        }

        if (fechaVencimiento == null) {
            throw new FechaVencimientoRequeridaException();
        }
        if (fechaVencimiento.estaVencida()) {
            throw new ArticuloVencidoException();
        }
        if (tienda == null) {
            throw new TiendaRequeridaException();
        }
    }
    public Precio getPrecio() {return precio; }

    //Este metodo valida al momento de la agregar al carrito, es decir en modo venta y el anterior valida al momento de publicar
    //el articulo en el catalogo

    public void validarDisponibleParaVenta(int cantidad) {
        if (!publicado || eliminado) {
            throw new ArticuloNoPublicadoParaVentaException();
        }
        if (fechaVencimiento.estaVencida()) {
            throw new ArticuloVencidoParaVentaException();
        }
        if (getCantidadDisponible() == 0) {
            throw new ArticuloAgotadoException();
        }
        inventario.validarDisponibilidad(cantidad);
    }
    public void validarDisponibleParaConsulta() {
        if (!publicado || eliminado) {
            throw new ArticuloNoPublicadoParaVentaException();
        }

        if (fechaVencimiento.estaVencida()) {
            throw new ArticuloVencidoParaVentaException();
        }

        if (getCantidadDisponible() == 0) {
            throw new ArticuloAgotadoException();
        }
    }
    //metodos para la busqueda
    public boolean coincideConTexto(String texto) {
        return nombre.valor()
                .toLowerCase()
                .contains(texto.trim().toLowerCase());
    }


    public void actualizarDatos(
            NombreArticulo nombre,
            Precio precio,
            Categoria categoria,
            Marca marca,
            Tono tono,
            List<TipoPiel> tiposPiel,
            FechaVencimiento fechaVencimiento
    ) {
        if (eliminado) {
            throw new ArticuloEliminadoException();
        }

        if (nombre == null) {
            throw new NombreArticuloRequeridoException();
        }

        if (precio == null) {
            throw new PrecioRequeridoException();
        }

        if (categoria == null) {
            throw new CategoriaRequeridaException();
        }

        if (marca == null) {
            throw new MarcaRequeridaException();
        }

        if (tono == null) {
            throw new IllegalArgumentException(
                    "El tono es obligatorio."
            );
        }

        if (tiposPiel == null || tiposPiel.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe especificarse al menos un tipo de piel."
            );
        }

        if (fechaVencimiento == null) {
            throw new FechaVencimientoRequeridaException();
        }

        if (fechaVencimiento.estaVencida()) {
            throw new ArticuloVencidoException();
        }

        // Todas las validaciones se realizan antes de modificar los datos.
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.marca = marca;
        this.tono = tono;
        this.tiposPiel = List.copyOf(tiposPiel);
        this.fechaVencimiento = fechaVencimiento;
    }

    public void eliminar() {
        if (eliminado) {
            throw new ArticuloEliminadoException();
        }

        eliminado = true;
    }



    public boolean perteneceACategoria(String categoriaId) {
        return categoria.getId().equals(categoriaId);
    }

    public boolean esDeMarca(String marcaId) {
        return marca.getId().equals(marcaId);
    }

    public boolean perteneceATienda(String tiendaId) {
        return tienda.getId().equals(tiendaId);
    }

    public boolean tienePrecioMinimo(BigDecimal precioMinimo) {
        return precio.valor().compareTo(precioMinimo) >= 0;
    }

    public boolean tienePrecioMaximo(BigDecimal precioMaximo) {
        return precio.valor().compareTo(precioMaximo) <= 0;
    }

    public boolean tieneTono(Tono tono) {
        return this.tono == tono;
    }

    public boolean esCompatibleConTipoPiel(TipoPiel tipoPiel) {
        return tiposPiel.contains(tipoPiel);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Articulo otro)) return false;
        return id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
