package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.response.PedidoResponse;
import com.example.ecommerce.application.usecase.ConfirmarPedidoUseCase;
import com.example.ecommerce.application.usecase.ConsultarPedidoUseCase;
import com.example.ecommerce.application.usecase.ListarMisPedidosUseCase;
import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.infrastructure.rest.mapper.PedidoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Pedidos del cliente con sesión iniciada. El cliente se identifica con el token
 * ("Authorization: Bearer <token>") y los artículos salen de su carrito.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final ConfirmarPedidoUseCase confirmarPedidoUseCase;
    private final ConsultarPedidoUseCase consultarPedidoUseCase;
    private final ListarMisPedidosUseCase listarMisPedidosUseCase;
    private final PedidoMapper mapper;
    private final TokenService tokenService;

    @PostMapping
    public ResponseEntity<PedidoResponse> confirmarPedido(
            @RequestHeader(value = "Authorization", required = false) String autorizacion) {

        Pedido pedido = confirmarPedidoUseCase.ejecutar(
                UUID.randomUUID().toString(),
                clienteDelToken(autorizacion)
        );

        PedidoResponse response = mapper.toResponse(pedido);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(pedido.getId())
                .toUri();

        return ResponseEntity.created(location).body(response); // 201
    }

    // Listar mis pedidos: el historial de compras del cliente del token.
    @GetMapping
    public ResponseEntity<List<PedidoResponse>> listarMisPedidos(
            @RequestHeader(value = "Authorization", required = false) String autorizacion) {

        List<PedidoResponse> pedidos = listarMisPedidosUseCase.ejecutar(clienteDelToken(autorizacion))
                .stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(pedidos);
    }

    // Consultar pedido: solo el cliente dueño del pedido puede verlo.
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> consultarPedido(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @PathVariable String id) {

        Pedido pedido = consultarPedidoUseCase.ejecutar(id, clienteDelToken(autorizacion));

        return ResponseEntity.ok(mapper.toResponse(pedido));
    }

    private String clienteDelToken(String autorizacion) {
        return tokenService.extraerUsuarioId(BearerToken.extraer(autorizacion));
    }
}
