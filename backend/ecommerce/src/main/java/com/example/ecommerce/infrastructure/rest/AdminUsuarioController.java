package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.request.CrearUsuarioConRolRequest;
import com.example.ecommerce.application.dto.response.UsuarioResponse;
import com.example.ecommerce.application.usecase.CrearUsuarioPorAdministradorUseCase;
import com.example.ecommerce.application.usecase.ListarUsuariosUseCase;
import com.example.ecommerce.domain.valueobject.FiltroUsuarios;
import com.example.ecommerce.domain.valueobject.RolUsuario;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.infrastructure.rest.mapper.UsuarioMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/usuarios")
public class AdminUsuarioController {

    private final CrearUsuarioPorAdministradorUseCase crearUsuarioUseCase;
    private final ListarUsuariosUseCase listarUsuariosUseCase;
    private final TokenService tokenService;
    private final UsuarioMapper mapper;

    // Un administrador crea vendedores u otros administradores. Requiere "Authorization: Bearer <token>".
    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @Valid @RequestBody CrearUsuarioConRolRequest request) {

        String administradorId = tokenService.extraerUsuarioId(BearerToken.extraer(autorizacion));

        Usuario nuevo = crearUsuarioUseCase.ejecutar(
                administradorId,
                UUID.randomUUID().toString(),
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

    // CU-07: lista los usuarios. Filtros opcionales: ?rol=VENDEDOR&activo=true
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @RequestParam(required = false) RolUsuario rol,
            @RequestParam(required = false) Boolean activo) {

        String administradorId = tokenService.extraerUsuarioId(BearerToken.extraer(autorizacion));
        List<UsuarioResponse> usuarios = listarUsuariosUseCase
                .ejecutar(administradorId, new FiltroUsuarios(rol, activo)).stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(usuarios);
    }
}
