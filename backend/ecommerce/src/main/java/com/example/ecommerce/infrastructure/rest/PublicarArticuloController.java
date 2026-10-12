package com.example.ecommerce.infrastructure.rest;
import com.example.ecommerce.application.dto.request.PublicarArticuloRequest;
import com.example.ecommerce.application.dto.response.PublicarArticuloResponse;
import com.example.ecommerce.application.usecase.PublicarArticuloUseCase;
import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.infrastructure.rest.mapper.PublicarArticuloMapper;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/articulos")
public class PublicarArticuloController {
    private final PublicarArticuloUseCase publicarArticuloUseCase;
    private final PublicarArticuloMapper mapper;

    public PublicarArticuloController(
            PublicarArticuloUseCase publicarArticuloUseCase,
            PublicarArticuloMapper mapper) {

        this.publicarArticuloUseCase = publicarArticuloUseCase;
        this.mapper = mapper;
    }
    @PostMapping
    public ResponseEntity<PublicarArticuloResponse> publicar(
            @Valid @RequestBody PublicarArticuloRequest request) {

        Articulo articulo = mapper.toEntity(request);

        Articulo articuloPublicado =
                publicarArticuloUseCase.ejecutar(articulo);

        PublicarArticuloResponse response =
                mapper.toResponse(articuloPublicado);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(articuloPublicado.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }
}
