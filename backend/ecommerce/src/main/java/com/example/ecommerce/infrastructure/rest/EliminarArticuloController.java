package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.usecase.EliminarArticuloUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articulos")
public class EliminarArticuloController {
    private final EliminarArticuloUseCase eliminarArticuloUseCase;
    private final TokenService tokenService;

    // Solo el vendedor dueño de la tienda del artículo puede eliminarlo. El vendedor sale del token.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @PathVariable String id) {

        String vendedorId = tokenService.extraerUsuarioId(BearerToken.extraer(autorizacion));
        eliminarArticuloUseCase.ejecutar(id, vendedorId);

        return ResponseEntity.noContent().build();
    }
}
