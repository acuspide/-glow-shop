package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
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
    private final TokenService tokenService;

    @PostMapping("/{id}/inventario/aumentar")
    public ResponseEntity<ActualizarInventarioResponse> aumentar(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @PathVariable String id,
            @Valid @RequestBody ActualizarInventarioRequest request) {

        Articulo articulo = actualizarInventarioUseCase.aumentar(
                id,
                vendedorDelToken(autorizacion),
                request.cantidad()
        );

        ActualizarInventarioResponse response = mapper.toResponse(articulo);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/inventario/disminuir")
    public ResponseEntity<ActualizarInventarioResponse> disminuir(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @PathVariable String id,
            @Valid @RequestBody ActualizarInventarioRequest request) {

        Articulo articulo = actualizarInventarioUseCase.disminuir(
                id,
                vendedorDelToken(autorizacion),
                request.cantidad()
        );

        ActualizarInventarioResponse response = mapper.toResponse(articulo);

        return ResponseEntity.ok(response);
    }

    // El vendedor sale del token; el caso de uso verifica que sea el dueño de la tienda. dueño de la tienda del artículo puede cambiar su inventario.
    private String vendedorDelToken(String autorizacion) {
        return tokenService.extraerUsuarioId(BearerToken.extraer(autorizacion));
    }
}
