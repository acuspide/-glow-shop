package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.response.TiendaResponse;
import com.example.ecommerce.application.usecase.AprobarTiendaUseCase;
import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.infrastructure.rest.mapper.TiendaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Administración de tiendas. El administrador sale del token; el dominio verifica su rol.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/tiendas")
public class AdminTiendaController {

    private final AprobarTiendaUseCase aprobarTiendaUseCase;
    private final TokenService tokenService;
    private final TiendaMapper mapper;

    @PutMapping("/{id}/aprobar")
    public ResponseEntity<TiendaResponse> aprobar(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @PathVariable String id) {

        String administradorId = tokenService.extraerUsuarioId(BearerToken.extraer(autorizacion));
        Tienda tienda = aprobarTiendaUseCase.ejecutar(id, administradorId);

        return ResponseEntity.ok(mapper.toResponse(tienda));
    }
}
