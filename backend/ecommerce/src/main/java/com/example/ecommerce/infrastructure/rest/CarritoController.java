package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.request.AgregarArticuloAlCarritoRequest;
import com.example.ecommerce.application.dto.response.CarritoResponse;
import com.example.ecommerce.application.usecase.AgregarAlCarritoUseCase;
import com.example.ecommerce.application.usecase.QuitarArticuloDelCarritoUseCase;
import com.example.ecommerce.application.usecase.VaciarCarritoUseCase;
import com.example.ecommerce.application.usecase.VerCarritoUseCase;
import com.example.ecommerce.domain.entity.Carrito;
import com.example.ecommerce.infrastructure.rest.mapper.CarritoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.ecommerce.application.dto.request.ModificarCantidadEnCarritoRequest;
import com.example.ecommerce.application.usecase.ModificarCantidadEnCarritoUseCase;
import java.util.UUID;

/**
 * Carrito del cliente con sesión iniciada. El cliente se identifica con el token
 * ("Authorization: Bearer <token>"), nunca con un id en la URL: así nadie puede
 * ver ni modificar el carrito de otra persona.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carrito")
public class CarritoController {

    private final AgregarAlCarritoUseCase agregarAlCarritoUseCase;
    private final CarritoMapper mapper;
    private final VerCarritoUseCase verCarritoUseCase;
    private final VaciarCarritoUseCase vaciarCarritoUseCase;
    private final QuitarArticuloDelCarritoUseCase quitarArticuloDelCarritoUseCase;
    private final ModificarCantidadEnCarritoUseCase modificarCantidadEnCarritoUseCase;
    private final TokenService tokenService;

    @PostMapping("/items")
    public ResponseEntity<CarritoResponse> agregarArticulo(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @Valid @RequestBody AgregarArticuloAlCarritoRequest request) {

        Carrito carrito = agregarAlCarritoUseCase.ejecutar(
                UUID.randomUUID().toString(),
                clienteDelToken(autorizacion),
                request.articuloId(),
                request.cantidad()
        );

        return ResponseEntity.ok(mapper.toResponse(carrito));
    }

    @GetMapping
    public ResponseEntity<CarritoResponse> verCarrito(
            @RequestHeader(value = "Authorization", required = false) String autorizacion) {

        Carrito carrito = verCarritoUseCase.ejecutar(clienteDelToken(autorizacion));

        return ResponseEntity.ok(mapper.toResponse(carrito));
    }

    @DeleteMapping("/items")
    public ResponseEntity<Void> vaciarCarrito(
            @RequestHeader(value = "Authorization", required = false) String autorizacion) {

        vaciarCarritoUseCase.ejecutar(clienteDelToken(autorizacion));

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/items/{articuloId}")
    public ResponseEntity<Void> quitarArticulo(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @PathVariable String articuloId) {

        quitarArticuloDelCarritoUseCase.ejecutar(clienteDelToken(autorizacion), articuloId);

        return ResponseEntity.noContent().build();
    }
    @PutMapping("/items/{articuloId}")
    public ResponseEntity<Void> modificarCantidad(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @PathVariable String articuloId,
            @Valid @RequestBody ModificarCantidadEnCarritoRequest request) {

        modificarCantidadEnCarritoUseCase.ejecutar(
                clienteDelToken(autorizacion),
                articuloId,
                request.cantidad()
        );

        return ResponseEntity.noContent().build();
    }

    private String clienteDelToken(String autorizacion) {
        return tokenService.extraerUsuarioId(BearerToken.extraer(autorizacion));
    }
}
