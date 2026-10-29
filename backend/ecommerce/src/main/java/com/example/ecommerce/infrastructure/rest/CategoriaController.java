package com.example.ecommerce.infrastructure.rest;
import com.example.ecommerce.application.dto.request.CrearCategoriaRequest;
import com.example.ecommerce.application.dto.response.CrearCategoriaResponse;
import com.example.ecommerce.application.usecase.CrearCategoriaUseCase;
import com.example.ecommerce.domain.entity.Categoria;
import com.example.ecommerce.infrastructure.rest.mapper.CategoriaMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categorias")
public class CategoriaController {
    private final CrearCategoriaUseCase crearCategoriaUseCase;
    private final CategoriaMapper mapper;

    @PostMapping
    public ResponseEntity<CrearCategoriaResponse> crear(
            @Valid @RequestBody CrearCategoriaRequest request) {

        Categoria categoria = crearCategoriaUseCase.ejecutar(
                UUID.randomUUID().toString(),
                request.nombre()
        );

        CrearCategoriaResponse response = mapper.toResponse(categoria);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(categoria.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }
}
