package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.dto.response.ConsultarInventarioCompletoResponse;
import com.example.ecommerce.application.usecase.AlertarStockBajoUseCase;
import com.example.ecommerce.infrastructure.rest.mapper.ConsultarInventarioCompletoMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventario")
public class AlertarStockBajoController {
    private final AlertarStockBajoUseCase alertarStockBajoUseCase;
    private final ConsultarInventarioCompletoMapper inventarioMapper;

    public AlertarStockBajoController(
            AlertarStockBajoUseCase alertarStockBajoUseCase,
            ConsultarInventarioCompletoMapper inventarioMapper) {
        this.alertarStockBajoUseCase = alertarStockBajoUseCase;
        this.inventarioMapper = inventarioMapper;
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<ConsultarInventarioCompletoResponse> alertarStockBajo() {
        var articulos = alertarStockBajoUseCase.ejecutar();
        var response = inventarioMapper.toResponse(articulos);

        return ResponseEntity.ok(response);
    }
}
