package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.dto.request.AgregarArticuloAlCarritoRequest;
import com.example.ecommerce.application.dto.response.CarritoResponse;
import com.example.ecommerce.application.usecase.AgregarAlCarritoUseCase;
import com.example.ecommerce.domain.entity.Carrito;
import com.example.ecommerce.infrastructure.rest.mapper.CarritoMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carritos")
public class CarritoController {

    private final AgregarAlCarritoUseCase agregarAlCarritoUseCase;
    private final CarritoMapper mapper;

    public CarritoController(AgregarAlCarritoUseCase agregarAlCarritoUseCase, CarritoMapper mapper) {
        this.agregarAlCarritoUseCase = agregarAlCarritoUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/{clienteId}/items")
    public ResponseEntity<CarritoResponse> agregarArticulo(
            @PathVariable long clienteId,
            @Valid @RequestBody AgregarArticuloAlCarritoRequest request) {

        Carrito carrito = agregarAlCarritoUseCase.ejecutar(
                clienteId,
                request.articuloId(),
                request.cantidad()
        );

        return ResponseEntity.ok(mapper.toResponse(carrito));
    }
}