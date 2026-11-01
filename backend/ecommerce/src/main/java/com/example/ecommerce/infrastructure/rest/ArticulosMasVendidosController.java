package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.dto.response.ArticuloMasVendidoResponse;
import com.example.ecommerce.application.usecase.ConsultarArticulosMasVendidosUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reportes/articulos-mas-vendidos")
public class ArticulosMasVendidosController {

    private final ConsultarArticulosMasVendidosUseCase consultarArticulosMasVendidosUseCase;

    @GetMapping
    public ResponseEntity<List<ArticuloMasVendidoResponse>> consultar(
            @RequestParam(name = "limite", defaultValue = "5") int limite) {

        List<ArticuloMasVendidoResponse> respuesta = consultarArticulosMasVendidosUseCase.ejecutar(limite)
                .stream()
                .map(ArticuloMasVendidoResponse::desde)
                .toList();

        return ResponseEntity.ok(respuesta);
    }
}
