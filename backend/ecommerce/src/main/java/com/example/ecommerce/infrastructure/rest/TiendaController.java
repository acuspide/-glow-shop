package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.request.RegistrarTiendaRequest;
import com.example.ecommerce.application.dto.response.TiendaResponse;
import com.example.ecommerce.application.usecase.RegistrarTiendaUseCase;
import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.infrastructure.rest.mapper.TiendaMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

/**
 * Tiendas del vendedor con sesión iniciada. El vendedor sale del token, nunca del cuerpo de la petición.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tiendas")
public class TiendaController {

    private final RegistrarTiendaUseCase registrarTiendaUseCase;
    private final TokenService tokenService;
    private final TiendaMapper mapper;

    @PostMapping
    public ResponseEntity<TiendaResponse> registrar(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @Valid @RequestBody RegistrarTiendaRequest request) {

        String vendedorId = tokenService.extraerUsuarioId(BearerToken.extraer(autorizacion));
        Tienda tienda = registrarTiendaUseCase.ejecutar(
                UUID.randomUUID().toString(), vendedorId, request.nombre(), request.descripcion());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(tienda.getId())
                .toUri();

        return ResponseEntity.created(location).body(mapper.toResponse(tienda));
    }
}
