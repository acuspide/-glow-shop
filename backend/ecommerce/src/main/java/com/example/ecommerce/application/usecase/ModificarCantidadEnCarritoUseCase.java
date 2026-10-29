package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.Carrito;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.CarritoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ModificarCantidadEnCarritoUseCase {

    private final CarritoRepository carritoRepository;
    private final ArticuloRepository articuloRepository;

    @Transactional
    public void ejecutar(String clienteId, String articuloId, int nuevaCantidad) {
        Carrito carrito = carritoRepository.obtenerPorClienteId(clienteId).orElseThrow(() -> new RecursoNoEncontradoException("el carrito del cliente", clienteId));
        Articulo articulo = articuloRepository.obtenerPorId(articuloId).orElseThrow(() -> new ArticuloNoEncontradoException(articuloId));
        carrito.modificarCantidad(articulo, nuevaCantidad);
        carritoRepository.guardar(carrito);
    }
}
