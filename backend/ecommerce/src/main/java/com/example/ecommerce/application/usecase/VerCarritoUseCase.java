package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Carrito;
import com.example.ecommerce.domain.repository.CarritoRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerCarritoUseCase {

    private final CarritoRepository carritoRepository;

    // Un cliente que todavía no ha agregado nada no tiene carrito guardado: se le muestra uno vacío
    // (200 con items vacíos) en vez de un 404, porque para la persona no es un error. No se guarda.
    @Transactional(readOnly = true)
    public Carrito ejecutar(String clienteId) {
        return carritoRepository.obtenerPorClienteId(clienteId)
                .orElseGet(() -> Carrito.crear(UUID.randomUUID().toString(), clienteId));
    }
}
