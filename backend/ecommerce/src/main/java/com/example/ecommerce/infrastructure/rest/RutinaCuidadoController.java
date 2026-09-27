package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.dto.request.CrearRutinaCuidadoRequest;
import com.example.ecommerce.application.dto.response.RutinaCuidadoResponse;
import com.example.ecommerce.application.usecase.CrearRutinaCuidadoUseCase;
import com.example.ecommerce.domain.entity.RutinaCuidado;
import com.example.ecommerce.infrastructure.rest.mapper.RutinaCuidadoMapper;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/rutinas")
public class RutinaCuidadoController {

    private final CrearRutinaCuidadoUseCase crearRutinaCuidadoUseCase;
    private final RutinaCuidadoMapper mapper;

    public RutinaCuidadoController(
            CrearRutinaCuidadoUseCase crearRutinaCuidadoUseCase,
            RutinaCuidadoMapper mapper) {

        this.crearRutinaCuidadoUseCase = crearRutinaCuidadoUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<RutinaCuidadoResponse> crear(
            @Valid @RequestBody CrearRutinaCuidadoRequest request) {

        RutinaCuidado rutina = crearRutinaCuidadoUseCase.ejecutar(
                1L,
                request.nombre(),
                request.articulosIds()
        );

        RutinaCuidadoResponse response = mapper.toResponse(rutina);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(rutina.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }
}
