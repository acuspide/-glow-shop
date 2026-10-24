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
import com.example.ecommerce.application.usecase.ListarHistorialDePagosUseCase;
import java.util.List;
import com.example.ecommerce.application.dto.request.ReintentarPagoRequest;
import com.example.ecommerce.application.usecase.ReintentarPagoUseCase;
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
    private final ListarHistorialDePagosUseCase listarHistorialDePagosUseCase;
    private final PagoMapper mapper;
    private final TokenService tokenService;
    private final ReintentarPagoUseCase reintentarPagoUseCase;

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
    @GetMapping("/pagos")
    public ResponseEntity<List<PagoResponse>> listarHistorial(
            @RequestHeader(value = "Authorization", required = false) String autorizacion) {

        List<PagoResponse> historial = listarHistorialDePagosUseCase.ejecutar(clienteDelToken(autorizacion))
                .stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(historial); // 200, lista vacía si no hay pagos
    }

    @GetMapping("/pagos/{id}")
    public ResponseEntity<PagoResponse> consultarEstado(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @PathVariable String id) {

        Pago pago = consultarEstadoDePagoUseCase.ejecutar(id, clienteDelToken(autorizacion));

        return ResponseEntity.ok(mapper.toResponse(pago)); // 200
    }
    @PostMapping("/pagos/{id}/reintentos")
    public ResponseEntity<PagoResponse> reintentarPago(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @PathVariable String id,
            @RequestBody(required = false) ReintentarPagoRequest request) {

        Pago nuevoPago = reintentarPagoUseCase.ejecutar(
                UUID.randomUUID().toString(),
                id,
                clienteDelToken(autorizacion),
                request != null ? request.medioPago() : null
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/pagos/{id}")
                .buildAndExpand(nuevoPago.getId())
                .toUri();

        return ResponseEntity.created(location).body(mapper.toResponse(nuevoPago)); // 201
    }
    private String clienteDelToken(String autorizacion) {
        return tokenService.extraerUsuarioId(BearerToken.extraer(autorizacion));
    }
}
