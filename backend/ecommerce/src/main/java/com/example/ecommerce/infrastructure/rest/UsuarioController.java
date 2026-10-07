package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.dto.request.RegistrarClienteRequest;
import com.example.ecommerce.application.dto.response.UsuarioResponse;
import com.example.ecommerce.application.usecase.RegistrarClienteUseCase;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.infrastructure.rest.mapper.UsuarioMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final RegistrarClienteUseCase registrarClienteUseCase;
    private final UsuarioMapper mapper;

    // Temporal: mientras el repositorio sea en memoria. Con JPA/MariaDB lo genera la base de datos.
    private final AtomicLong siguienteId = new AtomicLong(1);

    public UsuarioController(RegistrarClienteUseCase registrarClienteUseCase, UsuarioMapper mapper) {
        this.registrarClienteUseCase = registrarClienteUseCase;
        this.mapper = mapper;
    }

    // Autorregistro público: siempre crea un CLIENTE.
    @PostMapping
    public ResponseEntity<UsuarioResponse> registrar(
            @Valid @RequestBody RegistrarClienteRequest request) {

        Usuario cliente = registrarClienteUseCase.ejecutar(
                siguienteId.getAndIncrement(),
                request.nombre(),
                request.email(),
                request.contrasena()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(cliente.getId())
                .toUri();

        return ResponseEntity.created(location).body(mapper.toResponse(cliente));
    }
}
