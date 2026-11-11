package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.request.RegistrarTiendaRequest;
import com.example.ecommerce.application.dto.response.TiendaResponse;
import com.example.ecommerce.application.usecase.ConsultarMiTiendaUseCase;
import com.example.ecommerce.application.usecase.RegistrarTiendaUseCase;
import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.infrastructure.rest.mapper.TiendaMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    private final ConsultarMiTiendaUseCase consultarMiTiendaUseCase;
    private final TokenService tokenService;
    private final TiendaMapper mapper;

    // La tienda del vendedor con sesión iniciada; 404 si todavía no ha registrado una.
    @GetMapping("/mia")
    public ResponseEntity<TiendaResponse> consultarMiTienda(
            @RequestHeader(value = "Authorization", required = false) String autorizacion) {

        String vendedorId = tokenService.extraerUsuarioId(BearerToken.extraer(autorizacion));
        Tienda tienda = consultarMiTiendaUseCase.ejecutar(vendedorId);

        return ResponseEntity.ok(mapper.toResponse(tienda));
    }

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
