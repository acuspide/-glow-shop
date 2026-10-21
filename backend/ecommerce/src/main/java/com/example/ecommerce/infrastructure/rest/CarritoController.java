package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.dto.request.AgregarArticuloAlCarritoRequest;
import com.example.ecommerce.application.dto.response.CarritoResponse;
import com.example.ecommerce.application.usecase.AgregarAlCarritoUseCase;
import com.example.ecommerce.application.usecase.VerCarritoUseCase;
import com.example.ecommerce.domain.entity.Carrito;
import com.example.ecommerce.infrastructure.rest.mapper.CarritoMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.ecommerce.application.usecase.VaciarCarritoUseCase;

@RestController
@RequestMapping("/api/carritos")
public class CarritoController {

    private final AgregarAlCarritoUseCase agregarAlCarritoUseCase;
    private final CarritoMapper mapper;
    private final VerCarritoUseCase verCarritoUseCase;
    private final VaciarCarritoUseCase vaciarCarritoUseCase;

    public CarritoController(AgregarAlCarritoUseCase agregarAlCarritoUseCase,
                             CarritoMapper mapper,
                             VerCarritoUseCase verCarritoUseCase,
                             VaciarCarritoUseCase vaciarCarritoUseCase) {
        this.agregarAlCarritoUseCase = agregarAlCarritoUseCase;
        this.mapper = mapper;
        this.verCarritoUseCase = verCarritoUseCase;
        this.vaciarCarritoUseCase = vaciarCarritoUseCase;
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

    @GetMapping("/{clienteId}")
    public ResponseEntity<CarritoResponse> verCarrito(@PathVariable long clienteId) {

        Carrito carrito = verCarritoUseCase.ejecutar(clienteId);

        return ResponseEntity.ok(mapper.toResponse(carrito));
    }
    @DeleteMapping("/{clienteId}/items")
    public ResponseEntity<Void> vaciarCarrito(@PathVariable long clienteId) {

        vaciarCarritoUseCase.ejecutar(clienteId);

        return ResponseEntity.noContent().build();
    }
}