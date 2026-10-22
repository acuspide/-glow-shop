package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.Carrito;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.CarritoRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgregarAlCarritoUseCase {

    private final CarritoRepository carritoRepository;
    private final ArticuloRepository articuloRepository;

    @Transactional
    public Carrito ejecutar(String nuevoCarritoId, String clienteId, long articuloId, int cantidad) {

        Articulo articulo = articuloRepository.obtenerPorId(articuloId).orElseThrow();

        Carrito carrito = carritoRepository.obtenerPorClienteId(clienteId)
                .orElseGet(() -> Carrito.crear(nuevoCarritoId, clienteId));

        carrito.agregarArticulo(articulo, cantidad);

        carritoRepository.guardar(carrito);

        return carrito;
    }
}