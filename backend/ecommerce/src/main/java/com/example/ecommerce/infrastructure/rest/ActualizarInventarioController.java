package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.dto.request.ActualizarInventarioRequest;
import com.example.ecommerce.application.dto.response.ActualizarInventarioResponse;
import com.example.ecommerce.application.usecase.ActualizarInventarioUseCase;
import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.infrastructure.rest.mapper.ActualizarInventarioMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articulos")
public class ActualizarInventarioController {
    private final ActualizarInventarioUseCase actualizarInventarioUseCase;
    private final ActualizarInventarioMapper mapper;

    @PostMapping("/{id}/inventario/aumentar")
    public ResponseEntity<ActualizarInventarioResponse> aumentar(
            @PathVariable String id,
            @Valid @RequestBody ActualizarInventarioRequest request) {

        Articulo articulo = actualizarInventarioUseCase.aumentar(
                id,
                request.cantidad()
        );

        ActualizarInventarioResponse response = mapper.toResponse(articulo);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/inventario/disminuir")
    public ResponseEntity<ActualizarInventarioResponse> disminuir(
            @PathVariable String id,
            @Valid @RequestBody ActualizarInventarioRequest request) {

        Articulo articulo = actualizarInventarioUseCase.disminuir(
                id,
                request.cantidad()
        );

        ActualizarInventarioResponse response = mapper.toResponse(articulo);

        return ResponseEntity.ok(response);
    }

}
