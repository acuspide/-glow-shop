package com.example.ecommerce.domain.service;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.Carrito;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
import com.example.ecommerce.domain.exception.CarritoVacioException;
import com.example.ecommerce.domain.exception.PrecioDelArticuloCambioException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.valueobject.ItemCarrito;

/**
 * Servicio de dominio: antes de pagar, cada artículo del carrito debe seguir disponible
 * para la venta en la cantidad pedida y conservar el precio con el que se agregó.
 * La regla necesita mirar el carrito y los artículos a la vez, por eso no cabe en
 * ninguna de las dos entidades.
 */
public class VerificadorDeDisponibilidadDelCarrito {

    private final ArticuloRepository articuloRepository;

    public VerificadorDeDisponibilidadDelCarrito(ArticuloRepository articuloRepository) {
        this.articuloRepository = articuloRepository;
    }

    public void verificarQuePuedePagarse(Carrito carrito) {
        if (carrito.estaVacio()) {
            throw new CarritoVacioException();
        }

        for (ItemCarrito item : carrito.getItems()) {
            Articulo articulo = articuloRepository.obtenerPorId(item.getArticuloId())
                    .orElseThrow(() -> new ArticuloNoEncontradoException(item.getArticuloId()));

            articulo.validarDisponibleParaVenta(item.getCantidad());

            if (articulo.getPrecio().valor().compareTo(item.getPrecioUnitario().valor()) != 0) {
                throw new PrecioDelArticuloCambioException(item.getArticuloId());
            }
        }
    }
}