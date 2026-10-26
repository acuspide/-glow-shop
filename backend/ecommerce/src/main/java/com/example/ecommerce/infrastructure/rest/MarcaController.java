package com.example.ecommerce.infrastructure.rest;
import com.example.ecommerce.application.dto.request.CrearMarcaRequest;
import com.example.ecommerce.application.dto.response.CrearMarcaResponse;
import com.example.ecommerce.application.usecase.CrearMarcaUseCase;
import com.example.ecommerce.domain.entity.Marca;
import com.example.ecommerce.infrastructure.rest.mapper.MarcaMapper;
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
@RequestMapping("/api/marcas")
public class MarcaController {
    private final CrearMarcaUseCase crearMarcaUseCase;
    private final MarcaMapper mapper;

    @PostMapping
    public ResponseEntity<CrearMarcaResponse> crear(
            @Valid @RequestBody CrearMarcaRequest request) {

        Marca marca = crearMarcaUseCase.ejecutar(
                UUID.randomUUID().toString(),
                request.nombre()
        );

        CrearMarcaResponse response = mapper.toResponse(marca);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(marca.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }
}
