package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.request.ActualizarEstadoPedidoRequest;
import com.example.ecommerce.application.dto.response.PedidoResponse;
import com.example.ecommerce.application.usecase.ActualizarEstadoPedidoUseCase;
import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.infrastructure.rest.mapper.PedidoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Pedidos que recibe la tienda del vendedor con sesión iniciada. El vendedor sale del token.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vendedor/pedidos")
public class VendedorPedidoController {

    private final ActualizarEstadoPedidoUseCase actualizarEstadoPedidoUseCase;
    private final TokenService tokenService;
    private final PedidoMapper mapper;

    // Actualizar estado del pedido: EN_PREPARACION -> ENVIADO -> ENTREGADO.
    @PutMapping("/{id}/estado")
    public ResponseEntity<PedidoResponse> actualizarEstado(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @PathVariable String id,
            @Valid @RequestBody ActualizarEstadoPedidoRequest request) {

        Pedido pedido = actualizarEstadoPedidoUseCase.ejecutar(id, vendedorDelToken(autorizacion), request.estado());

        return ResponseEntity.ok(mapper.toResponse(pedido));
    }

    private String vendedorDelToken(String autorizacion) {
        return tokenService.extraerUsuarioId(BearerToken.extraer(autorizacion));
    }
}
