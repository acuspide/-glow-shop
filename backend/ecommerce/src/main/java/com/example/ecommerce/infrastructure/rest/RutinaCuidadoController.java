package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
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
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rutinas")
public class RutinaCuidadoController {

    private final CrearRutinaCuidadoUseCase crearRutinaCuidadoUseCase;
    private final ObtenerRutinaCuidadoUseCase obtenerRutinaCuidadoUseCase;
    private final RutinaCuidadoMapper mapper;
    private final ActualizarRutinaCuidadoUseCase actualizarRutinaCuidadoUseCase;
    private final AgregarArticuloARutinaUseCase agregarArticuloARutinaUseCase;
    private final TokenService tokenService;

    @PostMapping
    public ResponseEntity<RutinaCuidadoResponse> crear(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @Valid @RequestBody CrearRutinaCuidadoRequest request) {

        RutinaCuidado rutina = crearRutinaCuidadoUseCase.ejecutar(
                UUID.randomUUID().toString(),
                administradorDelToken(autorizacion),
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
            @PathVariable String id) {

        RutinaCuidado rutina =
                obtenerRutinaCuidadoUseCase.ejecutar(id);

        RutinaCuidadoResponse response =
                mapper.toResponse(rutina);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RutinaCuidadoResponse> actualizar(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @PathVariable String id,
            @Valid @RequestBody ActualizarRutinaCuidadoRequest request) {

        RutinaCuidado rutina = actualizarRutinaCuidadoUseCase.ejecutar(
                id,
                administradorDelToken(autorizacion),
                request.nombre(),
                request.articulosIds()
        );

        RutinaCuidadoResponse response = mapper.toResponse(rutina);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/articulos/{articuloId}")
    public ResponseEntity<RutinaCuidadoResponse> agregarArticulo(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @PathVariable String id,
            @PathVariable String articuloId) {

        RutinaCuidado rutina = agregarArticuloARutinaUseCase
                .ejecutar(id, administradorDelToken(autorizacion), articuloId);

        RutinaCuidadoResponse response = mapper.toResponse(rutina);

        return ResponseEntity.ok(response);
    }

    // Crear, editar y agregar artículos a una rutina es solo para el administrador; ver una rutina es público.
    private String administradorDelToken(String autorizacion) {
        return tokenService.extraerUsuarioId(BearerToken.extraer(autorizacion));
    }
}
