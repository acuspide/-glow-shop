package com.example.ecommerce.infrastructure.rest;
import com.example.ecommerce.application.dto.response.ListarRutinasResponse;
import com.example.ecommerce.application.usecase.ListarRutinasUseCase;
import com.example.ecommerce.infrastructure.rest.mapper.ListarRutinasMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rutinas")
public class ListarRutinasController {
    private final ListarRutinasUseCase listarRutinasUseCase;
    private final ListarRutinasMapper listarRutinasMapper;

    @GetMapping
    public ResponseEntity<ListarRutinasResponse> listarRutinas() {
        var rutinas = listarRutinasUseCase.ejecutar();
        var response = listarRutinasMapper.toResponse(rutinas);

        return ResponseEntity.ok(response);
    }
}
