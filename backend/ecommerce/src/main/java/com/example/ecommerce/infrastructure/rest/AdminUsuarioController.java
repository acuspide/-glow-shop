package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.request.CrearUsuarioConRolRequest;
import com.example.ecommerce.application.dto.response.UsuarioResponse;
import com.example.ecommerce.application.usecase.CrearUsuarioPorAdministradorUseCase;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.TokenInvalidoException;
import com.example.ecommerce.infrastructure.persistence.SecuenciaIdUsuario;
import com.example.ecommerce.infrastructure.rest.mapper.UsuarioMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/admin/usuarios")
public class AdminUsuarioController {

    private static final String PREFIJO_BEARER = "Bearer ";

    private final CrearUsuarioPorAdministradorUseCase crearUsuarioUseCase;
    private final TokenService tokenService;
    private final UsuarioMapper mapper;
    private final SecuenciaIdUsuario secuenciaId;

    public AdminUsuarioController(CrearUsuarioPorAdministradorUseCase crearUsuarioUseCase,
                                  TokenService tokenService,
                                  UsuarioMapper mapper,
                                  SecuenciaIdUsuario secuenciaId) {
        this.crearUsuarioUseCase = crearUsuarioUseCase;
        this.tokenService = tokenService;
        this.mapper = mapper;
        this.secuenciaId = secuenciaId;
    }

    // Un administrador crea vendedores u otros administradores. Requiere "Authorization: Bearer <token>".
    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @Valid @RequestBody CrearUsuarioConRolRequest request) {

        long administradorId = tokenService.extraerUsuarioId(extraerToken(autorizacion));

        Usuario nuevo = crearUsuarioUseCase.ejecutar(
                administradorId,
                secuenciaId.siguiente(),
                request.nombre(),
                request.email(),
                request.contrasena(),
                request.telefono(),
                request.fechaNacimiento(),
                request.rol()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(nuevo.getId())
                .toUri();

        return ResponseEntity.created(location).body(mapper.toResponse(nuevo));
    }

    private String extraerToken(String autorizacion) {
        if (autorizacion == null || !autorizacion.startsWith(PREFIJO_BEARER)) {
            throw new TokenInvalidoException();
        }
        return autorizacion.substring(PREFIJO_BEARER.length()).trim();
    }
}
