package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;

import com.example.ecommerce.domain.entity.Carrito;
import com.example.ecommerce.domain.repository.CarritoRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VerCarritoUseCase {

    private final CarritoRepository carritoRepository;

    @Transactional(readOnly = true)
    public Carrito ejecutar(String clienteId) {
        return carritoRepository.obtenerPorClienteId(clienteId).orElseThrow(() -> new RecursoNoEncontradoException("el carrito del cliente", clienteId));
    }
}
