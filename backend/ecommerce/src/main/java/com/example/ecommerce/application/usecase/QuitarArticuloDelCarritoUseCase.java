package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;

import com.example.ecommerce.domain.entity.Carrito;
import com.example.ecommerce.domain.repository.CarritoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuitarArticuloDelCarritoUseCase {

    private final CarritoRepository carritoRepository;

    @Transactional
    public void ejecutar(String clienteId, String articuloId) {
        Carrito carrito = carritoRepository.obtenerPorClienteId(clienteId).orElseThrow(() -> new RecursoNoEncontradoException("el carrito del cliente", clienteId));
        carrito.quitarArticulo(articuloId);
        carritoRepository.guardar(carrito);
    }
}