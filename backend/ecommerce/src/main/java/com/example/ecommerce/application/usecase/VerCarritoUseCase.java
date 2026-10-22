package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Carrito;
import com.example.ecommerce.domain.repository.CarritoRepository;
import org.springframework.stereotype.Service;

@Service
public class VerCarritoUseCase {

    private final CarritoRepository carritoRepository;

    public VerCarritoUseCase(CarritoRepository carritoRepository) {
        this.carritoRepository = carritoRepository;
    }

    public Carrito ejecutar(long clienteId) {
        return carritoRepository.obtenerPorClienteId(clienteId).orElseThrow();
    }
}
