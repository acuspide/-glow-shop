package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.request.CalificarArticuloRequest;
import com.example.ecommerce.application.dto.response.CalificacionResponse;
import com.example.ecommerce.application.usecase.CalificarArticuloUseCase;
import com.example.ecommerce.domain.entity.Calificacion;
import com.example.ecommerce.infrastructure.rest.mapper.CalificacionMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CalificacionController {

    private final CalificarArticuloUseCase calificarArticuloUseCase;
    private final TokenService tokenService;
    private final CalificacionMapper mapper;

    // CU-90: el cliente con sesión iniciada califica un artículo que recibió.
    @PostMapping("/articulos/{articuloId}/calificaciones")
    public ResponseEntity<CalificacionResponse> calificar(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @PathVariable long articuloId,
            @Valid @RequestBody CalificarArticuloRequest request) {

        String clienteId = tokenService.extraerUsuarioId(BearerToken.extraer(autorizacion));
        Calificacion calificacion = calificarArticuloUseCase.ejecutar(
                UUID.randomUUID().toString(), clienteId, articuloId, request.puntuacion(), request.comentario());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/calificaciones/{id}")
                .buildAndExpand(calificacion.getId())
                .toUri();

        return ResponseEntity.created(location).body(mapper.toResponse(calificacion));
    }
}
