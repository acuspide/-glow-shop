package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.request.ProcesarPagoRequest;
import com.example.ecommerce.application.dto.response.PagoResponse;
import com.example.ecommerce.application.usecase.ProcesarPagoDePedidoUseCase;
import com.example.ecommerce.domain.entity.Pago;
import com.example.ecommerce.infrastructure.rest.mapper.PagoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.example.ecommerce.application.usecase.ConsultarEstadoDePagoUseCase;
import java.net.URI;
import java.util.UUID;

/**
 * Pagos del cliente con sesión iniciada ("Authorization: Bearer <token>").
 * Un pago rechazado no es un error: se responde 201 con el pago y su motivo de rechazo.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PagoController {

    private final ProcesarPagoDePedidoUseCase procesarPagoDePedidoUseCase;
    private final ConsultarEstadoDePagoUseCase consultarEstadoDePagoUseCase;
    private final PagoMapper mapper;
    private final TokenService tokenService;

    @PostMapping("/pedidos/{pedidoId}/pagos")
    public ResponseEntity<PagoResponse> procesarPago(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @PathVariable String pedidoId,
            @Valid @RequestBody ProcesarPagoRequest request) {

        Pago pago = procesarPagoDePedidoUseCase.ejecutar(
                UUID.randomUUID().toString(),
                pedidoId,
                clienteDelToken(autorizacion),
                request.medioPago()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/pagos/{id}")
                .buildAndExpand(pago.getId())
                .toUri();

        return ResponseEntity.created(location).body(mapper.toResponse(pago)); // 201
    }
    @GetMapping("/pagos/{id}")
    public ResponseEntity<PagoResponse> consultarEstado(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @PathVariable String id) {

        Pago pago = consultarEstadoDePagoUseCase.ejecutar(id, clienteDelToken(autorizacion));

        return ResponseEntity.ok(mapper.toResponse(pago)); // 200
    }
    private String clienteDelToken(String autorizacion) {
        return tokenService.extraerUsuarioId(BearerToken.extraer(autorizacion));
    }
}
