package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.Carrito;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.CarritoRepository;
import org.springframework.stereotype.Service;

@Service
public class AgregarAlCarritoUseCase {

    private final CarritoRepository carritoRepository;
    private final ArticuloRepository articuloRepository;

    public AgregarAlCarritoUseCase(CarritoRepository carritoRepository, ArticuloRepository articuloRepository) {
        this.carritoRepository = carritoRepository;
        this.articuloRepository = articuloRepository;
    }

    public Carrito ejecutar(long clienteId, long articuloId, int cantidad) {

        Articulo articulo = articuloRepository.obtenerPorId(articuloId).orElseThrow();

        Carrito carrito = carritoRepository.obtenerPorClienteId(clienteId)
                .orElseGet(() -> Carrito.crear(carritoRepository.siguienteId(), clienteId));

        carrito.agregarArticulo(articulo, cantidad);

        carritoRepository.guardar(carrito);

        return carrito;
    }
}