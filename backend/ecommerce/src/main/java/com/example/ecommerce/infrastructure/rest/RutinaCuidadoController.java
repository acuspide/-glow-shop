package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.dto.request.CrearRutinaCuidadoRequest;
import com.example.ecommerce.application.dto.response.RutinaCuidadoResponse;
import com.example.ecommerce.application.usecase.CrearRutinaCuidadoUseCase;
import com.example.ecommerce.domain.entity.RutinaCuidado;
import com.example.ecommerce.infrastructure.rest.mapper.RutinaCuidadoMapper;
import com.example.ecommerce.application.usecase.ObtenerRutinaCuidadoUseCase;
import com.example.ecommerce.application.dto.request.ActualizarRutinaCuidadoRequest;
import com.example.ecommerce.application.usecase.ActualizarRutinaCuidadoUseCase;
import com.example.ecommerce.application.usecase.AgregarArticuloARutinaUseCase;
import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rutinas")
public class RutinaCuidadoController {

    private final CrearRutinaCuidadoUseCase crearRutinaCuidadoUseCase;
    private final ObtenerRutinaCuidadoUseCase obtenerRutinaCuidadoUseCase;
    private final RutinaCuidadoMapper mapper;
    private final ActualizarRutinaCuidadoUseCase actualizarRutinaCuidadoUseCase;
    private final AgregarArticuloARutinaUseCase agregarArticuloARutinaUseCase;

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
    @GetMapping("/{id}")
    public ResponseEntity<RutinaCuidadoResponse> obtener(
            @PathVariable long id) {

        RutinaCuidado rutina =
                obtenerRutinaCuidadoUseCase.ejecutar(id);

        RutinaCuidadoResponse response =
                mapper.toResponse(rutina);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RutinaCuidadoResponse> actualizar(
            @PathVariable long id,
            @Valid @RequestBody ActualizarRutinaCuidadoRequest request) {

        RutinaCuidado rutina = actualizarRutinaCuidadoUseCase.ejecutar(
                id,
                request.nombre(),
                request.articulosIds()
        );

        RutinaCuidadoResponse response = mapper.toResponse(rutina);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/articulos/{articuloId}")
    public ResponseEntity<RutinaCuidadoResponse> agregarArticulo(
            @PathVariable long id,
            @PathVariable long articuloId) {

        RutinaCuidado rutina = agregarArticuloARutinaUseCase
                .ejecutar(id, articuloId);

        RutinaCuidadoResponse response = mapper.toResponse(rutina);

        return ResponseEntity.ok(response);
    }

}
