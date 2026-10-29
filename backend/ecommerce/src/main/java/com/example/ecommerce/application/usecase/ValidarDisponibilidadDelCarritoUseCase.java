package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;

import com.example.ecommerce.domain.entity.Carrito;
import com.example.ecommerce.domain.repository.CarritoRepository;
import com.example.ecommerce.domain.service.VerificadorDeDisponibilidadDelCarrito;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ValidarDisponibilidadDelCarritoUseCase {

    private final CarritoRepository carritoRepository;
    private final VerificadorDeDisponibilidadDelCarrito verificador;

    @Transactional(readOnly = true)
    public void ejecutar(String clienteId) {
        Carrito carrito = carritoRepository.obtenerPorClienteId(clienteId).orElseThrow(() -> new RecursoNoEncontradoException("el carrito del cliente", clienteId));
        verificador.verificarQuePuedePagarse(carrito);
    }
}
