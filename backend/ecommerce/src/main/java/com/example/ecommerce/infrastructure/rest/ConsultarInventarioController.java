package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import org.springframework.web.bind.annotation.RequestHeader;import com.example.ecommerce.application.dto.response.ConsultarInventarioCompletoResponse;
import com.example.ecommerce.application.usecase.ConsultarInventarioCompletoUseCase;
import com.example.ecommerce.infrastructure.rest.mapper.ConsultarInventarioCompletoMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventario")
public class ConsultarInventarioController {
    private final ConsultarInventarioCompletoUseCase consultarInventarioCompletoUseCase;
    private final ConsultarInventarioCompletoMapper consultarInventarioCompletoMapper;
    private final TokenService tokenService;

    // Solo el vendedor con sesión iniciada, y solo ve los artículos de su tienda.
    @GetMapping
    public ResponseEntity<ConsultarInventarioCompletoResponse> consultarInventario(
            @RequestHeader(value = "Authorization", required = false) String autorizacion) {
        var articulos = consultarInventarioCompletoUseCase.ejecutar(
                tokenService.extraerUsuarioId(BearerToken.extraer(autorizacion)));

        var response = consultarInventarioCompletoMapper.toResponse(articulos);

        return ResponseEntity.ok(response);
    }
}
