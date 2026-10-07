package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.dto.request.LoginRequest;
import com.example.ecommerce.application.dto.response.UsuarioResponse;
import com.example.ecommerce.application.usecase.AutenticarUsuarioUseCase;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.infrastructure.rest.mapper.UsuarioMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;
    private final UsuarioMapper mapper;

    public AuthController(AutenticarUsuarioUseCase autenticarUsuarioUseCase, UsuarioMapper mapper) {
        this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponse> login(@Valid @RequestBody LoginRequest request) {

        Usuario usuario = autenticarUsuarioUseCase.ejecutar(
                request.email(),
                request.contrasena()
        );

        // Pendiente: devolver un token JWT en lugar del usuario (requisito del enunciado).
        return ResponseEntity.ok(mapper.toResponse(usuario));
    }
}
