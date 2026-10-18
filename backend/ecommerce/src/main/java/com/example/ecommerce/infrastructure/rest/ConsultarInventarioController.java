package com.example.ecommerce.infrastructure.rest;
import com.example.ecommerce.application.dto.response.ConsultarInventarioCompletoResponse;
import com.example.ecommerce.application.usecase.ConsultarInventarioCompletoUseCase;
import com.example.ecommerce.infrastructure.rest.mapper.ConsultarInventarioCompletoMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventario")
public class ConsultarInventarioController {
    private final ConsultarInventarioCompletoUseCase consultarInventarioCompletoUseCase;
    private final ConsultarInventarioCompletoMapper consultarInventarioCompletoMapper;

    public ConsultarInventarioController(
            ConsultarInventarioCompletoUseCase consultarInventarioCompletoUseCase,
            ConsultarInventarioCompletoMapper consultarInventarioCompletoMapper) {
        this.consultarInventarioCompletoUseCase = consultarInventarioCompletoUseCase;
        this.consultarInventarioCompletoMapper = consultarInventarioCompletoMapper;
    }

    @GetMapping
    public ResponseEntity<ConsultarInventarioCompletoResponse> consultarInventario() {
        var articulos = consultarInventarioCompletoUseCase.ejecutar();

        var response = consultarInventarioCompletoMapper.toResponse(articulos);

        return ResponseEntity.ok(response);
    }
}
